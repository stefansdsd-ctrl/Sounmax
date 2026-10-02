package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.PowerManager

/**
 * Scherm uit + muziek boven 76% -> cap 58%.
 * Telefoon in de zak blijft anders te hard staan.
 */
object ScreenOffSoft {
    private const val PREFS = "sounmax_screen_off_soft"
    private const val CAP_PCT = 58
    private const val TRIGGER_PCT = 76

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun screenOff(context: Context): Boolean = try {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        !pm.isInteractive
    } catch (_: Exception) {
        false
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !screenOff(context)) return false
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

    fun active(context: Context) = enabled(context) && screenOff(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Scherm-uit-cap uit"
        screenOff(context) -> "Scherm-uit-cap (58%)"
        else -> "Scherm-uit-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
