package com.example.data

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager

/**
 * Niet storen (niet ALL) + muziek boven 65% → cap 45%.
 * Echte NotificationManager-check, los van klok-caps.
 */
object DndSoft {
    private const val PREFS = "sounmax_dnd_soft"
    private const val CAP_PCT = 45
    private const val TRIGGER_PCT = 65

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun inDnd(context: Context): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !inDnd(context)) return false
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

    fun active(context: Context) = enabled(context) && inDnd(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Niet-storen-cap uit"
        inDnd(context) -> "Niet-storen-cap (45%)"
        else -> "Niet-storen-cap aan"
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
