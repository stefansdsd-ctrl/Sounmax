package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Volume boven 90% zakt naar 78%.
 * Voorkomt een per-ongeluk maximum op de headset.
 */
object PeakCap {
    private const val PREFS = "sounmax_peak_cap"
    private const val CAP_PCT = 78
    private const val TRIGGER_PCT = 90

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("capped", false).apply()
        else apply(context)
        return label(context)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) {
            prefs(context).edit().putBoolean("capped", false).apply()
            return false
        }
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) {
            prefs(context).edit().putBoolean("capped", false).apply()
            return false
        }
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putBoolean("capped", true).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && prefs(context).getBoolean("capped", false)

    fun label(context: Context) = when {
        !enabled(context) -> "Piek-cap uit"
        active(context) -> "Piek-cap (78%)"
        else -> "Piek-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
