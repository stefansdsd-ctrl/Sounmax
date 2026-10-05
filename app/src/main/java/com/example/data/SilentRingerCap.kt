package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Beltoon op tril of stil + muziek actief:
 * boven 68% → 52%, stapsgewijs.
 * Los van FocusQuietCap (Niet storen / interruption filter).
 */
object SilentRingerCap {
    private const val PREFS = "sounmax_silent_ringer_cap"
    private const val CAP_PCT = 52
    private const val TRIGGER_PCT = 68

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun ringerQuiet(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.ringerMode != AudioManager.RINGER_MODE_NORMAL
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !ringerQuiet(context)) return false
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

    fun active(context: Context) = enabled(context) && ringerQuiet(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Stil-cap uit"
        ringerQuiet(context) -> "Stil-cap (52%)"
        else -> "Stil-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
