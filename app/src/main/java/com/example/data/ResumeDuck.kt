package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Muziek valt kort weg en komt terug: 2s naar 68% zodat reclame of hervatten niet knalt.
 * Zit boven wissel-duck (62). Deelt DuckLane. Laagste cap wint.
 * Geen extra permissie. Alle Android-versies met isMusicActive.
 */
object ResumeDuck {
    private const val PREFS = "sounmax_resume_duck"
    private const val CAP_PCT = 68
    private const val HOLD_MS = 2_000L
    private const val MIN_GAP_MS = 1_200L
    private const val MAX_GAP_MS = 8_000L
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) {
            prefs(context).edit().remove("until").apply()
            DuckLane.release(context, "resume")
        }
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                sample(app)
                if (resuming(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun resuming(context: Context): Boolean {
        if (!enabled(context)) return false
        return System.currentTimeMillis() < prefs(context).getLong("until", 0L)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !resuming(context)) return false
        if (EmergencyDuck.alerting(context) || AlarmDuck.ringing(context) || RingDuck.ringing(context)) {
            return false
        }
        return DuckLane.hold(context, "resume", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || resuming(context)) return false
        if (!DuckLane.heldBy(context, "resume") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "resume")
    }

    fun active(context: Context): Boolean =
        enabled(context) && resuming(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        !enabled(context) -> "Pauze-hervat uit"
        active(context) -> "Hervat, muziek 68%"
        DuckLane.restoring(context) && DuckLane.heldBy(context, "resume") -> "Volume komt terug"
        else -> "Pauze-hervat aan"
    }

    private fun sample(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val playing = am.isMusicActive
        val p = prefs(context)
        val now = System.currentTimeMillis()
        val was = p.getBoolean("was", false)
        val gapAt = p.getLong("gap", 0L)
        if (playing) {
            if (!was && gapAt > 0L) {
                val gap = now - gapAt
                if (enabled(context) && gap in MIN_GAP_MS..MAX_GAP_MS) {
                    p.edit().putLong("until", now + HOLD_MS).apply()
                }
            }
            p.edit().putBoolean("was", true).remove("gap").apply()
        } else if (was && gapAt == 0L) {
            p.edit().putBoolean("was", false).putLong("gap", now).apply()
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
