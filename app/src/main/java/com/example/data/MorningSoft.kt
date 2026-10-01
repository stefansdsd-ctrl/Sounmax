package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * 06:00–09:00 + muziek boven 62% → cap 48%.
 * Zachte start voor de oren, naast de connect-ramp.
 */
object MorningSoft {
    private const val PREFS = "sounmax_morning_soft"
    private const val CAP_PCT = 48
    private const val TRIGGER_PCT = 62

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("capped", false).apply()
        else apply(context)
        return label(context)
    }

    fun inWindow(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour in 6..8
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inWindow()) {
            prefs(context).edit().putBoolean("capped", false).apply()
            return false
        }
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putBoolean("capped", true).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && inWindow()

    fun label(context: Context) = when {
        !enabled(context) -> "Ochtend-cap uit"
        active(context) && prefs(context).getBoolean("capped", false) -> "Ochtend-cap (48%)"
        active(context) -> "Ochtend-cap venster"
        else -> "Ochtend-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
