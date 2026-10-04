package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Hardlopen (activity recognition, laatste 8 min): boven 70% → 58%, stapsgewijs.
 * Wind en impact maken hoge volumes onbruikbaar. Geen overlap met Sport-app-cap.
 */
object RunWind {
    private const val PREFS = "sounmax_run_wind"
    private const val CAP_PCT = 58
    private const val TRIGGER_PCT = 70
    private const val FRESH_MS = 8 * 60_000L

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun running(context: Context): Boolean {
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (wellness.getString("last_activity", null) != "run") return false
        val at = wellness.getLong("last_activity_at", 0L)
        return at > 0L && System.currentTimeMillis() - at < FRESH_MS
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !running(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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

    fun active(context: Context) = enabled(context) && running(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Wind-cap uit"
        running(context) -> "Wind-cap (58%)"
        else -> "Wind-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
