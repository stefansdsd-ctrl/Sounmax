package com.example.media

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.MicRmsProbe
import com.example.dsp.SceneLookup
import com.google.android.gms.location.DetectedActivity

/**
 * Lopen/fietsen + hoge mic-RMS → wind-scene (WIND_GUARD).
 * Opt-in via prefs wind_detect_enabled (default aan).
 */
object WindAdvisor {
    const val KEY_ENABLED = "wind_detect_enabled"
    const val KEY_LAST_REASON = "wind_detect_reason"
    private const val RMS_THRESHOLD = 0.62f

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun suggest(context: Context, current: ListeningScene): ListeningScene? {
        if (!enabled(context)) return null
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val type = prefs.getInt("last_activity_type", -1)
        val outdoor = type == DetectedActivity.WALKING ||
            type == DetectedActivity.ON_FOOT ||
            type == DetectedActivity.ON_BICYCLE ||
            type == DetectedActivity.RUNNING
        if (!outdoor) return null

        if (!MicRmsProbe.enabled(context, SceneAutomation.PREFS)) return null
        val intensity = MicRmsProbe.sample(context) ?: MicRmsProbe.lastIntensity
        if (intensity < RMS_THRESHOLD) return null

        val sceneId = when (type) {
            DetectedActivity.ON_BICYCLE -> "avondfiets"
            DetectedActivity.RUNNING -> "avondwandeling"
            else -> "avondwandeling"
        }
        val next = SceneLookup.byId(sceneId) ?: return null
        if (next.id == current.id) return null
        prefs.edit().putString(KEY_LAST_REASON, "wind+beweging rms=${"%.2f".format(intensity)}").apply()
        return next
    }
}
