package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Stille beltoon + muziek boven 64% -> cap 46%.
 * RINGER_MODE_SILENT betekent dat de omgeving stil moet blijven.
 */
object SilentSoft {
    private const val PREFS = "sounmax_silent_soft"
    private const val CAP_PCT = 46
    private const val TRIGGER_PCT = 64

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun silent(context: Context): Boolean = try {
        audio(context).ringerMode == AudioManager.RINGER_MODE_SILENT
    } catch (_: Exception) {
        false
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !silent(context)) return false
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && silent(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Stil-cap uit"
        silent(context) -> "Stil-cap (46%)"
        else -> "Stil-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
