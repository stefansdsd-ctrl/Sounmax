package com.example.media

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Na 22:00 volume cap 50% tenzij uitgeschakeld.
 * Voorkomt late luide pieken.
 */
object NightVolumeGuard {
    private const val PREFS = "soundmax_prefs"
    const val KEY = "night_volume_guard"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY, on).apply()
    }

    fun isNight(): Boolean {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return h >= 22 || h < 7
    }

    /** Returns true if volume was capped. */
    fun applyIfNeeded(context: Context): Boolean {
        if (!enabled(context) || !isNight()) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * 0.5f).toInt().coerceAtLeast(1)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            return true
        }
        return false
    }
}
