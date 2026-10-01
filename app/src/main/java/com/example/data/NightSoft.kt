package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * 23:00–06:00 + muziek boven 60% → cap 42%.
 * Avond-cap stopt om 23:00; nachtladen werkt alleen aan de lader.
 */
object NightSoft {
    private const val PREFS = "sounmax_night_soft"
    private const val CAP_PCT = 42
    private const val TRIGGER_PCT = 60

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inWindow(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour >= 23 || hour < 6
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
        !enabled(context) -> "Nacht-cap uit"
        inWindow() -> "Nacht-cap (42%)"
        else -> "Nacht-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
