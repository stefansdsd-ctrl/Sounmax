package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * 21:00–23:00 + muziek boven 68% → cap 50%.
 * Zachter dan ochtend-cap, los van nachtladen (alleen bij lader).
 */
object EveningSoft {
    private const val PREFS = "sounmax_evening_soft"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 68

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inWindow(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour in 21..22
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inWindow()) return false
        val am = audio(context)
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

    fun active(context: Context) = enabled(context) && inWindow()

    fun label(context: Context) = when {
        !enabled(context) -> "Avond-cap uit"
        inWindow() -> "Avond-cap (50%)"
        else -> "Avond-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
