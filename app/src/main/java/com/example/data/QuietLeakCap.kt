package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Telefoon op trillen of stil + muziek boven 60% → cap 50%.
 * Minder lekkage in kantoor, bibliotheek of vergadering.
 */
object QuietLeakCap {
    private const val PREFS = "sounmax_quiet_leak"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 60

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) prefs(context).edit().putBoolean("capped", false).apply()
        else apply(context)
        return label(context)
    }

    fun quietMode(context: Context): Boolean {
        val mode = audio(context).ringerMode
        return mode == AudioManager.RINGER_MODE_SILENT || mode == AudioManager.RINGER_MODE_VIBRATE
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !quietMode(context)) {
            prefs(context).edit().putBoolean("capped", false).apply()
            return false
        }
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putBoolean("capped", true).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && quietMode(context)

    fun label(context: Context): String = when {
        !enabled(context) -> "Stilte-cap uit"
        prefs(context).getBoolean("capped", false) -> "Stilte-cap (50%)"
        active(context) -> "Stilte-cap wacht"
        else -> "Stilte-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
