package com.example.data

import android.content.Context
import android.media.AudioManager

/** Game-hold: game-scene + volume-cap 30/60/120 min. */
object GameHold {
    private const val PREFS = "sounmax_game_hold"
    private const val KEY_UNTIL = "until_ms"
    private const val KEY_CAP = "cap_percent"
    private const val KEY_SAVED_VOL = "saved_vol"

    fun active(context: Context): Boolean =
        prefs(context).getLong(KEY_UNTIL, 0L) > System.currentTimeMillis()

    fun remainingMin(context: Context): Int {
        val left = prefs(context).getLong(KEY_UNTIL, 0L) - System.currentTimeMillis()
        return if (left <= 0) 0 else ((left + 59_999L) / 60_000L).toInt()
    }

    fun capPercent(context: Context) = prefs(context).getInt(KEY_CAP, 80).coerceIn(40, 95)

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
            .putLong(KEY_UNTIL, System.currentTimeMillis() + minutes.coerceIn(15, 180) * 60_000L)
            .putInt(KEY_SAVED_VOL, nowVol)
            .apply()
        if (!com.example.media.GameOneTap.isOn(context)) {
            com.example.media.GameOneTap.toggle(context)
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
        if (com.example.media.GameOneTap.isOn(context)) {
            com.example.media.GameOneTap.toggle(context)
        }
    }

    fun cycle(context: Context): String {
        val left = remainingMin(context)
        return when {
            !active(context) -> {
                start(context, 30)
                label(context)
            }
            left <= 30 -> {
                start(context, 60)
                label(context)
            }
            left <= 60 -> {
                start(context, 120)
                label(context)
            }
            else -> {
                stop(context)
                label(context)
            }
        }
    }

    fun label(context: Context): String =
        if (active(context)) "Game ${remainingMin(context)}m ${capPercent(context)}%"
        else "Game-hold uit"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
