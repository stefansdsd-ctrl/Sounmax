package com.example.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

/**
 * Bluetooth-headset valt weg: pauzeer media.
 * ACTION_AUDIO_BECOMING_NOISY mist veel ACL-drops op TAH6519.
 * Zet dezelfde pauze-vlag als UnplugPause zodat Terug-play hervat.
 */
object AclPause {
    private const val PREFS = "sounmax_acl_pause"
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
                if (intent.action != BluetoothDevice.ACTION_ACL_DISCONNECTED) return
                if (!enabled(app)) return
                UnplugPause.pause(app)
                HeadsetBatt.markGone(app)
                prefs(app).edit().putLong("last", System.currentTimeMillis()).apply()
            }
        }
        receiver = r
        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
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

    fun active(context: Context) =
        enabled(context) &&
            System.currentTimeMillis() - prefs(context).getLong("last", 0L) < 90_000L

    fun label(context: Context) = when {
        !enabled(context) -> "BT-pauze uit"
        active(context) -> "BT-pauze (media)"
        else -> "BT-pauze aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
