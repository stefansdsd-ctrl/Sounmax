package com.example.ai

import android.content.Context
import com.example.BuildConfig
import com.example.data.OfflineGuard
import com.example.dsp.EqPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiAcousticRecommendation(
    val presetName: String,
    val description: String,
    val eqPreset: EqPreset,
    val ancRecommendation: String,
    val codecRecommendation: String,
    val acousticInsight: String
)

class GeminiAudioTuner {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val models = listOf(
        "gemini-2.5-flash",
        "gemini-2.0-flash"
    )

    suspend fun generateAcousticProfile(
        userPrompt: String,
        headphoneModel: String,
        musicGenre: String = "YouTube Music",
        context: Context? = null,
        allowMetered: Boolean = false
    ): Result<AiAcousticRecommendation> = withContext(Dispatchers.IO) {
        try {
            if (context != null) {
                val decision = TunerNet.shouldAllowCloud(context, force = allowMetered)
                if (!decision.allow || OfflineGuard.blockCloud(context)) {
                    val why = if (!decision.allow) decision.reason else "offline"
                    return@withContext Result.success(skipCloud(context, userPrompt, headphoneModel, musicGenre, why))
                }
            }
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.success(getSmartFallback(userPrompt, headphoneModel, musicGenre))
            }

            val body = requestBody(userPrompt, headphoneModel, musicGenre)
            var lastCode = 0
            for (model in models) {
                val attempt = call(model, apiKey, body)
                lastCode = attempt.code
                if (attempt.text != null) {
                    return@withContext Result.success(parse(attempt.text, userPrompt, headphoneModel, musicGenre))
                }
                if (attempt.code == 429 || attempt.code in 500..599) {
                    delay(800)
                    val retry = call(model, apiKey, body)
                    lastCode = retry.code
                    if (retry.text != null) {
                        return@withContext Result.success(parse(retry.text, userPrompt, headphoneModel, musicGenre))
                    }
                }
            }
            val local = if (context != null) skipCloud(context, userPrompt, headphoneModel, musicGenre, "cloud $lastCode")
            else getSmartFallback(userPrompt, headphoneModel, musicGenre)
            Result.success(local)
        } catch (e: Exception) {
            val local = if (context != null) skipCloud(context, userPrompt, headphoneModel, musicGenre, "fout")
            else getSmartFallback(userPrompt, headphoneModel, musicGenre)
            Result.success(local)
        }
    }

    private fun skipCloud(
        context: Context,
        prompt: String,
        headphone: String,
        genre: String,
        why: String
    ): AiAcousticRecommendation {
        val cached = AiCurveCache.best(context, prompt)
        if (cached != null) {
            return cached.copy(
                description = "Cache · $why. " + cached.description,
                acousticInsight = "Cloud overgeslagen ($why). Laatste Gemini-curve hergebruikt."
            )
        }
        val local = getSmartFallback(prompt, headphone, genre)
        return local.copy(
            description = "Lokale curve · $why. " + local.description,
            acousticInsight = "Cloud overgeslagen ($why). Geen cache."
        )
    }

    private fun requestBody(userPrompt: String, headphoneModel: String, musicGenre: String): String {
        val systemInstruction = """
            Je bent een Master Sound Engineer gespecialiseerd in Bluetooth ANC-hoofdtelefoons (Philips TAH6519).
            Genereer een precieze 10-bands EQ (31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000 Hz) tussen -10.0 en +10.0 dB.
            Houd rekening met closed-back ANC karakteristieken. Geef altijd geldige JSON.
            Antwoord ALLEEN als JSON:
            {"presetName":"","description":"","bandGains":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0],"bassBoost":400,"virtualizer":350,"loudness":450,"clarity":8.0,"ancRecommendation":"","codecRecommendation":"","acousticInsight":""}
        """.trimIndent()
        return JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Koptelefoon: $headphoneModel. Genre/context: $musicGenre. Verzoek: $userPrompt")
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemInstruction) })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.45)
            })
        }.toString()
    }

    private data class Call(val code: Int, val text: String?)

    private fun call(model: String, apiKey: String, json: String): Call {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return Call(response.code, null)
            val raw = response.body?.string().orEmpty()
            val root = JSONObject(raw)
            val text = root.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
                .orEmpty()
            return Call(response.code, text.ifBlank { null })
        }
    }

    private fun parse(
        text: String,
        userPrompt: String,
        headphoneModel: String,
        musicGenre: String
    ): AiAcousticRecommendation {
        return try {
            val cleaned = text.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleaned)
            val gains = mutableListOf<Float>()
            val arr = parsed.optJSONArray("bandGains")
            if (arr != null) {
                for (i in 0 until minOf(10, arr.length())) gains.add(arr.optDouble(i, 0.0).toFloat().coerceIn(-10f, 10f))
            }
            while (gains.size < 10) gains.add(0f)
            val name = parsed.optString("presetName", "AI-curve")
            val description = parsed.optString("description", "Aangepaste curve.")
            AiAcousticRecommendation(
                presetName = name,
                description = description,
                eqPreset = EqPreset(
                    name = name,
                    bandGains = gains,
                    bassBoost = parsed.optInt("bassBoost", 400),
                    virtualizer = parsed.optInt("virtualizer", 350),
                    loudness = parsed.optInt("loudness", 450),
                    clarity = parsed.optDouble("clarity", 8.0).toFloat(),
                    isCustom = true,
                    description = description
                ),
                ancRecommendation = parsed.optString("ancRecommendation", "ANC adaptief"),
                codecRecommendation = parsed.optString("codecRecommendation", "LDAC of AAC HQ"),
                acousticInsight = parsed.optString("acousticInsight", "Volume onder veilige dosis houden.")
            )
        } catch (_: Exception) {
            getSmartFallback(userPrompt, headphoneModel, musicGenre)
        }
    }

    private fun getSmartFallback(
        prompt: String,
        headphone: String,
        genre: String
    ): AiAcousticRecommendation {
        val lower = prompt.lowercase()
        val base = when {
            lower.contains("hip") || lower.contains("hop") || lower.contains("trap") || lower.contains("rap") || lower.contains("drill") ->
                HarmanTargetEq.hipHopPreset().copy(name = "AI Hip-Hop")
            lower.contains("rock") || lower.contains("metal") || lower.contains("hardrock") || lower.contains("guitar") || lower.contains("riff") ->
                HarmanTargetEq.rockPreset().copy(name = "AI Rock")
            lower.contains("pop") || lower.contains("chart") || lower.contains("dance") || lower.contains("hit") ->
                HarmanTargetEq.popPreset().copy(name = "AI Pop")
            lower.contains("bass") || lower.contains("sub") || lower.contains("kick") || lower.contains("edm") ->
                HarmanTargetEq.warmPreset().copy(name = "AI Warm Bass")
            lower.contains("vocal") || lower.contains("stem") || lower.contains("podcast") || lower.contains("spraak") || lower.contains("talk") ->
                HarmanTargetEq.podcastPreset().copy(name = "AI Podcast")
            lower.contains("jazz") || lower.contains("acoustic") || lower.contains("akoest") || lower.contains("piano") || lower.contains("sax") ->
                HarmanTargetEq.jazzPreset().copy(name = "AI Jazz")
            lower.contains("helder") || lower.contains("clarity") || lower.contains("detail") || lower.contains("klassiek") ->
                HarmanTargetEq.clarityPreset().copy(name = "AI Clarity")
            lower.contains("film") || lower.contains("movie") || lower.contains("cinema") || lower.contains("spatial") || lower.contains("atmos") ->
                HarmanTargetEq.moviePreset().copy(name = "AI Movie")
            lower.contains("game") || lower.contains("gaming") || lower.contains("footstep") || lower.contains("fps") ->
                HarmanTargetEq.gamingPreset().copy(name = "AI Gaming")
            lower.contains("nacht") || lower.contains("night") || lower.contains("zacht") || lower.contains("soft") || lower.contains("slaap") ->
                HarmanTargetEq.neutralPreset().copy(name = "AI Soft", bandGains = HarmanTargetEq.neutralPreset().bandGains.map { it * 0.6f })
            else -> HarmanTargetEq.neutralPreset().copy(name = "AI Harman")
        }
        val anc = when {
            lower.contains("bass") || lower.contains("film") || lower.contains("movie") || lower.contains("rock") || lower.contains("metal") || lower.contains("hip") || lower.contains("trap") -> "ANC maximaal"
            lower.contains("podcast") || lower.contains("spraak") -> "ANC laag of uit"
            lower.contains("jazz") || lower.contains("acoustic") -> "ANC medium"
            else -> "ANC adaptief"
        }
        return AiAcousticRecommendation(
            presetName = base.name,
            description = "Lokale Harman-gebaseerde curve voor $headphone ($genre).",
            eqPreset = base,
            ancRecommendation = anc,
            codecRecommendation = "LDAC of AAC HQ",
            acousticInsight = "Gebaseerd op verfijnde Harman-target voor closed-back ANC."
        )
    }
}
