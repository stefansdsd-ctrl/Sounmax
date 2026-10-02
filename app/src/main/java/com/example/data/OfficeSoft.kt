package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Ma–vr 09:00–17:00 + muziek boven 74% → cap 58%.
 * Kantoorplafond, los van focus-uren en lunch-cap.
 */
object OfficeSoft {
    private const val PREFS = "sounmax_office_soft"
    private const val CAP_PCT = 58
    private const val TRIGGER_PCT = 74

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inWindow(): Boolean {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) return false
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return hour in 9..16
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inWindow()) return false
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

    fun active(context: Context) = enabled(context) && inWindow()

    fun label(context: Context) = when {
        !enabled(context) -> "Kantoor-cap uit"
        inWindow() -> "Kantoor-cap (58%)"
        else -> "Kantoor-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
