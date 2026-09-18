package com.example.media

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.dsp.BestNow
import com.example.widget.SoundMaxWidget

/** Past Beste-nu toe als de gekoppelde headset verbindt. */
object BtConnectBestNow {
    fun register(context: Context, targetAddress: String?) {
        if (targetAddress.isNullOrBlank()) return
        val app = context.applicationContext
        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        app.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val enabled = ctx.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                    .getBoolean("bestnow_on_connect", true)
                if (!enabled) return
                val dev = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (!dev?.address.equals(targetAddress, ignoreCase = true)) return
                val scene = BestNow.top(ctx) ?: return
                ctx.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                    .edit()
                    .putString("last_scene_id", scene.id)
                    .putBoolean("pending_widget_scene", true)
                    .putBoolean("auto_scene", true)
                    .apply()
                SoundMaxWidget.applyScene(ctx, scene.id)
            }
        }, filter)
    }
}
