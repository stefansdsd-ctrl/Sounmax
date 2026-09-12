package com.example.media

import android.content.Context
import android.media.AudioManager
import com.example.dsp.ListeningScenes

/**
 * Dagelijkse gehoordosis-cap: 180 minuten-equivalent.
 * ≥ cap → veilig volume + rust-scene tot middernacht.
 */
object DailyHearingBudget {
    const val KEY_ENABLED = "daily_hearing_budget"
    const val CAP_MIN = 180
    private const val WELLNESS = "soundmax_wellness"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun todayMinutes(context: Context): Int {
        val wellness = context.getSharedPreferences(WELLNESS, Context.MODE_PRIVATE)
        val stored = wellness.getInt("dose_today", 0)
        val fromDose = ListenDose.weekMinutes(context).lastOrNull()?.second?.toInt() ?: 0
        return maxOf(stored, fromDose)
    }

    fun percent(context: Context): Int =
        ((todayMinutes(context) / CAP_MIN.toFloat()) * 100f).toInt().coerceAtLeast(0)

    fun overCap(context: Context): Boolean = enabled(context) && todayMinutes(context) >= CAP_MIN

    fun chipLabel(context: Context): String {
        val min = todayMinutes(context)
        return if (overCap(context)) {
            "Dosis $min/${CAP_MIN} min — rust tot 00:00"
        } else {
            "$min/${CAP_MIN} min vandaag"
        }
    }

    fun applySoftCap(context: Context): Boolean {
        if (!overCap(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * 0.55f).toInt().coerceAtLeast(1)
        var changed = false
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            changed = true
        }
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(SafeVolume.KEY, true).apply()
        SafeVolume.enforce(context)
        return changed
    }

    fun rustSceneOrNull() =
        ListeningScenes.byId("rust")
            ?: ListeningScenes.byId("winddown")
            ?: ListeningScenes.byId("nacht")
}
