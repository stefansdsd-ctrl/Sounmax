package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Eén keer per processtart: volume boven 70% stapsgewijs naar 50%.
 * Voorkomt een knal als de app opent met een vergeten hoog volume.
 * Hervatten daarna laat de gebruiker weer omhoog.
 */
object BootQuiet {
    private const val PREFS = "sounmax_boot_quiet"
    private const val TRIGGER_PCT = 70
    private const val CAP_PCT = 50

    @Volatile
    private var appliedThisProcess = false

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) {
            appliedThisProcess = false
            apply(context)
        }
        return label(context)
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || appliedThisProcess) return false
        appliedThisProcess = true
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        val target = (cur - 3).coerceAtLeast(cap)
        if (target >= cur) return false
        am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
        return true
    }

    fun active(context: Context): Boolean {
        if (!enabled(context)) return false
        val last = prefs(context).getLong("last", 0L)
        return System.currentTimeMillis() - last < 12_000L
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Start-cap uit"
        active(context) -> "Start gedempt"
        else -> "Start-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
