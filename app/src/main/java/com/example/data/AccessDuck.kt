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
 * TalkBack / toegankelijkheidsklik terwijl muziek loopt: stapsgewijs naar 33%.
 * Zit onder TTS (34) en boven bel (32). Deelt DuckLane. Laagste cap wint.
 * Geen wekker, bel of NL-Alert. Alleen Android 8+.
 */
object AccessDuck {
    private const val PREFS = "sounmax_access_duck"
    private const val CAP_PCT = 33
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "access")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (accessing(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun accessing(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (EmergencyDuck.alerting(context) || AlarmDuck.ringing(context) || RingDuck.ringing(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { it.audioAttributes.usage == AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !accessing(context)) return false
        return DuckLane.hold(context, "access", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || accessing(context)) return false
        if (!DuckLane.heldBy(context, "access") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "access")
    }

    fun active(context: Context): Boolean =
        enabled(context) && accessing(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "Toegang-duck: Android 8+"
        !enabled(context) -> "Toegang-duck uit"
        active(context) -> "TalkBack, muziek 33%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Toegang-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
