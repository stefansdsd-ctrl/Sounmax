package com.example.media

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

/**
 * Pauzeert media bij ACL-disconnect van de gekoppelde headset.
 */
object BtDisconnectPause {
    fun register(context: Context, targetAddress: String?) {
        if (targetAddress.isNullOrBlank()) return
        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val dev = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (dev?.address.equals(targetAddress, ignoreCase = true)) {
                    val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    am.dispatchMediaKeyEvent(
                        android.view.KeyEvent(
                            android.view.KeyEvent.ACTION_DOWN,
                            android.view.KeyEvent.KEYCODE_MEDIA_PAUSE
                        )
                    )
                    am.dispatchMediaKeyEvent(
                        android.view.KeyEvent(
                            android.view.KeyEvent.ACTION_UP,
                            android.view.KeyEvent.KEYCODE_MEDIA_PAUSE
                        )
                    )
                }
            }
        }, filter)
    }
}
