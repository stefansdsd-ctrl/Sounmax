package com.example.data

import android.content.Context

/**
 * Suggests ANC mode based on wind/noise RMS and current battery/trend.
 * Soft ANC or transparency when high wind or low battery.
 */
object NoiseBasedAnc {
    enum class Suggestion { FULL_ANC, SOFT_ANC, TRANSPARENCY, OFF }

    fun suggest(context: Context, currentBattery: Int): Suggestion {
        val rms = WindNoiseDetector.measureRms(context)
        val windy = WindNoiseDetector.isWindLikely(rms)
        val lowBat = currentBattery in 1..15
        val trend = BatteryHistory.trend(context)

        return when {
            windy && lowBat -> Suggestion.TRANSPARENCY
            windy -> Suggestion.SOFT_ANC
            lowBat || trend == BatteryHistory.Trend.DISCHARGING && currentBattery < 30 -> Suggestion.SOFT_ANC
            rms > 0.05f -> Suggestion.FULL_ANC
            else -> Suggestion.OFF
        }
    }

    fun suggestionText(s: Suggestion): String = when (s) {
        Suggestion.FULL_ANC -> "Volle ANC"
        Suggestion.SOFT_ANC -> "Zachte ANC"
        Suggestion.TRANSPARENCY -> "Transparantie"
        Suggestion.OFF -> "Uit"
    }
}
