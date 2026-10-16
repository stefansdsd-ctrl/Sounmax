package com.example.data

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Batterij van bonded Classic headset (TAH6519 e.d.).
 * Reflection getBatteryLevel + HF-indicator (id=2) + BATTERY_LEVEL_CHANGED.
 * Chip + notificatie bij laag (<20%).
 * BatteryHistory logging.
 */
object HeadphoneBattery {
    private const val TAG = "HeadphoneBattery"
    private const val PREFS = "sounmax_hp_battery"
    private const val LOW = 20
    private const val NOTIF = 4201
    private const val ACTION_BATTERY = "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED"

    private var last = -1

    fun level(device: BluetoothDevice?): Int {
        if (device == null) return last
        val r = viaReflection(device)
        if (r in 0..100) {
            last = r
            return r
        }
        return last
    }

    private fun viaReflection(device: BluetoothDevice): Int = try {
        val m = device.javaClass.getMethod("getBatteryLevel")
        (m.invoke(device) as? Int) ?: -1
    } catch (e: Exception) {
        Log.d(TAG, "reflect fail: ${e.message}")
        -1
    }

    fun isConnected(device: BluetoothDevice?): Boolean = try {
        val m = device?.javaClass?.getMethod("isConnected")
        m?.invoke(device) as? Boolean ?: false
    } catch (_: Exception) { false }

    fun register(context: Context, onChange: () -> Unit = {}): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val value = when (intent?.action) {
                    BluetoothHeadset.ACTION_HF_INDICATORS_VALUE_CHANGED -> {
                        val id = intent.getIntExtra(BluetoothHeadset.EXTRA_HF_INDICATORS_IND_ID, -1)
                        if (id == 2) intent.getIntExtra(BluetoothHeadset.EXTRA_HF_INDICATORS_IND_VALUE, -1) else -1
                    }
                    ACTION_BATTERY -> intent.getIntExtra("android.bluetooth.device.extra.BATTERY_LEVEL", -1)
                    else -> -1
                }
                if (value in 0..100 && value != last) {
                    last = value
                    ctx?.let { 
                        save(it, value)
                        BatteryHistory.record(it, value)
                        if (value in 1..LOW) notifyLow(it, value) 
                    }
                    onChange()
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(ACTION_BATTERY)
            addAction(BluetoothHeadset.ACTION_HF_INDICATORS_VALUE_CHANGED)
        }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        return receiver
    }

    private fun save(context: Context, level: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("level", level).putLong("ts", System.currentTimeMillis()).apply()
    }

    fun chip(context: Context): String {
        val l = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("level", last)
        return when {
            l < 0 -> "Bat: ?"
            l <= LOW -> "Bat: $l% laag"
            else -> "Bat: $l%"
        }
    }

    private fun notifyLow(context: Context, level: Int) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val n = NotificationCompat.Builder(context, "sounmax_default")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setContentTitle("Headset batterij laag")
            .setContentText("$level% — laad op")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        try { NotificationManagerCompat.from(context).notify(NOTIF, n) } catch (_: Exception) {}
    }
}
