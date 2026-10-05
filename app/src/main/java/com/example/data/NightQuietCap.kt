package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Stille uren (22:30–07:00) + muziek actief:
 * boven 65% → 50%, stapsgewijs. Werkt zonder oplader, los van ChargeNightCap.
 */
object NightQuietCap {
    private const val PREFS = "sounmax_night_quiet_cap"
    private const val CAP_PCT = 50
    private const val TRIGGER_PCT = 65

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun quietHours(): Boolean {
        val c = Calendar.getInstance()
        val mins = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
        return mins >= 22 * 60 + 30 || mins < 7 * 60
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !quietHours()) return false
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

    fun active(context: Context) = enabled(context) && quietHours()

    fun label(context: Context) = when {
        !enabled(context) -> "Nacht-cap uit"
        quietHours() -> "Nacht-cap (50%)"
        else -> "Nacht-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
