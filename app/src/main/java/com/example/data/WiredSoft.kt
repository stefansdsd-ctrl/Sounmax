package com.example.data

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager

/**
 * Bedrade headset of USB-audio + muziek boven 78% -> cap 62%.
 * Kabel zit dichter op het oor dan speaker.
 */
object WiredSoft {
    private const val PREFS = "sounmax_wired_soft"
    private const val CAP_PCT = 62
    private const val TRIGGER_PCT = 78

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun wired(context: Context): Boolean = try {
        audio(context).getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { d ->
            d.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                d.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                d.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
                d.type == AudioDeviceInfo.TYPE_USB_DEVICE
        }
    } catch (_: Exception) {
        false
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !wired(context)) return false
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

    fun active(context: Context) = enabled(context) && wired(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Kabel-cap uit"
        wired(context) -> "Kabel-cap (62%)"
        else -> "Kabel-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
