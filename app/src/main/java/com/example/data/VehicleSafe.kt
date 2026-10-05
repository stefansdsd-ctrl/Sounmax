package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * In de auto (activity recognition, laatste 15 min) zonder Android Auto:
 * boven 76% → 64%, stapsgewijs. Claxon en navigatie blijven hoorbaar.
 * Geen overlap met DriveHold of CarCap (Android Auto UI).
 */
object VehicleSafe {
    private const val PREFS = "sounmax_vehicle_safe"
    private const val CAP_PCT = 64
    private const val TRIGGER_PCT = 76
    private const val FRESH_MS = 15 * 60_000L

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inVehicle(context: Context): Boolean {
        if (DriveHold.active(context) || CarCap.inCar(context)) return false
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (wellness.getString("last_activity", null) != "in_vehicle") return false
        val at = wellness.getLong("last_activity_at", 0L)
        return at > 0L && System.currentTimeMillis() - at < FRESH_MS
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inVehicle(context)) return false
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

    fun active(context: Context) = enabled(context) && inVehicle(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Rij-cap uit"
        inVehicle(context) -> "Rij-cap (64%)"
        else -> "Rij-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
