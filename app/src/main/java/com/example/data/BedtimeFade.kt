package com.example.data

import android.content.Context
import android.media.AudioManager

/** Bedtime-fade: volume zakt lineair naar 20% in 15/30/45 min + sleep-scene. */
object BedtimeFade {
    private const val PREFS = "sounmax_bedtime_fade"
    private const val KEY_UNTIL = "until_ms"
    private const val KEY_START = "start_ms"
    private const val KEY_START_VOL = "start_vol"
    private const val TARGET_PERCENT = 20

    fun active(context: Context): Boolean =
        prefs(context).getLong(KEY_UNTIL, 0L) > System.currentTimeMillis()

    fun remainingMin(context: Context): Int {
        val left = prefs(context).getLong(KEY_UNTIL, 0L) - System.currentTimeMillis()
        return if (left <= 0) 0 else ((left + 59_999L) / 60_000L).toInt()
    }

    fun apply(context: Context): Boolean {
        if (!active(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val startVol = prefs(context).getInt(KEY_START_VOL, am.getStreamVolume(AudioManager.STREAM_MUSIC))
        val startMs = prefs(context).getLong(KEY_START, System.currentTimeMillis())
        val until = prefs(context).getLong(KEY_UNTIL, startMs)
        val span = (until - startMs).coerceAtLeast(1L)
        val t = ((System.currentTimeMillis() - startMs).toFloat() / span).coerceIn(0f, 1f)
        val target = (max * TARGET_PERCENT) / 100
        val next = (startVol + (target - startVol) * t).toInt().coerceIn(0, max)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) != next) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, next, 0)
        }
        if (!com.example.media.SleepOneTap.isOn(context)) {
            com.example.media.SleepOneTap.toggle(context)
        }
        return true
    }

    fun start(context: Context, minutes: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val now = System.currentTimeMillis()
        prefs(context).edit()
            .putLong(KEY_START, now)
            .putLong(KEY_UNTIL, now + minutes.coerceIn(10, 90) * 60_000L)
            .putInt(KEY_START_VOL, am.getStreamVolume(AudioManager.STREAM_MUSIC))
            .apply()
        apply(context)
    }

    fun stop(context: Context) {
        prefs(context).edit().putLong(KEY_UNTIL, 0L).apply()
    }

    fun cycle(context: Context): String {
        val left = remainingMin(context)
        return when {
            !active(context) -> { start(context, 15); label(context) }
            left <= 15 -> { start(context, 30); label(context) }
            left <= 30 -> { start(context, 45); label(context) }
            else -> { stop(context); label(context) }
        }
    }

    fun label(context: Context): String =
        if (active(context)) "Bedtime-fade ${remainingMin(context)}m → $TARGET_PERCENT%"
        else "Bedtime-fade uit"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
