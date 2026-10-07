package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * TalkBack / toegankelijkheids-TTS terwijl muziek loopt: volume stapsgewijs naar 34%.
 * Deelt DuckLane. Laagste cap wint. Na de stem +2 terug. Geen knal.
 * Alleen Android 8+ (STREAM_ACCESSIBILITY).
 */
object TtsDuck {
    private const val PREFS = "sounmax_tts_duck"
    private const val CAP_PCT = 34
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "tts")
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
        if (AlarmDuck.ringing(context) || RingDuck.ringing(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        return am.isStreamActive(AudioManager.STREAM_ACCESSIBILITY)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !speaking(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        return DuckLane.hold(context, "tts", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || speaking(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        if (!DuckLane.heldBy(context, "tts") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "tts")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !speaking(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> "TTS-duck: Android 8+"
        !enabled(context) -> "TTS-duck uit"
        active(context) -> "Voorlezen, muziek 34%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "TTS-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
