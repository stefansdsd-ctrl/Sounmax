package com.example.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

/**
 * Na headset-connect: volume boven 55% zakt naar 28% en klimt in 4 stappen terug.
 * Voorkomt een klap in de oren bij ACL-connect.
 */
object ConnectRamp {
    private const val PREFS = "sounmax_connect_ramp"
    private var receiver: BroadcastReceiver? = null
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) handler.removeCallbacksAndMessages(null)
        return label(context)
    }

    fun ensure(context: Context) {
        if (receiver != null) return
        val app = context.applicationContext
        val r = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action != BluetoothDevice.ACTION_ACL_CONNECTED) return
                if (!enabled(app)) return
                ramp(app)
            }
        }
        receiver = r
        app.registerReceiver(r, IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED))
    }

    fun release(context: Context) {
        receiver?.let {
            try {
                context.applicationContext.unregisterReceiver(it)
            } catch (_: Exception) {
            }
        }
        receiver = null
        handler.removeCallbacksAndMessages(null)
    }

    fun ramp(context: Context) {
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * 55) / 100) return
        val start = (max * 28) / 100
        am.setStreamVolume(AudioManager.STREAM_MUSIC, start, 0)
        val step = (cur - start) / 4
        for (i in 1..4) {
            handler.postDelayed({
                if (!enabled(context)) return@postDelayed
                am.setStreamVolume(AudioManager.STREAM_MUSIC, (start + step * i).coerceAtMost(cur), 0)
            }, i * 900L)
        }
        prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
    }

    fun label(context: Context) =
        if (enabled(context)) "Zachte start aan" else "Zachte start uit"

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
