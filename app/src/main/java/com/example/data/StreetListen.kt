package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Straat oversteken: 25 s muziek naar 15%, daarna vorig volume terug.
 * Geen extra hold; safety-holds blijven.
 */
object StreetListen {
    private const val PREFS = "sounmax_street_listen"
    private const val DURATION_MS = 25L * 1000
    private const val TARGET_PCT = 15

    fun active(context: Context): Boolean {
        if (!prefs(context).getBoolean("on", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return false
        }
        return true
    }

    fun cycle(context: Context): String {
        return if (active(context)) {
            stop(context)
            "Straat uit · volume hersteld"
        } else {
            start(context)
            label(context)
        }
    }

    fun start(context: Context) {
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val target = (max * TARGET_PCT) / 100
        prefs(context).edit()
            .putBoolean("on", true)
            .putLong("until", System.currentTimeMillis() + DURATION_MS)
            .putInt("prev", cur)
            .apply()
        if (cur > target) am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
    }

    fun apply(context: Context): Boolean {
        if (!prefs(context).getBoolean("on", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return true
        }
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val target = (max * TARGET_PCT) / 100
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > target) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
            return true
        }
        return false
    }

    fun stop(context: Context) {
        val prev = prefs(context).getInt("prev", -1)
        prefs(context).edit().putBoolean("on", false).remove("until").remove("prev").apply()
        if (prev >= 0) {
            val am = audio(context)
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, prev.coerceIn(0, max), 0)
        }
    }

    fun remainingSec(context: Context): Int =
        (remainingMs(context) / 1000L).toInt().coerceAtLeast(0)

    fun label(context: Context): String =
        if (active(context)) "Straat (${remainingSec(context)}s · 15%)"
        else "Straat uit"

    private fun remainingMs(context: Context): Long {
        val until = prefs(context).getLong("until", 0L)
        return until - System.currentTimeMillis()
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
