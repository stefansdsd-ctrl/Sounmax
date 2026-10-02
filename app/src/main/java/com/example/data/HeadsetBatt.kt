package com.example.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

/**
 * Leest het headset-accupercentage uit Bluetooth-broadcasts.
 * Geen vendor-GATT nodig; TAH6519 stuurt dit vaak via het systeem.
 */
object HeadsetBatt {
    private const val PREFS = "sounmax_headset_batt"
    private const val ACTION = "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED"
    private const val EXTRA = "android.bluetooth.device.extra.BATTERY_LEVEL"
    private var receiver: BroadcastReceiver? = null

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        return label(context)
    }

    fun ensure(context: Context) {
        if (receiver != null) return
        val app = context.applicationContext
        val r = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (!enabled(app)) return
                val level = intent.getIntExtra(EXTRA, -1)
                if (level !in 0..100) return
                val name = try {
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.name
                } catch (_: Exception) {
                    null
                }
                prefs(app).edit()
                    .putInt("pct", level)
                    .putString("name", name ?: "")
                    .putLong("last", System.currentTimeMillis())
                    .apply()
            }
        }
        receiver = r
        val filter = IntentFilter(ACTION)
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                app.registerReceiver(r, filter, Context.RECEIVER_EXPORTED)
            } else {
                app.registerReceiver(r, filter)
            }
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

    fun percent(context: Context): Int {
        val p = prefs(context)
        val age = System.currentTimeMillis() - p.getLong("last", 0L)
        if (age > 6 * 60 * 60 * 1000L) return -1
        return p.getInt("pct", -1)
    }

    fun active(context: Context) = enabled(context) && percent(context) in 0..100

    fun label(context: Context): String {
        if (!enabled(context)) return "Headset-accu uit"
        val pct = percent(context)
        if (pct !in 0..100) return "Headset-accu —"
        val name = prefs(context).getString("name", "").orEmpty()
        val who = if (name.isBlank()) "Headset" else name
        return "$who $pct%"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
