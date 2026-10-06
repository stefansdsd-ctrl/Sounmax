package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Muziek actief en volume springt meer dan 3 stappen sinds de vorige meting:
 * terug naar vorige stap + 1. Voorkomt een knal bij per ongeluk volume omhoog.
 */
object SpikeGuard {
    private const val PREFS = "sounmax_spike_guard"
    private const val MAX_JUMP = 3

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun apply(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val prev = prefs(context).getInt("prev", cur)
        if (!enabled(context) || !am.isMusicActive) {
            prefs(context).edit().putInt("prev", cur).apply()
            return false
        }
        if (cur - prev > MAX_JUMP) {
            val target = (prev + 1).coerceAtMost(cur)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
            prefs(context).edit()
                .putInt("prev", target)
                .putLong("last", System.currentTimeMillis())
                .apply()
            return true
        }
        prefs(context).edit().putInt("prev", cur).apply()
        return false
    }

    fun active(context: Context): Boolean {
        if (!enabled(context)) return false
        val last = prefs(context).getLong("last", 0L)
        return System.currentTimeMillis() - last < 8_000L
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Sprong uit"
        active(context) -> "Sprong gedempt"
        else -> "Sprong aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
