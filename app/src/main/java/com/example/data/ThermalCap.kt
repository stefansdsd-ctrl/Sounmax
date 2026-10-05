package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.PowerManager

/**
 * Telefoon warm (thermal status matig of hoger) + muziek actief:
 * boven 68% → 55%, stapsgewijs. Minder hitte en accudrain bij luide BT.
 */
object ThermalCap {
    private const val PREFS = "sounmax_thermal_cap"
    private const val CAP_PCT = 55
    private const val TRIGGER_PCT = 68

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun hot(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
        return pm.currentThermalStatus >= PowerManager.THERMAL_STATUS_MODERATE
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !hot(context)) return false
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

    fun active(context: Context) = enabled(context) && hot(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Warmte-cap uit"
        hot(context) -> "Warmte-cap (55%)"
        else -> "Warmte-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
