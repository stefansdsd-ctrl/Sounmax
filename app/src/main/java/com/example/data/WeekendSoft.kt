package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Za/zo 10:00–18:00 + muziek boven 80% → cap 64%.
 * Woonkamer/gezin, los van doordeweekse ochtend-, lunch- en avond-cap.
 */
object WeekendSoft {
    private const val PREFS = "sounmax_weekend_soft"
    private const val CAP_PCT = 64
    private const val TRIGGER_PCT = 80

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inWindow(): Boolean {
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_WEEK)
        if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) return false
        val mins = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
        return mins in (10 * 60)..(17 * 60 + 59)
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
        !enabled(context) -> "Weekend-cap uit"
        inWindow() -> "Weekend-cap (64%)"
        else -> "Weekend-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}