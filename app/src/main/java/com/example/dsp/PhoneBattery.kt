package com.example.dsp

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object PhoneBattery {
    fun percent(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return 100
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        if (level < 0) return 100
        return ((level * 100) / scale).coerceIn(0, 100)
    }

    fun isLow(context: Context, threshold: Int = 20): Boolean = percent(context) <= threshold

    fun isCritical(context: Context): Boolean = percent(context) <= 10
}
