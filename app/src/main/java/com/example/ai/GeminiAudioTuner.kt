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
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
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
            if (context != null && TunerNet.portal(context)) {
                return@withContext Result.success(skipCloud(context, userPrompt, headphoneModel, musicGenre, "portal"))
            }
            if (context != null && (OfflineGuard.blockCloud(context) || !TunerNet.validated(context))) {
                return@withContext Result.success(skipCloud(context, userPrompt, headphoneModel, musicGenre, "offline"))
            }
            if (context != null && TunerNet.slow(context) && !allowMetered) {
                return@withContext Result.success(skipCloud(context, userPrompt, headphoneModel, musicGenre, "traag net"))
            }
            if (context != null && TunerNet.metered(context) && !allowMetered) {
                return@withContext Result.success(skipCloud(context, userPrompt, headphoneModel, musicGenre, "mobiel data"))
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
                    delay(700)
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
            Je bent een Master Sound Engineer voor Bluetooth-hoofdtelefoons (Philips TAH6519 en vergelijkbaar).
            Genereer een 10-bands EQ (31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz) tussen -12.0 en +12.0 dB.
            Antwoord ALLEEN als JSON zonder markdown:
            {"presetName":"","description":"","bandGains":[0,0,0,0,0,0,0,0,0,0],"bassBoost":500,"virtualizer":400,"loudness":500,"clarity":7.5,"ancRecommendation":"","codecRecommendation":"","acousticInsight":""}
        """.trimIndent()
        return JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Koptelefoon: $headphoneModel. Context: $musicGenre. Verzoek: $userPrompt")
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
                put("temperature", 0.6)
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
                for (i in 0 until minOf(10, arr.length())) gains.add(arr.optDouble(i, 0.0).toFloat().coerceIn(-12f, 12f))
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
                    bassBoost = parsed.optInt("bassBoost", 500),
                    virtualizer = parsed.optInt("virtualizer", 400),
                    loudness = parsed.optInt("loudness", 450),
                    clarity = parsed.optDouble("clarity", 7.0).toFloat(),
                    isCustom = true,
                    description = description
                ),
                ancRecommendation = parsed.optString("ancRecommendation", "ANC adaptief"),
                codecRecommendation = parsed.optString("codecRecommendation", "LDAC of AAC HQ"),
                acousticInsight = parsed.optString("acousticInsight", "Houd het volume onder een veilige dosis.")
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
        return when {
            lower.contains("bass") || lower.contains("hard") || lower.contains("sub") || lower.contains("kick") -> {
                AiAcousticRecommendation(
                    presetName = "AI Hyper-Bass",
                    description = "Sub-bass (31-125Hz) met mid-cut tegen modder op $headphone.",
                    eqPreset = EqPreset(
                        name = "AI Hyper-Bass",
                        bandGains = listOf(8.0f, 6.5f, 4.0f, 0.5f, -1.5f, 0.0f, 2.0f, 3.5f, 4.0f, 4.5f),
                        bassBoost = 850,
                        virtualizer = 350,
                        loudness = 650,
                        clarity = 7.0f,
                        isCustom = true,
                        description = "Diepe bas, strakke kick."
                    ),
                    ancRecommendation = "ANC maximaal voor schone sub.",
                    codecRecommendation = "LDAC 990 kbps.",
                    acousticInsight = "31Hz omhoog, 500Hz licht omlaag."
                )
            }
            lower.contains("vocal") || lower.contains("stem") || lower.contains("podcast") || lower.contains("helder") -> {
                AiAcousticRecommendation(
                    presetName = "AI Vocal",
                    description = "1-4 kHz aanwezigheid voor stem en podcast.",
                    eqPreset = EqPreset(
                        name = "AI Vocal",
                        bandGains = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.5f, 5.0f, 4.5f, 3.0f, 2.5f, 2.0f),
                        bassBoost = 200,
                        virtualizer = 300,
                        loudness = 500,
                        clarity = 9.5f,
                        isCustom = true,
                        description = "Stem naar voren."
                    ),
                    ancRecommendation = "ANC adaptief.",
                    codecRecommendation = "AAC HQ of LDAC.",
                    acousticInsight = "250Hz neutraal houdt stemmen niet hol."
                )
            }
            else -> {
                AiAcousticRecommendation(
                    presetName = "AI Harman Plus",
                    description = "Gebalanceerde curve voor $headphone ($genre).",
                    eqPreset = EqPreset(
                        name = "AI Harman Plus",
                        bandGains = listOf(4.0f, 3.5f, 2.0f, 0.5f, 0.0f, 1.5f, 3.0f, 3.5f, 3.0f, 2.0f),
                        bassBoost = 550,
                        virtualizer = 450,
                        loudness = 500,
                        clarity = 8.0f,
                        isCustom = true,
                        description = "Universele curve."
                    ),
                    ancRecommendation = "ANC sterk.",
                    codecRecommendation = "LDAC indien de headset het doet.",
                    acousticInsight = "Harman-achtig, gesloten ANC-behuizing."
                )
            }
        }
    }
}
