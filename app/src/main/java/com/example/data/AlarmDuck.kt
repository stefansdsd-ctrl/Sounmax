package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Wekker of alarm terwijl muziek loopt: volume stapsgewijs naar 28%.
 * ANC slikt een wekker anders. Na het alarm bouwt het volume terug in stappen van +2.
 * Alleen Android 8+ (active playback configs).
 */
object AlarmDuck {
    private const val PREFS = "sounmax_alarm_duck"
    private const val CAP_PCT = 28
    private const val STEP = 2
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) { clear(context); DuckLane.release(context, "alarm") }
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (ringing(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun ringing(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { cfg ->
            cfg.audioAttributes.usage == AudioAttributes.USAGE_ALARM
        }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !ringing(context)) return false
        return DuckLane.hold(context, "alarm", 28)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || ringing(context)) return false
        if (!DuckLane.heldBy(context, "alarm") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "alarm")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !ringing(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Alarm-duck uit"
        active(context) -> "Wekker, muziek 28%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Alarm-duck aan"
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
