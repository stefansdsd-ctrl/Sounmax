package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.PowerManager

/**
 * Systeem-spaarstand (niet accu-percentage) + muziek actief:
 * boven 72% → 60%, stapsgewijs. Apart van Tel-cap en Accu-DSP.
 */
object PowerSaveCap {
    private const val PREFS = "sounmax_power_save_cap"
    private const val CAP_PCT = 60
    private const val TRIGGER_PCT = 72

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun saving(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
        return pm.isPowerSaveMode
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !saving(context)) return false
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

    fun active(context: Context) = enabled(context) && saving(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Spaar-cap uit"
        saving(context) -> "Spaar-cap (60%)"
        else -> "Spaar-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
