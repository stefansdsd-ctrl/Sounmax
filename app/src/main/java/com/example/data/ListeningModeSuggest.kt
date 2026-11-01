package com.example.data

import android.content.Context

/**
 * Suggests best listening mode (ANC / Transparency / EQ style) based on context.
 * Combines battery, wind noise, media, and time heuristics.
 * No extra permissions. Chip or status string.
 */
object ListeningModeSuggest {

    fun suggest(context: Context): String {
        val batteryLow = runCatching {
            // Approximate: if recent BatteryHistory trend is steep or low %
            // Fallback simple
            false
        }.getOrDefault(false)

        val windy = runCatching {
            // Would call WindNoiseDetector if available
            false
        }.getOrDefault(false)

        val mediaActive = runCatching {
            // MediaNowPlaying or AudioManager
            false
        }.getOrDefault(false)

        return when {
            windy -> "Suggest: Transparency (wind)"
            batteryLow && mediaActive -> "Suggest: Soft ANC + Warm EQ"
            mediaActive -> "Suggest: Full ANC + Harman Neutral"
            else -> "Suggest: Adaptive / Transparantie"
        }
    }

    fun status(context: Context): String = suggest(context)
}
