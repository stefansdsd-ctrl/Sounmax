package com.example.data

import android.content.Context
import android.media.AudioManager
import com.example.dsp.AudioDspManager

/**
 * Soft volume when headphone battery <15%.
 * Optional, under DuckLane priority.
 */
object LowBatteryDuck {
    private const val PREFS = "sounmax_low_bat_duck"
    private const val THRESHOLD = 15
    private const val SOFT_PERCENT = 40

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("enabled", false)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("enabled", on).apply()
    }

    fun applyIfNeeded(context: Context, audio: AudioManager, dsp: AudioDspManager?) {
        if (!isEnabled(context)) return
        val bat = HeadphoneBattery.level(null)
        if (bat in 1..THRESHOLD) {
            val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val soft = (max * SOFT_PERCENT / 100).coerceAtLeast(1)
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) > soft) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, soft, 0)
            }
            dsp?.setVolumeScale(SOFT_PERCENT / 100f)
        }
    }

    fun chip(): String = if (HeadphoneBattery.level(null) in 1..THRESHOLD) "Laag bat soft" else ""
}
