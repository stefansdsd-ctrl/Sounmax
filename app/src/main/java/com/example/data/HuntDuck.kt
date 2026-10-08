package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Drie korte stiltes (0,2–1,1s) binnen 18s, daarna play boven 55%: 4s naar 56%.
 * Vangt bladeren/skippen waarbij één skip-duck (1,5s / 64%) niet genoeg is.
 * Zit op DuckLane, onder skip (64). Laagste cap wint.
 * Geen extra permissie.
 */
object HuntDuck {
    private const val PREFS = "sounmax_hunt_duck"
    private const val CAP_PCT = 56
    private const val TRIGGER_PCT = 55
    private const val HOLD_MS = 4_000L
    private const val MIN_GAP_MS = 200L
    private const val MAX_GAP_MS = 1_100L
    private const val WINDOW_MS = 18_000L
    private const val NEED = 3
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) {
            prefs(context).edit().remove("until").remove("hits").apply()
            DuckLane.release(context, "hunt")
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
        return DuckLane.hold(context, "hunt", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || holding(context)) return false
        if (!DuckLane.heldBy(context, "hunt") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "hunt")
    }

    fun active(context: Context): Boolean =
        enabled(context) && holding(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        !enabled(context) -> "Zoek-cap uit"
        active(context) -> "Zoek, muziek 56%"
        DuckLane.restoring(context) && DuckLane.heldBy(context, "hunt") -> "Volume komt terug"
        else -> "Zoek-cap aan"
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
                    val hits = hits(p).filter { now - it <= WINDOW_MS }.toMutableList()
                    hits.add(now)
                    val edit = p.edit().putString("hits", hits.joinToString(","))
                    if (hits.size >= NEED) edit.putLong("until", now + HOLD_MS)
                    edit.apply()
                }
            }
            p.edit().putBoolean("was", true).remove("gap").apply()
        } else if (was && gapAt == 0L) {
            p.edit().putBoolean("was", false).putLong("gap", now).apply()
        }
    }

    private fun hits(p: android.content.SharedPreferences): List<Long> =
        p.getString("hits", "").orEmpty().split(",").mapNotNull { it.toLongOrNull() }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
