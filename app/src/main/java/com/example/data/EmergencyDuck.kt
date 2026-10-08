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
 * Noodmelding (USAGE_EMERGENCY) terwijl muziek loopt: stapsgewijs naar 22%.
 * ANC slikt NL-Alert / amber / overheid anders. Deelt DuckLane. Laagste cap wint.
 * Geen wekker of bel. Alleen Android 8+.
 */
object EmergencyDuck {
    private const val PREFS = "sounmax_emergency_duck"
    private const val CAP_PCT = 22
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "sos")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (alerting(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun alerting(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { it.audioAttributes.usage == AudioAttributes.USAGE_EMERGENCY }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !alerting(context)) return false
        return DuckLane.hold(context, "sos", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || alerting(context)) return false
        if (!DuckLane.heldBy(context, "sos") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "sos")
    }

    fun active(context: Context): Boolean =
        enabled(context) && alerting(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "Nood-duck: Android 8+"
        !enabled(context) -> "Nood-duck uit"
        active(context) -> "Noodmelding, muziek 22%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Nood-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
