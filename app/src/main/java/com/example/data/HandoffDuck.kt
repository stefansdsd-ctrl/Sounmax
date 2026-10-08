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
 * Speler wisselt terwijl muziek loopt: kort naar 62% zodat de nieuwe app niet knalt.
 * Zit boven UI-duck (55). Deelt DuckLane. Laagste cap wint.
 * Alleen Android 9+ (clientUid). Geen extra permissie.
 */
object HandoffDuck {
    private const val PREFS = "sounmax_handoff_duck"
    private const val CAP_PCT = 62
    private const val HOLD_MS = 2_500L
    private val ticking = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) {
            prefs(context).edit().remove("until").apply()
            DuckLane.release(context, "handoff")
        }
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        if (!ticking.compareAndSet(false, true)) return
        val app = context.applicationContext
        val loop = object : Runnable {
            override fun run() {
                sample(app)
                if (switching(app)) apply(app) else tick(app)
                handler.postDelayed(this, 500)
            }
        }
        handler.post(loop)
    }

    fun switching(context: Context): Boolean {
        if (!enabled(context)) return false
        return System.currentTimeMillis() < prefs(context).getLong("until", 0L)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !switching(context)) return false
        if (EmergencyDuck.alerting(context) || AlarmDuck.ringing(context) || RingDuck.ringing(context)) {
            return false
        }
        return DuckLane.hold(context, "handoff", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || switching(context)) return false
        if (!DuckLane.heldBy(context, "handoff") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "handoff")
    }

    fun active(context: Context): Boolean =
        enabled(context) && switching(context) &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.P -> "Wissel-duck: Android 9+"
        !enabled(context) -> "Wissel-duck uit"
        active(context) -> "Speler wisselt, muziek 62%"
        DuckLane.restoring(context) && DuckLane.heldBy(context, "handoff") -> "Volume komt terug"
        else -> "Wissel-duck aan"
    }

    private fun sample(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val now = musicPackages(context)
        val p = prefs(context)
        val prev = p.getString("pkgs", null)
        if (prev == null) {
            p.edit().putString("pkgs", now.joinToString(",")).apply()
            return
        }
        val before = prev.split(",").filter { it.isNotBlank() }.toSet()
        p.edit().putString("pkgs", now.joinToString(",")).apply()
        if (!enabled(context) || now.isEmpty() || before.isEmpty()) return
        if (now == before) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return
        p.edit().putLong("until", System.currentTimeMillis() + HOLD_MS).apply()
    }

    private fun musicPackages(context: Context): Set<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return emptySet()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val configs: List<AudioPlaybackConfiguration> = am.activePlaybackConfigurations
        return configs.mapNotNull { cfg ->
            val usage = cfg.audioAttributes.usage
            val media = usage == AudioAttributes.USAGE_MEDIA ||
                usage == AudioAttributes.USAGE_GAME ||
                usage == AudioAttributes.USAGE_UNKNOWN
            if (!media) return@mapNotNull null
            context.packageManager.getPackagesForUid(cfg.clientUid)?.firstOrNull()
        }.toSet()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
