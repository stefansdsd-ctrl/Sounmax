package com.example.media

import android.content.Context
import android.media.AudioManager
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import com.google.android.gms.location.DetectedActivity

/**
 * Buiten in beweging: transparantie-scene + zachter volume zodat verkeer hoorbaar blijft.
 */
object OutdoorSafetyAdvisor {
    const val KEY_ENABLED = "outdoor_safety"
    const val KEY_CAP = "outdoor_safety_cap_pct"
    const val DEFAULT_CAP = 55

    private val MOVING = setOf(
        DetectedActivity.ON_FOOT,
        DetectedActivity.WALKING,
        DetectedActivity.RUNNING,
        DetectedActivity.ON_BICYCLE
    )

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun isOutdoorMoving(context: Context): Boolean {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val fresh = System.currentTimeMillis() - prefs.getLong("last_activity_at", 0L) < 8 * 60_000L
        if (!fresh) return false
        return prefs.getInt("last_activity_type", -1) in MOVING
    }

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context) || !isOutdoorMoving(context)) return scene
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val type = prefs.getInt("last_activity_type", -1)
        val id = when (type) {
            DetectedActivity.ON_BICYCLE -> "bike"
            DetectedActivity.RUNNING -> "cardio"
            else -> "walk"
        }
        enforceVolume(context)
        return SceneLookup.byId(id) ?: SceneLookup.byId("podcastwalk") ?: scene
    }

    fun hint(context: Context): String? {
        if (!enabled(context) || !isOutdoorMoving(context)) return null
        return "Buiten: transparantie + volume ≤ ${capPct(context)}%"
    }

    fun capPct(context: Context): Int =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_CAP, DEFAULT_CAP).coerceIn(35, 80)

    fun enforceVolume(context: Context): Boolean {
        if (!enabled(context) || !isOutdoorMoving(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cap = ((max * capPct(context)) / 100).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            return true
        }
        return false
    }
}
