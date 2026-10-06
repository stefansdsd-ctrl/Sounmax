package com.example.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager

/**
 * Telefoonaccu ≤ 20% + muziek actief:
 * boven 65% → 50%, stapsgewijs.
 * Los van PowerSaveCap (spaarstand).
 */
object LowBatteryCap {
    private const val PREFS = "sounmax_low_batt_cap"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 65
    private const val LOW = 20

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun batteryPct(context: Context): Int {
        val battery = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (level < 0 || scale <= 0) return 100
        return (100 * level) / scale
    }

    fun low(context: Context) = batteryPct(context) in 0..LOW

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !low(context)) return false
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

    fun active(context: Context) = enabled(context) && low(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Accu-cap uit"
        low(context) -> "Accu-cap (50%)"
        else -> "Accu-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
