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
 * VoIP-wachttoon (USAGE_VOICE_COMMUNICATION_SIGNALLING) terwijl muziek loopt:
 * stapsgewijs naar 38%. Deelt DuckLane. Laagste cap wint.
 * Geen bel, wekker, timer of nav. Alleen Android 8+.
 */
object WaitDuck {
    private const val PREFS = "sounmax_wait_duck"
    private const val CAP_PCT = 38
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "wait")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (signalling(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun signalling(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (AlarmDuck.ringing(context) || RingDuck.ringing(context) || TimerDuck.tickingNow(context) || NavDuck.guiding(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) return false
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { it.audioAttributes.usage == AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !signalling(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        return DuckLane.hold(context, "wait", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || signalling(context)) return false
        if (!DuckLane.heldBy(context, "wait") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "wait")
    }

    fun active(context: Context): Boolean =
        enabled(context) && signalling(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "Wacht-duck: Android 8+"
        !enabled(context) -> "Wacht-duck uit"
        active(context) -> "Wachttoon, muziek 38%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Wacht-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
