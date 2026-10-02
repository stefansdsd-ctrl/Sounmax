package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Muziek boven 62% langer dan 50 min → cap 52%.
 * Aanvulling op Oorpauze: geen stilte, wel een plafond na een lange sessie.
 */
object SessionCap {
    private const val PREFS = "sounmax_session_cap"
    private const val CAP_PCT = 52
    private const val TRIGGER_PCT = 62
    private const val MINUTES = 50

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().remove("since").apply()
        if (next) apply(context)
        return label(context)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val loud = cur > (max * TRIGGER_PCT) / 100
        val now = System.currentTimeMillis()
        val p = prefs(context)
        if (!loud) {
            if (p.contains("since")) p.edit().remove("since").apply()
            return false
        }
        val since = p.getLong("since", 0L)
        if (since == 0L) {
            p.edit().putLong("since", now).apply()
            return false
        }
        if (now - since < MINUTES * 60_000L) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            p.edit().putLong("capped", now).putLong("since", now).apply()
            return true
        }
        return false
    }

    fun active(context: Context): Boolean {
        if (!enabled(context)) return false
        val since = prefs(context).getLong("since", 0L)
        return since > 0L && System.currentTimeMillis() - since >= MINUTES * 60_000L
    }

    fun label(context: Context): String {
        if (!enabled(context)) return "Sessie-cap uit"
        val since = prefs(context).getLong("since", 0L)
        if (since == 0L) return "Sessie-cap aan"
        val min = ((System.currentTimeMillis() - since) / 60_000L).toInt()
        return if (min >= MINUTES) "Sessie-cap ${min}m → 52%" else "Sessie-cap ${min}m"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
