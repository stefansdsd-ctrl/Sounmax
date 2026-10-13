package com.example.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.dsp.AudioDspManager

/** Past opgeslagen EQ (slot 0) toe bij connect van doel-headset. */
object ConnectEqApplier {
    fun register(context: Context, targetAddress: String?) {
        if (targetAddress.isNullOrBlank()) return
        val app = context.applicationContext
        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        app.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val enabled = ctx.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                    .getBoolean("eq_persist_on_connect", true)
                if (!enabled) return
                val dev = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (!dev?.address.equals(targetAddress, ignoreCase = true)) return
                val preset = EqPersist.load(ctx, 0) ?: return
                // Note: AudioDspManager is instance-based; store pending for UI/ViewModel apply
                ctx.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE).edit()
                    .putBoolean("pending_eq_persist", true)
                    .apply()
            }
        }, filter)
    }
}
