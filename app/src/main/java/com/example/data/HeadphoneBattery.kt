package com.example.data

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat

/** Batterij van bonded Classic headset (TAH6519 e.d.). Reflection + broadcast fallback. */
object HeadphoneBattery {
    private const val TAG = "HeadphoneBattery"
    private const val ACTION_BATTERY = "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED"

    fun level(device: BluetoothDevice?): Int {
        if (device == null) return -1
        return try {
            val m = device.javaClass.getMethod("getBatteryLevel")
            (m.invoke(device) as? Int) ?: -1
        } catch (e: Exception) {
            Log.d(TAG, "getBatteryLevel failed: ${e.message}")
            -1
        }
    }

    fun isConnected(device: BluetoothDevice?): Boolean {
        if (device == null) return false
        return try {
            val m = device.javaClass.getMethod("isConnected")
            m.invoke(device) as? Boolean ?: false
        } catch (_: Exception) { false }
    }

    fun registerBatteryReceiver(context: Context, onChange: () -> Unit): android.content.BroadcastReceiver {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == ACTION_BATTERY) onChange()
            }
        }
        val filter = IntentFilter(ACTION_BATTERY)
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        return receiver
    }
}
