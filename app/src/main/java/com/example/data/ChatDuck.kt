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
 * Berichttoon (chat, mail) terwijl muziek loopt: stapsgewijs naar 44%.
 * Ping dekt alleen USAGE_NOTIFICATION_EVENT. WhatsApp/Signal gebruiken vaak
 * USAGE_NOTIFICATION of COMMUNICATION_INSTANT. Deelt DuckLane. Laagste cap wint.
 * Geen bel, wekker of NL-Alert. Alleen Android 8+.
 */
object ChatDuck {
    private const val PREFS = "sounmax_chat_duck"
    private const val CAP_PCT = 44
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "chat")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (chatting(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun chatting(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (EmergencyDuck.alerting(context) || AlarmDuck.ringing(context) || RingDuck.ringing(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { isChat(it.audioAttributes) }
    }

    private fun isChat(attrs: AudioAttributes): Boolean {
        if (attrs.usage == AudioAttributes.USAGE_NOTIFICATION) return true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        return attrs.usage == AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT ||
            attrs.usage == AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED ||
            attrs.usage == AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !chatting(context)) return false
        return DuckLane.hold(context, "chat", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || chatting(context)) return false
        if (!DuckLane.heldBy(context, "chat") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "chat")
    }

    fun active(context: Context): Boolean =
        enabled(context) && chatting(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "Chat-duck: Android 8+"
        !enabled(context) -> "Chat-duck uit"
        active(context) -> "Bericht, muziek 44%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Chat-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
