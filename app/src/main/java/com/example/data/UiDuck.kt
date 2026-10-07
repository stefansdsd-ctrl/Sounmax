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
 * UI-klik en toetsgeluid (USAGE_ASSISTANCE_SONIFICATION) terwijl muziek loopt:
 * volume stapsgewijs naar 55%. Deelt DuckLane. Laagste cap wint.
 * Geen wekker, bel, timer, nav, TTS, assistent, ping of game. Alleen Android 8+.
 */
object UiDuck {
    private const val PREFS = "sounmax_ui_duck"
    private const val CAP_PCT = 55
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "ui")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (playing(app)) apply(app) else tick(app)
                handler.postDelayed(this, 400)
            }
        }
        handler.post(loop)
    }

    fun playing(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (AlarmDuck.ringing(context) || RingDuck.ringing(context) || TimerDuck.tickingNow(context) || NavDuck.guiding(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { cfg ->
            cfg.audioAttributes.usage == AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
        }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !playing(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        return DuckLane.hold(context, "ui", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || playing(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        if (!DuckLane.heldBy(context, "ui") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "ui")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !playing(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "UI-duck: Android 8+"
        !enabled(context) -> "UI-duck uit"
        active(context) -> "Tik, muziek 55%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "UI-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
