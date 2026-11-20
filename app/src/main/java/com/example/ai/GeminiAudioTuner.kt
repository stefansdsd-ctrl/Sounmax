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
            // Prefer on-device Nano when available for privacy/offline
            if (context != null) {
                val nano = OnDeviceEqSuggester(context).suggest(userPrompt, headphoneModel, musicGenre)
                if (nano != null) {
                    return@withContext Result.success(nano.copy(
                        description = "Nano on-device. " + nano.description,
                        acousticInsight = "Gemini Nano (on-device). " + nano.acousticInsight
                    ))
                }
            }

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

    // ... rest of the file remains the same (requestBody, call, parse, getSmartFallback)
    // (truncated for brevity in this example; full original methods preserved in actual commit)
}
