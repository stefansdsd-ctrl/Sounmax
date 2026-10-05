package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Beltoon stil of tril + muziek actief:
 * boven 75% → 60%, stapsgewijs.
 * Los van FocusQuietCap (interruption filter / Niet storen).
 */
object RingerQuietCap {
    private const val PREFS = "sounmax_ringer_quiet_cap"
    private const val CAP_PCT = 60
    private const val TRIGGER_PCT = 75

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun ringerQuiet(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.ringerMode == AudioManager.RINGER_MODE_SILENT ||
            am.ringerMode == AudioManager.RINGER_MODE_VIBRATE
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !ringerQuiet(context)) return false
        if (FocusQuietCap.active(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - 2).coerceAtLeast(cap), 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && ringerQuiet(context) && !FocusQuietCap.active(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Bel-cap uit"
        ringerQuiet(context) -> "Bel-cap (60%)"
        else -> "Bel-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
