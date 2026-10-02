package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.view.KeyEvent

/**
 * Headset eruit: pauzeer media, niet alleen volume zakken.
 * Vangt ACTION_AUDIO_BECOMING_NOISY (kabel en veel BT-routes).
 */
object UnplugPause {
    private const val PREFS = "sounmax_unplug_pause"
    private var receiver: BroadcastReceiver? = null

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("paused", false).apply()
        return label(context)
    }

    fun ensure(context: Context) {
        if (receiver != null) return
        val app = context.applicationContext
        val r = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action != AudioManager.ACTION_AUDIO_BECOMING_NOISY) return
                if (!enabled(app)) return
                pause(app)
            }
        }
        receiver = r
        app.registerReceiver(r, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
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

    fun pause(context: Context) {
        val am = audio(context)
        try {
            val now = android.os.SystemClock.uptimeMillis()
            am.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0))
            am.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0))
        } catch (_: Exception) {
        }
        prefs(context).edit()
            .putBoolean("paused", true)
            .putLong("last", System.currentTimeMillis())
            .apply()
    }

    fun active(context: Context) =
        enabled(context) && prefs(context).getBoolean("paused", false) &&
            System.currentTimeMillis() - prefs(context).getLong("last", 0L) < 90_000L

    fun label(context: Context) = when {
        !enabled(context) -> "Los-pauze uit"
        active(context) -> "Los-pauze (media)"
        else -> "Los-pauze aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
