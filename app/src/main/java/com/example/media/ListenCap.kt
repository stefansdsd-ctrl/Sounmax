package com.example.media

import android.content.Context
import android.media.AudioManager
import android.widget.Toast
import com.example.data.WeeklyListenReport
import com.example.widget.SoundMaxWidget

/**
 * Soft cap: bij WHO-weekdosis ≥ 100% volume −2 stappen (max 1×/uur).
 */
object ListenCap {
    private const val PREFS = "soundmax_wellness"
    private const val KEY_LAST_NUDGE = "listen_cap_nudge_ms"
    private const val KEY_ENABLED = "listen_cap_enabled"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun tick(context: Context) {
        val am = context.getSystemService(AudioManager::class.java) ?: return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val vol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val pct = (vol * 100) / max
        WeeklyListenReport.addMinute(context, pct)
        WeeklyDose.remember(
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE),
            minutes = 1,
            estimatedDb = 70 + (pct * 20 / 100)
        )
        if (!enabled(context)) return
        val days = WeeklyListenReport.last7Days(context)
        val weekMin = days.sumOf { it.minutes }
        val db = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(WeeklyDose.KEY_DB, 80)
        if (WeeklyDose.exposureRatio(weekMin, db) < 1.0) return
        val now = System.currentTimeMillis()
        val last = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_NUDGE, 0L)
        if (now - last < 60 * 60_000L) return
        val next = (vol - 2).coerceAtLeast((max * 0.4f).toInt())
        if (next >= vol) return
        runCatching { am.setStreamVolume(AudioManager.STREAM_MUSIC, next, 0) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong(KEY_LAST_NUDGE, now).apply()
        Toast.makeText(context, "Gehoorcap: volume iets lager (weekdosis vol)", Toast.LENGTH_SHORT).show()
        SoundMaxWidget.refreshAll(context)
    }
}
