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
 * Korte melding (chat, mail) terwijl muziek loopt: volume stapsgewijs naar 45%.
 * Deelt DuckLane. Laagste cap wint. Na de ping +2 terug. Geen knal.
 * Geen ringtone, wekker of navigatie. Alleen Android 8+.
 */
object PingDuck {
    private const val PREFS = "sounmax_ping_duck"
    private const val CAP_PCT = 45
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "ping")
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (pinging(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun pinging(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (AlarmDuck.ringing(context) || RingDuck.ringing(context) || TimerDuck.tickingNow(context) || NavDuck.guiding(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { cfg -> isPing(cfg.audioAttributes) }
    }

    private fun isPing(attrs: AudioAttributes): Boolean {
        if (attrs.usage == AudioAttributes.USAGE_NOTIFICATION_RINGTONE) return false
        if (attrs.usage == AudioAttributes.USAGE_ALARM) return false
        if (attrs.usage == AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE) return false
        return attrs.usage == AudioAttributes.USAGE_NOTIFICATION_EVENT
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !pinging(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        return DuckLane.hold(context, "ping", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || pinging(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        if (!DuckLane.heldBy(context, "ping") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "ping")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !pinging(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Ping-duck uit"
        active(context) -> "Ping, muziek 45%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Ping-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
