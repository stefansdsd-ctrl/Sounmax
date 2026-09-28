package com.example.data

import android.content.Context
import android.media.AudioManager

/** Vlucht-hold: plane-scene + volume-cap voor 2/4/6/8 uur. */
object FlightHold {
    private const val PREFS = "sounmax_flight_hold"
    private const val KEY_UNTIL = "until_ms"
    private const val KEY_CAP = "cap_percent"
    private const val KEY_SAVED_VOL = "saved_vol"
    private const val KEY_MINUTES = "minutes"

    fun active(context: Context): Boolean =
        prefs(context).getLong(KEY_UNTIL, 0L) > System.currentTimeMillis()

    fun remainingMin(context: Context): Int {
        val left = prefs(context).getLong(KEY_UNTIL, 0L) - System.currentTimeMillis()
        return if (left <= 0) 0 else ((left + 59_999L) / 60_000L).toInt()
    }

    fun capPercent(context: Context) = prefs(context).getInt(KEY_CAP, 40).coerceIn(20, 70)

    fun apply(context: Context): Boolean {
        if (!active(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val capIdx = (max * capPercent(context)) / 100
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > capIdx) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, capIdx, 0)
        }
        return true
    }

    fun start(context: Context, minutes: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val nowVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        prefs(context).edit()
            .putLong(KEY_UNTIL, System.currentTimeMillis() + minutes.coerceIn(30, 12 * 60) * 60_000L)
            .putInt(KEY_SAVED_VOL, nowVol)
            .putInt(KEY_MINUTES, minutes)
            .apply()
        if (!com.example.media.FlightOneTap.isOn(context)) {
            com.example.media.FlightOneTap.toggle(context)
        }
        apply(context)
    }

    fun stop(context: Context) {
        val saved = prefs(context).getInt(KEY_SAVED_VOL, -1)
        prefs(context).edit().putLong(KEY_UNTIL, 0L).apply()
        if (saved >= 0) {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, saved.coerceIn(0, max), 0)
        }
        if (com.example.media.FlightOneTap.isOn(context)) {
            com.example.media.FlightOneTap.toggle(context)
        }
    }

    fun cycle(context: Context): String {
        val left = remainingMin(context)
        return when {
            !active(context) -> {
                start(context, 120)
                label(context)
            }
            left <= 120 -> {
                start(context, 240)
                label(context)
            }
            left <= 240 -> {
                start(context, 360)
                label(context)
            }
            left <= 360 -> {
                start(context, 480)
                label(context)
            }
            else -> {
                stop(context)
                label(context)
            }
        }
    }

    fun label(context: Context): String =
        if (active(context)) {
            val h = remainingMin(context) / 60
            val m = remainingMin(context) % 60
            if (h > 0) "Vlucht ${h}u${if (m > 0) "${m}m" else ""} ${capPercent(context)}%"
            else "Vlucht ${m}m ${capPercent(context)}%"
        } else "Vlucht uit"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
