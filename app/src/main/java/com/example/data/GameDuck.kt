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
 * Spel-SFX (USAGE_GAME) terwijl muziek loopt: volume stapsgewijs naar 50%.
 * Deelt DuckLane. Laagste cap wint. Na het effect +2 terug. Geen knal.
 * Geen wekker, bel, timer, nav, TTS, assistent of ping. Alleen Android 8+.
 */
object GameDuck {
    private const val PREFS = "sounmax_game_duck"
    private const val CAP_PCT = 50
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "game")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (playing(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
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
        return configs.any { cfg -> cfg.audioAttributes.usage == AudioAttributes.USAGE_GAME }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !playing(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        return DuckLane.hold(context, "game", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || playing(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        if (!DuckLane.heldBy(context, "game") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "game")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !playing(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "Game-duck: Android 8+"
        !enabled(context) -> "Game-duck uit"
        active(context) -> "Spel, muziek 50%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Game-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
