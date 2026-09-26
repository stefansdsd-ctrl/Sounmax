package com.example.data

import android.content.Context
import android.os.BatteryManager

/**
 * Bij lage accu DSP-zware lagen uitzetten (AI-tuner cache-only, extra scenes uit).
 */
object BatterySaverDsp {
    private const val PREFS = "sounmax_battery_dsp"
    private const val KEY_ON = "enabled"
    private const val KEY_THRESHOLD = "threshold_pct"

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ON, true)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ON, on).apply()
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        return next
    }

    fun threshold(context: Context): Int =
        prefs(context).getInt(KEY_THRESHOLD, 20).coerceIn(5, 50)

    fun setThreshold(context: Context, pct: Int) {
        prefs(context).edit().putInt(KEY_THRESHOLD, pct.coerceIn(5, 50)).apply()
    }

    fun batteryPercent(context: Context): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return 100
        val pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return if (pct in 0..100) pct else 100
    }

    fun shouldSave(context: Context): Boolean =
        enabled(context) && batteryPercent(context) <= threshold(context)

    fun label(context: Context): String {
        val pct = batteryPercent(context)
        return when {
            !enabled(context) -> "Accu-DSP uit ($pct%)"
            shouldSave(context) -> "Accu-DSP spaarstand $pct%≤${threshold(context)}%"
            else -> "Accu-DSP klaar $pct%"
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
