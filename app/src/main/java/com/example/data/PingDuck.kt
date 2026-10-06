package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Melding, alarm of beltoon terwijl muziek loopt: volume stapsgewijs naar 45%.
 * ANC op de TAH6519 slikt anders de ping. Na de toon bouwt het volume terug in stappen van +2.
 */
object PingDuck {
    private const val PREFS = "sounmax_ping_duck"
    private const val CAP_PCT = 45
    private const val STEP = 2
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) clear(context)
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (pinging(app)) apply(app) else tick(app)
                handler.postDelayed(this, 800)
            }
        }
        handler.post(loop)
    }

    fun pinging(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isStreamActive(AudioManager.STREAM_NOTIFICATION) ||
            am.isStreamActive(AudioManager.STREAM_ALARM) ||
            am.isStreamActive(AudioManager.STREAM_RING)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !pinging(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        remember(context, am)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * CAP_PCT) / 100
        prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
        if (cur <= cap) return false
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - STEP).coerceAtLeast(cap), 0)
        return true
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || pinging(context)) return false
        val p = prefs(context)
        val saved = p.getInt("saved", -1)
        if (saved < 0) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur >= saved) {
            clear(context)
            return false
        }
        val next = (cur + STEP).coerceAtMost(saved)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, next, 0)
        if (next >= saved) clear(context)
        return true
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !pinging(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Ping-duck uit"
        active(context) -> "Melding, muziek 45%"
        prefs(context).getInt("saved", -1) >= 0 -> "Volume komt terug"
        else -> "Ping-duck aan"
    }

    private fun remember(context: Context, am: AudioManager) {
        val p = prefs(context)
        if (p.getInt("saved", -1) >= 0) return
        p.edit().putInt("saved", am.getStreamVolume(AudioManager.STREAM_MUSIC)).apply()
    }

    private fun clear(context: Context) {
        prefs(context).edit().remove("saved").apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
