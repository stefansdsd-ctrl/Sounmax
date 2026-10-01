package com.example.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

/**
 * Headset los: muziek boven 40% zakt naar 28%.
 * Voorkomt dat de telefoonspeaker hard doorgaat na ACL-disconnect.
 */
object SpeakerGuard {
    private const val PREFS = "sounmax_speaker_guard"
    private var receiver: BroadcastReceiver? = null

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("dropped", false).apply()
        return label(context)
    }

    fun ensure(context: Context) {
        if (receiver != null) return
        val app = context.applicationContext
        val r = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action != BluetoothDevice.ACTION_ACL_DISCONNECTED) return
                if (!enabled(app)) return
                drop(app)
            }
        }
        receiver = r
        app.registerReceiver(r, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))
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

    fun drop(context: Context) {
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * 40) / 100) {
            prefs(context).edit().putBoolean("dropped", false).apply()
            return
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (max * 28) / 100, 0)
        prefs(context).edit()
            .putBoolean("dropped", true)
            .putLong("last", System.currentTimeMillis())
            .apply()
    }

    fun active(context: Context) =
        enabled(context) && prefs(context).getBoolean("dropped", false) &&
            System.currentTimeMillis() - prefs(context).getLong("last", 0L) < 90_000L

    fun label(context: Context) = when {
        !enabled(context) -> "Speaker-drop uit"
        active(context) -> "Speaker-drop (28%)"
        else -> "Speaker-drop aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
