package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build

/**
 * Telefoon-accu ≤15% + muziek boven 55% → cap 40%.
 * Apart van headset-accu; spaart de telefoon bij luide EQ.
 */
object PhoneLowCap {
    private const val PREFS = "sounmax_phone_low_cap"
    private const val CAP_PCT = 40
    private const val TRIGGER_PCT = 55
    private const val LOW = 15
    private var receiver: BroadcastReceiver? = null

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun ensure(context: Context) {
        if (receiver != null) return
        val app = context.applicationContext
        val r = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action != Intent.ACTION_BATTERY_CHANGED) return
                remember(app, intent)
                apply(app)
            }
        }
        receiver = r
        try {
            val sticky = if (Build.VERSION.SDK_INT >= 33) {
                app.registerReceiver(r, IntentFilter(Intent.ACTION_BATTERY_CHANGED), Context.RECEIVER_NOT_EXPORTED)
            } else {
                app.registerReceiver(r, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            }
            if (sticky != null) remember(app, sticky)
        } catch (_: Exception) {
            receiver = null
        }
    }

    fun release(context: Context) {
        receiver?.let {
            try {
                context.applicationContext.unregisterReceiver(it)
            } catch (_: Exception) {
            }
        }
        receiver = null
    }

    private fun remember(context: Context, intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        if (level < 0) return
        val pct = (level * 100) / scale
        prefs(context).edit().putInt("pct", pct).putLong("last", System.currentTimeMillis()).apply()
    }

    fun percent(context: Context): Int {
        val cached = prefs(context).getInt("pct", -1)
        if (cached in 0..100) return cached
        val sticky = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return -1
        val level = sticky.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = sticky.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        if (level < 0) return -1
        return (level * 100) / scale
    }

    fun low(context: Context) = percent(context) in 0..LOW

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !low(context)) return false
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

    fun active(context: Context) = enabled(context) && low(context)

    fun label(context: Context): String {
        if (!enabled(context)) return "Tel-cap uit"
        val pct = percent(context)
        return if (pct in 0..LOW) "Tel-cap $pct% → 40%" else "Tel-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
