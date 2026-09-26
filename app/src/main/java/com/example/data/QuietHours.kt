package com.example.data

import android.content.Context
import android.media.AudioManager
import java.util.Calendar

/**
 * Rusturen: 's nachts volume automatisch naar een zachte cap.
 * Default 22:00–07:00, cap 40%.
 */
object QuietHours {
    private const val PREFS = "sounmax_quiet_hours"
    private const val KEY_ON = "enabled"
    private const val KEY_START = "start_hour"
    private const val KEY_END = "end_hour"
    private const val KEY_CAP = "cap_percent"
    private const val KEY_LAST = "last_apply_ms"

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ON, false)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ON, on).apply()
        if (on) apply(context)
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        return next
    }

    fun startHour(context: Context): Int =
        prefs(context).getInt(KEY_START, 22).coerceIn(0, 23)

    fun endHour(context: Context): Int =
        prefs(context).getInt(KEY_END, 7).coerceIn(0, 23)

    fun capPercent(context: Context): Int =
        prefs(context).getInt(KEY_CAP, 40).coerceIn(20, 80)

    fun setWindow(context: Context, start: Int, end: Int) {
        prefs(context).edit()
            .putInt(KEY_START, start.coerceIn(0, 23))
            .putInt(KEY_END, end.coerceIn(0, 23))
            .apply()
    }

    fun setCapPercent(context: Context, pct: Int) {
        prefs(context).edit().putInt(KEY_CAP, pct.coerceIn(20, 80)).apply()
    }

    fun activeNow(context: Context, hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Boolean {
        if (!enabled(context)) return false
        val start = startHour(context)
        val end = endHour(context)
        return if (start <= end) hour in start until end else hour >= start || hour < end
    }

    fun apply(context: Context): Boolean {
        if (!activeNow(context)) return false
        val now = System.currentTimeMillis()
        val last = prefs(context).getLong(KEY_LAST, 0L)
        if (now - last < 30_000L) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val capIdx = (max * capPercent(context)) / 100
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) <= capIdx) return false
        am.setStreamVolume(AudioManager.STREAM_MUSIC, capIdx, 0)
        prefs(context).edit().putLong(KEY_LAST, now).apply()
        return true
    }

    fun label(context: Context): String {
        val w = "${startHour(context)}u–${endHour(context)}u ${capPercent(context)}%"
        return when {
            !enabled(context) -> "Rusturen uit"
            activeNow(context) -> "Rusturen aan ($w)"
            else -> "Rusturen $w"
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
