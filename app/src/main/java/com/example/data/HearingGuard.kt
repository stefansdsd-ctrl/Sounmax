package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Gehoorbescherming: optionele volumecap + daglimiet (WHO-achtig).
 * Soft: alleen advies. Hard: cap naar maxPercent.
 */
object HearingGuard {
    private const val PREFS = "sounmax_hearing_guard"
    private const val KEY_ON = "enabled"
    private const val KEY_HARD = "hard_cap"
    private const val KEY_MAX = "max_percent"
    private const val KEY_LIMIT_MIN = "daily_limit_min"

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ON, false)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ON, on).apply()
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        return next
    }

    fun hardCap(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HARD, false)

    fun setHardCap(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_HARD, on).apply()
    }

    fun maxPercent(context: Context): Int =
        prefs(context).getInt(KEY_MAX, 70).coerceIn(40, 100)

    fun setMaxPercent(context: Context, pct: Int) {
        prefs(context).edit().putInt(KEY_MAX, pct.coerceIn(40, 100)).apply()
    }

    fun dailyLimitMin(context: Context): Int =
        prefs(context).getInt(KEY_LIMIT_MIN, 180).coerceIn(30, 720)

    fun setDailyLimitMin(context: Context, min: Int) {
        prefs(context).edit().putInt(KEY_LIMIT_MIN, min.coerceIn(30, 720)).apply()
    }

    fun currentVolumePercent(context: Context): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (cur * 100) / max
    }

    fun applyCap(context: Context): Boolean {
        if (!enabled(context) || !hardCap(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val capIdx = (max * maxPercent(context)) / 100
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur > capIdx) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, capIdx, 0)
            return true
        }
        return false
    }

    fun minutesToday(context: Context): Int =
        WeeklyListenReport.last7Days(context).lastOrNull()?.minutes ?: 0

    fun overDailyLimit(context: Context): Boolean =
        enabled(context) && minutesToday(context) >= dailyLimitMin(context)

    fun status(context: Context): String {
        if (!enabled(context)) return "Gehoorwacht uit"
        val vol = currentVolumePercent(context)
        val cap = maxPercent(context)
        val used = minutesToday(context)
        val lim = dailyLimitMin(context)
        val mode = if (hardCap(context)) "hard" else "zacht"
        val warn = when {
            overDailyLimit(context) -> " LIMIET"
            vol > cap -> " VOLUME"
            else -> ""
        }
        return "Gehoorwacht $mode $vol%/$cap% ${used}m/${lim}m$warn"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
