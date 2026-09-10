package com.example.ai

import android.content.Context
import com.example.dsp.EqPreset
import org.json.JSONArray
import org.json.JSONObject

/** Cache laatste 3 AI-EQ-curves voor offline hergebruik. */
object AiCurveCache {
    private const val PREFS = "sounmax_ai_cache"
    private const val KEY = "curves"
    private const val MAX = 3

    fun remember(context: Context, rec: AiAcousticRecommendation) {
        val list = loadRaw(context)
        list.add(0, toJson(rec))
        while (list.length() > MAX) list.remove(list.length() - 1)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, list.toString()).apply()
    }

    fun lastThree(context: Context): List<AiAcousticRecommendation> {
        val list = loadRaw(context)
        return (0 until list.length()).mapNotNull { i -> fromJson(list.optJSONObject(i)) }
    }

    fun latest(context: Context): AiAcousticRecommendation? = lastThree(context).firstOrNull()

    private fun loadRaw(context: Context): JSONArray {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]")
        return runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
    }

    private fun toJson(rec: AiAcousticRecommendation): JSONObject = JSONObject().apply {
        put("presetName", rec.presetName)
        put("description", rec.description)
        put("ancRecommendation", rec.ancRecommendation)
        put("codecRecommendation", rec.codecRecommendation)
        put("acousticInsight", rec.acousticInsight)
        put("bassBoost", rec.eqPreset.bassBoost)
        put("virtualizer", rec.eqPreset.virtualizer)
        put("loudness", rec.eqPreset.loudness)
        put("clarity", rec.eqPreset.clarity.toDouble())
        put("bandGains", JSONArray().also { arr -> rec.eqPreset.bandGains.forEach { arr.put(it.toDouble()) } })
    }

    private fun fromJson(obj: JSONObject?): AiAcousticRecommendation? {
        obj ?: return null
        val gainsArr = obj.optJSONArray("bandGains") ?: return null
        val gains = FloatArray(gainsArr.length()) { i -> gainsArr.optDouble(i).toFloat() }
        val preset = EqPreset(
            name = obj.optString("presetName", "Offline AI"),
            bandGains = gains,
            bassBoost = obj.optInt("bassBoost", 500),
            virtualizer = obj.optInt("virtualizer", 400),
            loudness = obj.optInt("loudness", 450),
            clarity = obj.optDouble("clarity", 7.0).toFloat(),
            isCustom = true,
            description = obj.optString("description")
        )
        return AiAcousticRecommendation(
            presetName = preset.name,
            description = preset.description,
            eqPreset = preset,
            ancRecommendation = obj.optString("ancRecommendation"),
            codecRecommendation = obj.optString("codecRecommendation"),
            acousticInsight = obj.optString("acousticInsight") + " (offline cache)"
        )
    }
}
