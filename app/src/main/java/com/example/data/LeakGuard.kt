package com.example.data

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager

/**
 * Muziek lekt naar de speaker terwijl een headset (A2DP/BLE/bedraad) actief is:
 * volume in stapjes naar 30%. Geen aanname; alleen echte output-devices.
 */
object LeakGuard {
    private const val PREFS = "sounmax_leak_guard"
    private const val CAP_PCT = 30
    private const val TRIGGER_PCT = 45

    private val headsetTypes = setOf(
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
        AudioDeviceInfo.TYPE_BLE_HEADSET,
        AudioDeviceInfo.TYPE_BLE_SPEAKER,
        AudioDeviceInfo.TYPE_WIRED_HEADSET,
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
        AudioDeviceInfo.TYPE_USB_HEADSET
    )

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun leaking(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val outs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val headset = outs.any { it.type in headsetTypes }
        val speaker = outs.any { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
        return headset && speaker && am.isMusicActive
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !leaking(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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

    fun active(context: Context) = enabled(context) && leaking(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Lek-cap uit"
        leaking(context) -> "Lek-cap (30%)"
        else -> "Lek-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
