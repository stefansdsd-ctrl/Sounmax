package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import java.util.Calendar
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Eerste muziek van de dag boven 55%: 6s naar 48%.
 * Vangt een vergeten luid volume van gisteren. Zit op DuckLane, onder pauze-hervat.
 * Geen extra permissie.
 */
object MorningDuck {
    private const val PREFS = "sounmax_morning_duck"
    private const val CAP_PCT = 48
    private const val TRIGGER_PCT = 55
    private const val HOLD_MS = 6_000L
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) {
            prefs(context).edit().remove("until").apply()
            DuckLane.release(context, "morning")
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
                handler.postDelayed(this, 500)
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
        return DuckLane.hold(context, "morning", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || holding(context)) return false
        if (!DuckLane.heldBy(context, "morning") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "morning")
    }

    fun active(context: Context): Boolean =
        enabled(context) && holding(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        !enabled(context) -> "Ochtend-cap uit"
        active(context) -> "Ochtend, muziek 48%"
        else -> "Ochtend-cap aan"
    }

    private fun sample(context: Context) {
        if (!enabled(context) || holding(context)) return
        val day = dayKey()
        val p = prefs(context)
        if (p.getInt("day", -1) == day) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) {
            p.edit().putInt("day", day).apply()
            return
        }
        p.edit().putInt("day", day).putLong("until", System.currentTimeMillis() + HOLD_MS).apply()
    }

    private fun dayKey(): Int {
        val c = Calendar.getInstance()
        return c.get(Calendar.YEAR) * 1000 + c.get(Calendar.DAY_OF_YEAR)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
