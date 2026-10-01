package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * WhatsApp/Teams-spraak (MODE_IN_COMMUNICATION) + muziek boven 34% → 16%.
 * Beltoon-duck dekt alleen RINGTONE/IN_CALL; dit dekt VoIP.
 */
object CommDuck {
    private const val PREFS = "sounmax_comm_duck"
    private const val CAP_PCT = 16
    private const val TRIGGER_PCT = 34

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("ducked", false).apply()
        else apply(context)
        return label(context)
    }

    fun inComm(context: Context): Boolean =
        audio(context).mode == AudioManager.MODE_IN_COMMUNICATION

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inComm(context)) {
            prefs(context).edit().putBoolean("ducked", false).apply()
            return false
        }
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit()
                .putBoolean("ducked", true)
                .putLong("last", System.currentTimeMillis())
                .apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && inComm(context)

    fun label(context: Context) = when {
        !enabled(context) -> "VoIP-duck uit"
        prefs(context).getBoolean("ducked", false) && inComm(context) -> "VoIP-duck (16%)"
        inComm(context) -> "VoIP bezig"
        else -> "VoIP-duck aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
