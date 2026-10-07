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
 * Navigatie-stem (Maps, Waze) terwijl muziek loopt: volume stapsgewijs naar 42%.
 * Deelt DuckLane met alarm, bel, timer, ping en assistent. Laagste cap wint.
 * Na de aanwijzing bouwt het volume terug in stappen van +2. Geen knal.
 * Alleen Android 8+ (active playback configs).
 */
object NavDuck {
    private const val PREFS = "sounmax_nav_duck"
    private const val CAP_PCT = 42
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "nav")
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (guiding(app)) apply(app) else tick(app)
                handler.postDelayed(this, 650)
            }
        }
        handler.post(loop)
    }

    fun guiding(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (AlarmDuck.ringing(context) || RingDuck.ringing(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { cfg ->
            cfg.audioAttributes.usage == AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE
        }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !guiding(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        return DuckLane.hold(context, "nav", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || guiding(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        if (!DuckLane.heldBy(context, "nav") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "nav")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !guiding(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Nav-duck uit"
        active(context) -> "Nav, muziek 42%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Nav-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
