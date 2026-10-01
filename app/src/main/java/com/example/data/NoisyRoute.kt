package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

/**
 * Audio gaat naar de speaker (plug of BT los): volume boven 32% zakt naar 24%.
 * Vangt ACTION_AUDIO_BECOMING_NOISY, naast de ACL-speaker-drop.
 */
object NoisyRoute {
    private const val PREFS = "sounmax_noisy_route"
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
                if (intent.action != AudioManager.ACTION_AUDIO_BECOMING_NOISY) return
                if (!enabled(app)) return
                drop(app)
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

    fun drop(context: Context) {
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * 32) / 100) {
            prefs(context).edit().putBoolean("dropped", false).apply()
            return
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (max * 24) / 100, 0)
        prefs(context).edit()
            .putBoolean("dropped", true)
            .putLong("last", System.currentTimeMillis())
            .apply()
    }

    fun active(context: Context) =
        enabled(context) && prefs(context).getBoolean("dropped", false) &&
            System.currentTimeMillis() - prefs(context).getLong("last", 0L) < 90_000L

    fun label(context: Context) = when {
        !enabled(context) -> "Route-drop uit"
        active(context) -> "Route-drop (24%)"
        else -> "Route-drop aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
