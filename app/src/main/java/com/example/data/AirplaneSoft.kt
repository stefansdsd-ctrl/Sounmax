package com.example.data

import android.content.Context
import android.media.AudioManager
import android.provider.Settings

/**
 * Vliegtuigmodus + muziek boven 68% -> cap 48%.
 * Geen bel/data; volume blijft anders hoog in de cabine of trein.
 */
object AirplaneSoft {
    private const val PREFS = "sounmax_airplane_soft"
    private const val CAP_PCT = 48
    private const val TRIGGER_PCT = 68

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inAirplane(context: Context): Boolean = try {
        Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1
    } catch (_: Exception) {
        false
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inAirplane(context)) return false
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

    fun active(context: Context) = enabled(context) && inAirplane(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Vliegtuig-cap uit"
        inAirplane(context) -> "Vliegtuig-cap (48%)"
        else -> "Vliegtuig-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
