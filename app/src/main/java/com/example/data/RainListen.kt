package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Regen-cap: handmatig aan bij regen of wind.
 * Muziek boven 62% stapt terug naar 55%, zodat verkeer en spraak hoorbaar blijven.
 */
object RainListen {
    private const val PREFS = "sounmax_rain_listen"
    private const val CAP_PCT = 55
    private const val TRIGGER_PCT = 62

    fun enabled(context: Context) = prefs(context).getBoolean("on", false)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - 2).coerceAtLeast(cap), 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun label(context: Context) =
        if (enabled(context)) "Regen-cap (55%)" else "Regen-cap uit"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
