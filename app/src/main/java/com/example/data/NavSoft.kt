package com.example.data

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.session.MediaSessionManager
import com.example.media.SoundMaxNotificationListener

/**
 * Navigatie-app speelt audio + volume boven 66% → cap 50%.
 * Gesproken aanwijzingen blijven hoorbaar. Zonder sessie: playback-config.
 */
object NavSoft {
    private const val PREFS = "sounmax_nav_soft"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 66

    private val packages = setOf(
        "com.google.android.apps.maps",
        "com.waze",
        "com.sygic.aura",
        "com.here.app.maps",
        "nl.flitsmeister",
        "com.tomtom.gplay.navapp",
        "com.google.android.apps.mapslite"
    )

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun navPlaying(context: Context): Boolean {
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

    private fun viaPlayback(context: Context): Boolean = false

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !navPlaying(context)) return false
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

    fun active(context: Context) = enabled(context) && navPlaying(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Nav-cap uit"
        navPlaying(context) -> "Nav-cap (50%)"
        else -> "Nav-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
