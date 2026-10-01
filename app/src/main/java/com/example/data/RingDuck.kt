package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Inkomende beltoon (MODE_RINGTONE) + muziek boven 38% → 20%.
 * Zodat de beltoon door de headset heen blijft.
 */
object RingDuck {
    private const val PREFS = "sounmax_ring_duck"
    private const val CAP_PCT = 20
    private const val TRIGGER_PCT = 38

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("ducked", false).apply()
        else apply(context)
        return label(context)
    }

    fun ringing(context: Context): Boolean {
        val mode = audio(context).mode
        return mode == AudioManager.MODE_RINGTONE || mode == AudioManager.MODE_IN_CALL
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !ringing(context)) {
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

    fun active(context: Context) = enabled(context) && ringing(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Beltoon-duck uit"
        prefs(context).getBoolean("ducked", false) && ringing(context) -> "Beltoon-duck (20%)"
        ringing(context) -> "Beltoon bezig"
        else -> "Beltoon-duck aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
