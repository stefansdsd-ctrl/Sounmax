package com.example.data

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.session.MediaSessionManager
import com.example.media.SoundMaxNotificationListener

/**
 * Lees- of leer-app actief + volume boven 58% → cap 40%.
 * Gesproken les of audioboek hoeft niet op muziekhardheid.
 */
object StudySoft {
    private const val PREFS = "sounmax_study_soft"
    private const val CAP_PCT = 40
    private const val TRIGGER_PCT = 58

    private val packages = setOf(
        "com.ichi2.anki",
        "com.duolingo",
        "com.amazon.kindle",
        "com.overdrive.mobile.android.libby",
        "com.audible.application",
        "org.readera",
        "com.google.android.apps.books"
    )

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun studyPlaying(context: Context): Boolean {
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
        if (!enabled(context) || !studyPlaying(context)) return false
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

    fun active(context: Context) = enabled(context) && studyPlaying(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Studie-cap uit"
        studyPlaying(context) -> "Studie-cap (40%)"
        else -> "Studie-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
