package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Kantoor-cap: werkdagen 09:00–17:00, muziek boven 58% stapt naar 50%.
 */
object OfficeSoft {
    private const val PREFS = "sounmax_office_soft"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 58

    fun enabled(context: Context) = prefs(context).getBoolean("on", false)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inHours(): Boolean {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) return false
        val minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        return minutes in 9 * 60 until 17 * 60
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inHours()) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - 1).coerceAtLeast(cap), 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Kantoor-cap uit"
        inHours() -> "Kantoor-cap (50%)"
        else -> "Kantoor-cap wacht"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
