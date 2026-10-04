package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Fietsen (activity recognition, laatste 10 min): boven 72% → 55%, stapsgewijs.
 * Windruis op de TAH6519 maakt hoge volumes onbruikbaar. Geen overlap met RunWind of Sport-app-cap.
 */
object BikeWind {
    private const val PREFS = "sounmax_bike_wind"
    private const val CAP_PCT = 55
    private const val TRIGGER_PCT = 72
    private const val FRESH_MS = 10 * 60_000L

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun cycling(context: Context): Boolean {
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (wellness.getString("last_activity", null) != "cycling") return false
        val at = wellness.getLong("last_activity_at", 0L)
        return at > 0L && System.currentTimeMillis() - at < FRESH_MS
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !cycling(context)) return false
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

    fun active(context: Context) = enabled(context) && cycling(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Fiets-cap uit"
        cycling(context) -> "Fiets-cap (55%)"
        else -> "Fiets-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
