package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Muziek actief, geen A2DP en geen bedrade headset:
 * boven 60% → 45%, stapsgewijs.
 * Voorkomt dat de telefoonspeaker knalt als de headset wegvalt.
 */
object RouteCap {
    private const val PREFS = "sounmax_route_cap"
    private const val CAP_PCT = 45
    private const val TRIGGER_PCT = 60

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    @Suppress("DEPRECATION")
    fun onSpeaker(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return !am.isBluetoothA2dpOn && !am.isWiredHeadsetOn
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !onSpeaker(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - 2).coerceAtLeast(cap), 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && onSpeaker(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Route-cap uit"
        onSpeaker(context) -> "Route-cap (45%)"
        else -> "Route-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
