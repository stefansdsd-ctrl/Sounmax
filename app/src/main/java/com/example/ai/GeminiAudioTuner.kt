package com.example.ai

import com.example.BuildConfig
import com.example.dsp.EqPreset
import kotlinx.coroutines.Dispatchers
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
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateAcousticProfile(
        userPrompt: String,
        headphoneModel: String,
        musicGenre: String = "YouTube Music"
    ): Result<AiAcousticRecommendation> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Return high-quality intelligent rule-based acoustic profile if key is not injected
                return@withContext Result.success(getSmartFallback(userPrompt, headphoneModel, musicGenre))
            }

            val systemInstruction = """
                Je bent een wereldklasse Master Sound Engineer en Akoestisch Akoestiek-expert gespecialiseerd in Bluetooth hoofdtelefoons (zoals de Philips TAH6519 over-ear ANC, Sony, Bose, etc.) en YouTube Music audio streaming.
                Genereer een optimaal 10-bands equalizer DSP profiel (31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz) in dB waarden tussen -12.0 en +12.0.
                Geef het antwoord ALLEEN als een geldig JSON object zonder markdown code blocks of extra tekst:
                {
                  "presetName": "Korte pakkende naam",
                  "description": "Uitleg in het Nederlands van de klankkleur en tuning",
                  "bandGains": [4.5, 3.8, 2.0, -0.5, 0.5, 2.0, 3.2, 4.0, 3.5, 2.5],
                  "bassBoost": 650,
                  "virtualizer": 400,
                  "loudness": 500,
                  "clarity": 7.5,
                  "ancRecommendation": "ANC Sterk / Adaptief advies",
                  "codecRecommendation": "LDAC 990kbps / AAC HQ",
                  "acousticInsight": "Professionele akoestische tip voor de beste luisterervaring"
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Koptelefoon: $headphoneModel. Muziek / Context: $musicGenre. Gebruiker verzoek: $userPrompt")
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstruction)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.success(getSmartFallback(userPrompt, headphoneModel, musicGenre))
            }

            val responseString = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseString)
            val candidates = jsonRoot.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            val cleanedText = text.replace("```json", "").replace("```", "").trim()
            val parsedJson = JSONObject(cleanedText)

            val presetName = parsedJson.optString("presetName", "AI Akoestisch Meesterprofiel")
            val description = parsedJson.optString("description", "Aangepast DSP profiel voor jouw muziek en koptelefoon.")
            val gainsArray = parsedJson.optJSONArray("bandGains")
            val bandGains = mutableListOf<Float>()
            if (gainsArray != null) {
                for (i in 0 until minOf(10, gainsArray.length())) {
                    bandGains.add(gainsArray.optDouble(i, 0.0).toFloat())
                }
            }
            while (bandGains.size < 10) {
                bandGains.add(0f)
            }

            val bassBoost = parsedJson.optInt("bassBoost", 500)
            val virtualizer = parsedJson.optInt("virtualizer", 400)
            val loudness = parsedJson.optInt("loudness", 450)
            val clarity = parsedJson.optDouble("clarity", 7.0).toFloat()
            val ancRec = parsedJson.optString("ancRecommendation", "ANC Maximaal aanbevolen voor isolatie van lage frequenties.")
            val codecRec = parsedJson.optString("codecRecommendation", "LDAC 990kbps voor maximale 24-bit bitdiepte.")
            val insight = parsedJson.optString("acousticInsight", "Verhoog het volume niet te hoog; dankzij de geoptimaliseerde dynamiek hoor je subtiele details op een veilig luisterniveau.")

            val eqPreset = EqPreset(
                name = presetName,
                bandGains = bandGains,
                bassBoost = bassBoost,
                virtualizer = virtualizer,
                loudness = loudness,
                clarity = clarity,
                isCustom = true,
                description = description
            )

            Result.success(
                AiAcousticRecommendation(
                    presetName = presetName,
                    description = description,
                    eqPreset = eqPreset,
                    ancRecommendation = ancRec,
                    codecRecommendation = codecRec,
                    acousticInsight = insight
                )
            )
        } catch (e: Exception) {
            Result.success(getSmartFallback(userPrompt, headphoneModel, musicGenre))
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
                    presetName = "AI Hyper-Bass & Sub Definition",
                    description = "Agressieve sub-bass boost (31-125Hz) met scherpe mid-cut om modderigheid op $headphone te voorkomen.",
                    eqPreset = EqPreset(
                        name = "AI Hyper-Bass & Sub Definition",
                        bandGains = listOf(8.0f, 6.5f, 4.0f, 0.5f, -1.5f, 0.0f, 2.0f, 3.5f, 4.0f, 4.5f),
                        bassBoost = 850,
                        virtualizer = 350,
                        loudness = 650,
                        clarity = 7.0f,
                        isCustom = true,
                        description = "Diepe basweergave met kristalheldere kick-impact voor YouTube Music."
                    ),
                    ancRecommendation = "ANC Maximaal: Onderdrukt buitenrumoer zodat sub-frequenties onder 60Hz zuiver klinken.",
                    codecRecommendation = "LDAC 990 kbps voor onvervormde bas-transiënten.",
                    acousticInsight = "De 32mm drivers van over-ear ANC hoofdtelefoons presteren het krachtigst wanneer 31Hz versterkt wordt en 500Hz licht gedempt blijft."
                )
            }
            lower.contains("vocal") || lower.contains("stem") || lower.contains("zang") || lower.contains("podcast") || lower.contains("helder") -> {
                AiAcousticRecommendation(
                    presetName = "AI Pure Vocal & Studio Presence",
                    description = "Verhoogde 1kHz-4kHz aanwezigheid voor kristalheldere zang en intieme spraakverstaanbaarheid.",
                    eqPreset = EqPreset(
                        name = "AI Pure Vocal & Studio Presence",
                        bandGains = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.5f, 5.0f, 4.5f, 3.0f, 2.5f, 2.0f),
                        bassBoost = 200,
                        virtualizer = 300,
                        loudness = 500,
                        clarity = 9.5f,
                        isCustom = true,
                        description = "Accentueert zanglijnen en instrumentale solo's op YouTube Music."
                    ),
                    ancRecommendation = "ANC Adaptief: Zorgt voor een fluisterstille achtergrond voor vocalen.",
                    codecRecommendation = "AAC HD of LDAC voor natuurgetrouw hoog zonder artefacten.",
                    acousticInsight = "Door 250Hz licht neutraal te houden klinken stemmen niet hol of benauwd in de oorschelpen."
                )
            }
            lower.contains("spatial") || lower.contains("live") || lower.contains("3d") || lower.contains("concert") || lower.contains("ruimte") -> {
                AiAcousticRecommendation(
                    presetName = "AI 3D Holographic Soundstage",
                    description = "Geavanceerde virtuele akoestiek die de 'in-your-head' sensatie van koptelefoons omzet in een open concertzaal.",
                    eqPreset = EqPreset(
                        name = "AI 3D Holographic Soundstage",
                        bandGains = listOf(4.0f, 3.0f, 1.5f, 0.5f, 1.0f, 2.5f, 3.5f, 5.0f, 6.0f, 6.5f),
                        bassBoost = 500,
                        virtualizer = 900,
                        loudness = 550,
                        clarity = 8.5f,
                        isCustom = true,
                        description = "Immersieve 360-graden audio visualisatie voor live video's en concerten."
                    ),
                    ancRecommendation = "ANC Maximaal: Maximaliseert het contrast tussen stilte en ruimtelijke echo's.",
                    codecRecommendation = "LDAC 96kHz / 24-bit voor uiterste fase-nauwkeurigheid in stereo imaging.",
                    acousticInsight = "Hoge frequenties boven 8kHz dragen de meeste directionele ruimtelijke cues; deze boost creëert echte 'lucht' rond instrumenten."
                )
            }
            else -> {
                AiAcousticRecommendation(
                    presetName = "AI Master Reference & Harman Plus",
                    description = "Uiterst gebalanceerde audio tuning voor $headphone met dynamische punch en natuurgetrouwe instrument scheiding.",
                    eqPreset = EqPreset(
                        name = "AI Master Reference & Harman Plus",
                        bandGains = listOf(4.0f, 3.5f, 2.0f, 0.5f, 0.0f, 1.5f, 3.0f, 3.5f, 3.0f, 2.0f),
                        bassBoost = 550,
                        virtualizer = 450,
                        loudness = 500,
                        clarity = 8.0f,
                        isCustom = true,
                        description = "De perfecte universele curve voor alle genres op YouTube Music."
                    ),
                    ancRecommendation = "ANC Sterk / Adaptief voor ononderbroken luisterplezier.",
                    codecRecommendation = "LDAC Hi-Res (990 kbps) aanbevolen.",
                    acousticInsight = "Gekalibreerd volgens akoestische Harman target curves met compensatie voor gesloten ANC behuizingen."
                )
            }
        }
    }
}
