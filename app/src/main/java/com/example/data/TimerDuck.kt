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
 * Keukentimer of countdown terwijl muziek loopt: volume stapsgewijs naar 36%.
 * ANC slikt een korte piep anders. Na de timer bouwt volume terug in stappen van +2.
 * Wacht als Alarm-duck of Bel-duck actief is.
 * Alleen Android 8+ (active playback configs).
 */
object TimerDuck {
    private const val PREFS = "sounmax_timer_duck"
    private const val CAP_PCT = 36
    private const val STEP = 2
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) { clear(context); DuckLane.release(context, "timer") }
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (tickingNow(app)) apply(app) else tick(app)
                handler.postDelayed(this, 400)
            }
        }
        handler.post(loop)
    }

    fun tickingNow(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        if (AlarmDuck.ringing(context) || RingDuck.ringing(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.any { cfg -> isTimer(cfg.audioAttributes) }
    }

    private fun isTimer(attrs: AudioAttributes): Boolean {
        if (attrs.usage == AudioAttributes.USAGE_NOTIFICATION_RINGTONE) return false
        if (attrs.usage == AudioAttributes.USAGE_ALARM) return false
        val sonification = attrs.contentType == AudioAttributes.CONTENT_TYPE_SONIFICATION
        val notify = attrs.usage == AudioAttributes.USAGE_NOTIFICATION ||
            attrs.usage == AudioAttributes.USAGE_NOTIFICATION_EVENT
        return sonification && notify
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !tickingNow(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        return DuckLane.hold(context, "timer", 36)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || tickingNow(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        if (!DuckLane.heldBy(context, "timer")) return false
        return DuckLane.release(context, "timer")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !tickingNow(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Timer-duck uit"
        active(context) -> "Timer, muziek 36%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Timer-duck aan"
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
