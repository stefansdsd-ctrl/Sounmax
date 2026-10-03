package com.example.data

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.session.MediaSessionManager
import com.example.media.SoundMaxNotificationListener

/**
 * OV-app (NS/9292/Citymapper/DB) boven 70% → 52%.
 * Omroep blijft hoorbaar zonder muziekpiek. Playback-fallback. Geen Maps/Waze (dat is Nav-cap).
 */
object TransitSoft {
    private const val PREFS = "sounmax_transit_soft"
    private const val CAP_PCT = 52
    private const val TRIGGER_PCT = 70

    private val packages = setOf(
        "nl.ns.android",
        "nl.negentwee",
        "nl.ov9292",
        "com.citymapper.app.release",
        "com.thetransitapp.droid",
        "de.hafas.android.db",
        "com.google.android.apps.transit",
    )

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun transitPlaying(context: Context): Boolean {
        if (viaPlayback(context)) return true
        return try {
            val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val cn = ComponentName(context, SoundMaxNotificationListener::class.java)
            msm.getActiveSessions(cn).any { it.packageName in packages }
        } catch (_: SecurityException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    private fun viaPlayback(context: Context): Boolean {
        return try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val pm = context.packageManager
            am.activePlaybackConfigurations.any { cfg ->
                cfg.playerState == AudioPlaybackConfiguration.PLAYER_STATE_STARTED &&
                    (pm.getPackagesForUid(cfg.clientUid) ?: emptyArray()).any { it in packages }
            }
        } catch (_: Exception) {
            false
        }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !transitPlaying(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && transitPlaying(context)

    fun label(context: Context) = when {
        !enabled(context) -> "OV-cap uit"
        transitPlaying(context) -> "OV-cap (52%)"
        else -> "OV-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
