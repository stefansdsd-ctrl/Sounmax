package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Korte stilte 0,25–1,1s en meteen weer muziek boven 58%: 1,5s naar 64%.
 * Vangt nummerwissel en skip die pauze-hervat (1,2–8s) mist.
 * Zit op DuckLane, tussen wissel (62) en hervat (68). Laagste cap wint.
 * Geen extra permissie.
 */
object SkipDuck {
    private const val PREFS = "sounmax_skip_duck"
    private const val CAP_PCT = 64
    private const val TRIGGER_PCT = 58
    private const val HOLD_MS = 1_500L
    private const val MIN_GAP_MS = 250L
    private const val MAX_GAP_MS = 1_100L
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) {
            prefs(context).edit().remove("until").apply()
            DuckLane.release(context, "skip")
        }
        return label(context)
    }

    fun ensure(context: Context) {
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                sample(app)
                if (holding(app)) apply(app) else tick(app)
                handler.postDelayed(this, 250)
            }
        }
        handler.post(loop)
    }

    fun holding(context: Context): Boolean {
        if (!enabled(context)) return false
        return System.currentTimeMillis() < prefs(context).getLong("until", 0L)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !holding(context)) return false
        if (EmergencyDuck.alerting(context) || AlarmDuck.ringing(context) || RingDuck.ringing(context)) {
            return false
        }
        return DuckLane.hold(context, "skip", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || holding(context)) return false
        if (!DuckLane.heldBy(context, "skip") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "skip")
    }

    fun active(context: Context): Boolean =
        enabled(context) && holding(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        !enabled(context) -> "Skip-duck uit"
        active(context) -> "Skip, muziek 64%"
        DuckLane.restoring(context) && DuckLane.heldBy(context, "skip") -> "Volume komt terug"
        else -> "Skip-duck aan"
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
                val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
                val pct = (100 * am.getStreamVolume(AudioManager.STREAM_MUSIC)) / max
                if (enabled(context) && gap in MIN_GAP_MS..MAX_GAP_MS && pct >= TRIGGER_PCT) {
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
