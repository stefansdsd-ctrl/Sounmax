package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Assistent-stem terwijl muziek loopt: volume stapsgewijs naar 40%.
 * Deelt DuckLane. Laagste cap wint. Na de stem +2 terug. Geen knal.
 */
object AssistDuck {
    private const val PREFS = "sounmax_assist_duck"
    private const val CAP_PCT = 40
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "assist")
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                if (assisting(app)) apply(app) else tick(app)
                handler.postDelayed(this, 700)
            }
        }
        handler.post(loop)
    }

    fun assisting(context: Context): Boolean {
        if (AlarmDuck.ringing(context) || RingDuck.ringing(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) {
            return false
        }
        return am.isStreamActive(AudioManager.STREAM_ASSISTANT)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !assisting(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        return DuckLane.hold(context, "assist", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || assisting(context)) return false
        if (AlarmDuck.active(context) || RingDuck.active(context)) return false
        if (!DuckLane.heldBy(context, "assist") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "assist")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !assisting(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Assistent-duck uit"
        active(context) -> "Assistent, muziek 40%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Assistent-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
