package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.PowerManager

/**
 * Batterijspaarstand + muziek boven 74% -> cap 56%.
 * Echte PowerManager-check, los van lage-accu-hold.
 */
object SaverSoft {
    private const val PREFS = "sounmax_saver_soft"
    private const val CAP_PCT = 56
    private const val TRIGGER_PCT = 74

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inSaver(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isPowerSaveMode
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inSaver(context)) return false
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

    fun active(context: Context) = enabled(context) && inSaver(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Spaar-cap uit"
        inSaver(context) -> "Spaar-cap (56%)"
        else -> "Spaar-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
