package com.example.data

import android.content.Context
import android.media.AudioManager
import com.example.media.ListenCap
import com.example.media.WeeklyDose

/** Eén zin waarom volume nu begrensd is. */
object ActiveLimitBanner {
    fun text(context: Context): String {
        SceneHoldPriority.winner(context)?.let { return SceneHoldPriority.label(context) }
        if (PostCallRamp.active(context)) return "Na-bel: volume bouwt op"
        if (ListenCap.enabled(context)) {
            val days = WeeklyListenReport.last7Days(context)
            val weekMin = days.sumOf { it.minutes }
            val db = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                .getInt(WeeklyDose.KEY_DB, 80)
            if (WeeklyDose.exposureRatio(weekMin, db) >= 1.0) {
                return "Gehoorcap actief (weekdosis vol)"
            }
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val vol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        return "Geen limiet · volume ${(100 * vol) / max}%"
    }
}
