package com.example.media

import android.content.Context
import android.media.AudioManager

/** Voorkomt dat speaker-volume op headset-niveau blijft na ontkoppelen. */
object SpeakerSafeOnDisconnect {
    private const val PREFS = "sounmax_speaker_safe"
    private const val KEY = "enabled"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, on).apply()
    }

    fun apply(context: Context, am: AudioManager) {
        if (!enabled(context)) return
        if (am.isBluetoothA2dpOn) return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cap = (max * 0.5f).toInt().coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur > cap) {
            runCatching { am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0) }
        }
    }
}
