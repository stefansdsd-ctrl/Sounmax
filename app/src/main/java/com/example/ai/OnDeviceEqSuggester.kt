package com.example.ai

import android.content.Context
import com.example.dsp.EqPreset
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.generationConfig
import com.google.mlkit.genai.prompt.modelConfig
import com.google.mlkit.genai.prompt.ModelPreference
import com.google.mlkit.genai.prompt.ModelReleaseStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * On-device EQ suggestions via ML Kit Gemini Nano (when available).
 * Falls back gracefully if Nano not supported on device.
 */
class OnDeviceEqSuggester(private val context: Context) {

    private val model by lazy {
        try {
            val config = generationConfig {
                modelConfig = modelConfig {
                    releaseStage = ModelReleaseStage.PREVIEW
                    preference = ModelPreference.FAST
                }
            }
            Generation.getClient(config)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun suggest(
        userPrompt: String,
        headphoneModel: String,
        musicGenre: String
    ): AiAcousticRecommendation? = withContext(Dispatchers.Default) {
        val nano = model ?: return@withContext null
        try {
            val prompt = """
                Je bent een audio-ingenieur. Genereer een 10-bands EQ (31-16000 Hz) voor $headphoneModel.
                Genre/context: $musicGenre. Verzoek: $userPrompt.
                Antwoord ALLEEN JSON:
                {"presetName":"","description":"","bandGains":[0.0,...],"ancRecommendation":"","acousticInsight":""}
            """.trimIndent()

            val result = nano.generateContent(prompt)
            val text = result.text ?: return@withContext null
            parseNano(text, userPrompt, headphoneModel, musicGenre)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseNano(
        text: String,
        userPrompt: String,
        headphone: String,
        genre: String
    ): AiAcousticRecommendation? {
        return try {
            val cleaned = text.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleaned)
            val gains = mutableListOf<Float>()
            val arr = parsed.optJSONArray("bandGains")
            if (arr != null) {
                for (i in 0 until minOf(10, arr.length())) {
                    gains.add(arr.optDouble(i, 0.0).toFloat().coerceIn(-10f, 10f))
                }
            }
            while (gains.size < 10) gains.add(0f)
            val name = parsed.optString("presetName", "Nano EQ")
            val desc = parsed.optString("description", "On-device suggestie.")
            AiAcousticRecommendation(
                presetName = name,
                description = desc,
                eqPreset = EqPreset(
                    name = name,
                    bandGains = gains,
                    bassBoost = 400,
                    virtualizer = 350,
                    loudness = 450,
                    clarity = 8.0f,
                    isCustom = true,
                    description = desc
                ),
                ancRecommendation = parsed.optString("ancRecommendation", "ANC adaptief"),
                codecRecommendation = "LDAC of AAC",
                acousticInsight = parsed.optString("acousticInsight", "On-device Gemini Nano.")
            )
        } catch (_: Exception) {
            null
        }
    }
}
