package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Stilzitten (activity recognition, laatste 10 min) tussen 09:00 en 17:00:
 * boven 82% → 72%, stapsgewijs. Geen overlap met Rij-cap, Avond-loop, Hardloop, Fiets of Doze.
 */
object StillSafe {
    private const val PREFS = "sounmax_still_safe"
    private const val CAP_PCT = 72
    private const val TRIGGER_PCT = 82
    private const val FRESH_MS = 10 * 60_000L

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun deskStill(context: Context): Boolean {
        if (!workHours()) return false
        if (VehicleSafe.inVehicle(context) || WalkSafe.nightWalking(context)) return false
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (wellness.getString("last_activity", null) != "still") return false
        val at = wellness.getLong("last_activity_at", 0L)
        return at > 0L && System.currentTimeMillis() - at < FRESH_MS
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !deskStill(context)) return false
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

    fun active(context: Context) = enabled(context) && deskStill(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Bureau-cap uit"
        deskStill(context) -> "Bureau-cap (72%)"
        else -> "Bureau-cap aan"
    }

    private fun workHours(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour in 9..16
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
