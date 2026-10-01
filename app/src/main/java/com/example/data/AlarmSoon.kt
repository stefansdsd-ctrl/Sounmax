package com.example.data

import android.app.AlarmManager
import android.content.Context
import android.media.AudioManager

/**
 * Volgende wekker binnen 25 min + muziek boven 55% → cap 40%.
 * Zodat de wekker hoorbaar blijft.
 */
object AlarmSoon {
    private const val PREFS = "sounmax_alarm_soon"
    private const val WINDOW_MS = 25 * 60 * 1000L
    private const val CAP_PCT = 40
    private const val TRIGGER_PCT = 55

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("capped", false).apply()
        else apply(context)
        return label(context)
    }

    fun minutesUntil(context: Context): Int? {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val next = am.nextAlarmClock?.triggerTime ?: return null
        val delta = next - System.currentTimeMillis()
        if (delta <= 0L || delta > WINDOW_MS) return null
        return ((delta + 59_999L) / 60_000L).toInt()
    }

    fun apply(context: Context): Boolean {
        val mins = minutesUntil(context)
        if (!enabled(context) || mins == null) {
            prefs(context).edit().putBoolean("capped", false).apply()
            return false
        }
        val audio = audio(context)
        val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putBoolean("capped", true).putInt("mins", mins).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && minutesUntil(context) != null

    fun label(context: Context): String {
        val mins = minutesUntil(context)
        return when {
            !enabled(context) -> "Wekker-cap uit"
            mins != null && prefs(context).getBoolean("capped", false) -> "Wekker-cap ${mins}m"
            mins != null -> "Wekker over ${mins}m"
            else -> "Wekker-cap aan"
        }
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
