package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Volume omhoog terwijl muziek stil is, daarna play boven 52% binnen 25s: 5s naar 50%.
 * Vangt de knal van volume-knoppen op het slotscherm vóór play.
 * SpikeGuard ziet alleen sprongen tijdens afspelen. Ochtend-cap is één keer per dag.
 * Zit op DuckLane, onder ochtend (48). Laagste cap wint.
 * Geen extra permissie.
 */
object ArmDuck {
    private const val PREFS = "sounmax_arm_duck"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 52
    private const val HOLD_MS = 5_000L
    private const val ARM_MS = 25_000L
    private const val MIN_JUMP = 2
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) {
            prefs(context).edit().remove("until").remove("armed").apply()
            DuckLane.release(context, "arm")
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
        return DuckLane.hold(context, "arm", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || holding(context)) return false
        if (!DuckLane.heldBy(context, "arm") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "arm")
    }

    fun active(context: Context): Boolean =
        enabled(context) && holding(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        !enabled(context) -> "Wacht-cap uit"
        active(context) -> "Wacht, muziek 50%"
        DuckLane.restoring(context) && DuckLane.heldBy(context, "arm") -> "Volume komt terug"
        else -> "Wacht-cap aan"
    }

    private fun sample(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val playing = am.isMusicActive
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val pct = (100 * cur) / max
        val p = prefs(context)
        val now = System.currentTimeMillis()
        val was = p.getBoolean("was", false)
        val quietVol = p.getInt("quiet", cur)
        val armedUntil = p.getLong("armed", 0L)
        if (!playing) {
            if (was) {
                p.edit().putBoolean("was", false).putInt("quiet", cur).remove("armed").apply()
            } else if (cur - quietVol >= MIN_JUMP) {
                p.edit().putInt("quiet", cur).putLong("armed", now + ARM_MS).apply()
            }
            return
        }
        if (!was && enabled(context) && now < armedUntil && pct >= TRIGGER_PCT && !holding(context)) {
            p.edit().putLong("until", now + HOLD_MS).remove("armed").apply()
        }
        p.edit().putBoolean("was", true).putInt("quiet", cur).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
