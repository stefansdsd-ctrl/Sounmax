package com.example.dsp

import android.content.Context
import android.media.AudioManager
import com.example.data.WeeklyListenReport

/** Soft guard: na lange luistertijd oorpauze voorstellen + volume-cap hint. */
object HearingDoseGuard {
    data class Advice(
        val suggestPause: Boolean,
        val capPercent: Int,
        val message: String
    )

    fun advice(context: Context, volumePercent: Int): Advice {
        val today = WeeklyListenReport.last7Days(context).lastOrNull()?.minutes ?: 0
        return when {
            today >= 180 || volumePercent >= 85 -> Advice(
                suggestPause = true,
                capPercent = 60,
                message = "Oorpauze: ${today} min vandaag — volume max 60%"
            )
            today >= 120 || volumePercent >= 75 -> Advice(
                suggestPause = false,
                capPercent = 70,
                message = "Check volume: ${today} min — max 70%"
            )
            else -> Advice(
                suggestPause = false,
                capPercent = 100,
                message = WeeklyListenReport.hint(today)
            )
        }
    }

    fun suggestedSceneId(context: Context, volumePercent: Int): String? =
        if (advice(context, volumePercent).suggestPause) "oorpauze" else null

    fun musicVolumePercent(context: Context): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        return (am.getStreamVolume(AudioManager.STREAM_MUSIC) * 100) / max
    }

    fun adviceNow(context: Context): Advice = advice(context, musicVolumePercent(context))

    /** Zet music-volume terug naar capPercent (alleen omlaag). */
    fun applyCap(context: Context, capPercent: Int = adviceNow(context).capPercent): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val target = (max * capPercent.coerceIn(10, 100)) / 100
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= target) return false
        am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        return true
    }
}
