package com.example.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.view.KeyEvent

/**
 * Na los-pauze: headset weer aan → media play.
 * Alleen als UnplugPause in de laatste 3 minuten pauzeerde.
 */
object ReplugPlay {
    private const val PREFS = "sounmax_replug_play"
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
                val action = intent.action ?: return
                val plugged = action == Intent.ACTION_HEADSET_PLUG &&
                    intent.getIntExtra("state", 0) == 1
                val bt = action == BluetoothDevice.ACTION_ACL_CONNECTED
                if (!plugged && !bt) return
                if (!recentPause(app)) return
                play(app)
            }
        }
        receiver = r
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        }
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

    private fun recentPause(context: Context): Boolean {
        val p = context.getSharedPreferences("sounmax_unplug_pause", Context.MODE_PRIVATE)
        if (!p.getBoolean("paused", false)) return false
        return System.currentTimeMillis() - p.getLong("last", 0L) < 3 * 60 * 1000L
    }

    fun play(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            val now = android.os.SystemClock.uptimeMillis()
            am.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0))
            am.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0))
        } catch (_: Exception) {
        }
        prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
        context.getSharedPreferences("sounmax_unplug_pause", Context.MODE_PRIVATE)
            .edit().putBoolean("paused", false).apply()
    }

    fun active(context: Context) =
        enabled(context) &&
            System.currentTimeMillis() - prefs(context).getLong("last", 0L) < 90_000L

    fun label(context: Context) = when {
        !enabled(context) -> "Terug-play uit"
        active(context) -> "Terug-play (media)"
        else -> "Terug-play aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
