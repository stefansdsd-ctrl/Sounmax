package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Lopen (activity recognition, laatste 12 min) tussen 20:00 en 06:00:
 * boven 68% → 52%, stapsgewijs. Verkeer en overstekers blijven hoorbaar.
 * Geen overlap met RunWind (run) of BikeWind (cycling).
 */
object WalkSafe {
    private const val PREFS = "sounmax_walk_safe"
    private const val CAP_PCT = 52
    private const val TRIGGER_PCT = 68
    private const val FRESH_MS = 12 * 60_000L

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun nightWalking(context: Context): Boolean {
        if (!dark()) return false
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (wellness.getString("last_activity", null) != "walk") return false
        val at = wellness.getLong("last_activity_at", 0L)
        return at > 0L && System.currentTimeMillis() - at < FRESH_MS
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !nightWalking(context)) return false
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

    fun active(context: Context) = enabled(context) && nightWalking(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Avond-loop uit"
        nightWalking(context) -> "Avond-loop (52%)"
        else -> "Avond-loop aan"
    }

    private fun dark(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour >= 20 || hour < 6
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
