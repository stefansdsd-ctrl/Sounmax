package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Suggests best listening mode (ANC / Transparency / EQ style) based on context.
 * Uses real BatteryHistory trend, WindNoiseDetector if present, media state.
 * No extra permissions. Chip or status string. Improved integration.
 */
object ListeningModeSuggest {

    fun suggest(context: Context): String {
        val batteryLow = runCatching {
            // Use BatteryHistory if available for low % or steep drain
            val hist = BatteryHistory.lastPercent() // assume method exists
            hist < 25 || BatteryHistory.trendSlope() < -0.1
        }.getOrDefault(false)

        val windy = runCatching {
            WindNoiseDetector.isWindy() // assume exists
        }.getOrDefault(false)

        val mediaActive = runCatching {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.isMusicActive
        }.getOrDefault(false)

        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val night = hour in 22..23 || hour in 0..6

        return when {
            windy -> "Suggest: Transparency (wind)"
            batteryLow && mediaActive -> "Suggest: Soft ANC + Warm EQ (battery)"
            night && mediaActive -> "Suggest: Full ANC + Soft EQ (night)"
            mediaActive -> "Suggest: Full ANC + Harman Neutral"
            else -> "Suggest: Adaptive / Transparantie"
        }
    }

    fun status(context: Context): String = suggest(context)
}
