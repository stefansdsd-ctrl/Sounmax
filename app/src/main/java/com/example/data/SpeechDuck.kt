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
 * Spraak (audioboek, podcast, voice-note) terwijl muziek loopt: stapsgewijs naar 43%.
 * Alleen CONTENT_TYPE_SPEECH op media/unknown. Geen chat, ping, nav of bel.
 * Deelt DuckLane. Laagste cap wint. Geen extra permissie. Android 8+.
 */
object SpeechDuck {
    private const val PREFS = "sounmax_speech_duck"
    private const val CAP_PCT = 43
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "speech")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (speaking(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun speaking(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (EmergencyDuck.alerting(context) || AlarmDuck.ringing(context) || RingDuck.ringing(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { isSpeech(it.audioAttributes) }
    }

    private fun isSpeech(attrs: AudioAttributes): Boolean {
        if (attrs.contentType != AudioAttributes.CONTENT_TYPE_SPEECH) return false
        return attrs.usage == AudioAttributes.USAGE_MEDIA ||
            attrs.usage == AudioAttributes.USAGE_UNKNOWN ||
            attrs.usage == AudioAttributes.USAGE_GAME
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !speaking(context)) return false
        return DuckLane.hold(context, "speech", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || speaking(context)) return false
        if (!DuckLane.heldBy(context, "speech") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "speech")
    }

    fun active(context: Context): Boolean =
        enabled(context) && speaking(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "Spraak-duck: Android 8+"
        !enabled(context) -> "Spraak-duck uit"
        active(context) -> "Spraak, muziek 43%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Spraak-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
