package com.example.data

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

/**
 * Echte route-check: A2DP, SCO, bedraad, speaker.
 * Als muziek naar de telefoonleaker gaat terwijl de headset weg is, dempt hij muziek 2 stappen.
 */
object BudHealth {
    private const val PREFS = "sounmax_bud_health"

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        return label(context)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val a2dp = hasType(am, AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) || am.isBluetoothA2dpOn
        val sco = am.isBluetoothScoOn
        val wired = am.isWiredHeadsetOn
        val speaker = am.isSpeakerphoneOn
        val leaking = am.isMusicActive && !a2dp && !wired && (speaker || !sco)
        prefs(context).edit()
            .putBoolean("a2dp", a2dp)
            .putBoolean("sco", sco)
            .putBoolean("wired", wired)
            .putBoolean("leak", leaking)
            .putLong("last", System.currentTimeMillis())
            .apply()
        if (!leaking) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val next = (cur - 2).coerceAtLeast(1).coerceAtMost(max)
        if (next == cur) return false
        am.setStreamVolume(AudioManager.STREAM_MUSIC, next, 0)
        return true
    }

    fun label(context: Context): String {
        if (!enabled(context)) return "Headset-check uit"
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val a2dp = hasType(am, AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) || am.isBluetoothA2dpOn
        val wired = am.isWiredHeadsetOn
        val sco = am.isBluetoothScoOn
        return when {
            a2dp && sco -> "Headset: muziek + gesprek"
            a2dp -> "Headset: A2DP"
            wired -> "Headset: bedraad"
            sco -> "Headset: alleen gesprek"
            else -> "Headset: niet gekoppeld"
        }
    }

    private fun hasType(am: AudioManager, type: Int): Boolean {
        if (Build.VERSION.SDK_INT < 23) return false
        return am.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { it.type == type }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
