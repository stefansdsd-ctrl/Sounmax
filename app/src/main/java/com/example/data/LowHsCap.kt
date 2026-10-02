package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Headset-accu ≤18% + muziek boven 60% → cap 42%.
 * Spaart de laatste lading van de TAH6519 zonder GATT.
 */
object LowHsCap {
    private const val PREFS = "sounmax_low_hs_cap"
    private const val CAP_PCT = 42
    private const val TRIGGER_PCT = 60
    private const val LOW = 18

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun low(context: Context): Boolean {
        val pct = HeadsetBatt.percent(context)
        return pct in 0..LOW
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !low(context)) return false
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

    fun active(context: Context) = enabled(context) && low(context)

    fun label(context: Context): String {
        if (!enabled(context)) return "Accu-cap uit"
        val pct = HeadsetBatt.percent(context)
        return if (pct in 0..LOW) "Accu-cap $pct% → 42%" else "Accu-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
