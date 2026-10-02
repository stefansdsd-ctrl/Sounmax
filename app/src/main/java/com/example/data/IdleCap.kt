package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.PowerManager

/**
 * Doze/idle: muziek boven 70% → 48%.
 * Apart van scherm-uit en batterijspaarstand.
 */
object IdleCap {
    private const val PREFS = "sounmax_idle_cap"
    private const val CAP_PCT = 48
    private const val TRIGGER_PCT = 70

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun idle(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 23) return false
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isDeviceIdleMode
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !idle(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putLong("capped", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && idle(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Doze-cap uit"
        idle(context) -> "Doze-cap → 48%"
        else -> "Doze-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
