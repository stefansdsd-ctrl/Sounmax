package com.example.data

import kotlin.math.*

data class AutoEqPoint(val freq: Float, val gain: Float)

object AutoEqStrength {
    /** Scale AutoEQ curve by strength 0.0-1.0. 1.0 = full correction. */
    fun scale(points: List<AutoEqPoint>, strength: Float): List<AutoEqPoint> {
        val s = strength.coerceIn(0f, 1f)
        return points.map { AutoEqPoint(it.freq, it.gain * s) }
    }

    /** Interpolate to standard 10-band EQ frequencies for smooth application. */
    fun toTenBands(points: List<AutoEqPoint>): List<Float> {
        val bands = listOf(31f, 62f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
        return bands.map { target -> interpolate(points, target) }
    }

    private fun interpolate(points: List<AutoEqPoint>, targetFreq: Float): Float {
        if (points.isEmpty()) return 0f
        val sorted = points.sortedBy { it.freq }
        if (targetFreq <= sorted.first().freq) return sorted.first().gain
        if (targetFreq >= sorted.last().freq) return sorted.last().gain
        for (i in 0 until sorted.size - 1) {
            val a = sorted[i]
            val b = sorted[i + 1]
            if (targetFreq in a.freq..b.freq) {
                val t = (log10(targetFreq) - log10(a.freq)) / (log10(b.freq) - log10(a.freq))
                return a.gain + t * (b.gain - a.gain)
            }
        }
        return 0f
    }

    /** Generate graph data: measured, target, corrected for UI plot. */
    fun graphData(measured: List<AutoEqPoint>, correction: List<AutoEqPoint>): Triple<List<AutoEqPoint>, List<AutoEqPoint>, List<AutoEqPoint>> {
        val target = measured.zip(correction) { m, c ->
            AutoEqPoint(m.freq, m.gain + c.gain)
        }
        return Triple(measured, target, correction)
    }

    /** Simple preamp to avoid clipping after positive gains. */
    fun safePreamp(points: List<AutoEqPoint>): Float {
        val maxGain = points.maxOfOrNull { it.gain } ?: 0f
        return if (maxGain > 0f) -maxGain * 0.8f else 0f
    }
}
