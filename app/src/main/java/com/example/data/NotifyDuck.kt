package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Duck muziek 8s naar 40% bij een nieuwe melding (NotificationListener).
 */
object NotifyDuck {
    private const val PREFS = "sounmax_notify_duck"
    private const val DURATION_MS = 8_000L
    private const val TARGET_PCT = 40

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) stop(context)
        return label(context)
    }

    fun active(context: Context): Boolean {
        if (!prefs(context).getBoolean("active", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return false
        }
        return true
    }

    fun onNotification(context: Context) {
        if (!enabled(context)) return
        if (TalkSoft.active(context) || DoorListen.active(context) || StreetListen.active(context)) return
        start(context)
    }

    fun start(context: Context) {
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val target = (max * TARGET_PCT) / 100
        val prev = if (prefs(context).getBoolean("active", false)) {
            prefs(context).getInt("prev", cur)
        } else cur
        prefs(context).edit()
            .putBoolean("active", true)
            .putLong("until", System.currentTimeMillis() + DURATION_MS)
            .putInt("prev", prev)
            .apply()
        if (cur > target) am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
    }

    fun apply(context: Context): Boolean {
        if (!prefs(context).getBoolean("active", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return true
        }
        return false
    }

    fun stop(context: Context) {
        val prev = prefs(context).getInt("prev", -1)
        prefs(context).edit().putBoolean("active", false).remove("until").remove("prev").apply()
        if (prev >= 0) {
            val am = audio(context)
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, prev.coerceIn(0, max), 0)
        }
    }

    fun label(context: Context): String = when {
        !enabled(context) -> "Duck uit"
        active(context) -> "Duck (${(remainingMs(context) / 1000).toInt()}s · 40%)"
        else -> "Duck aan"
    }

    private fun remainingMs(context: Context): Long =
        prefs(context).getLong("until", 0L) - System.currentTimeMillis()

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
