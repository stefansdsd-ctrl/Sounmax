package com.example.data

import kotlin.math.max
import kotlin.math.min

data class AutoEqPoint(val freq: Float, val gain: Float)

object AutoEqStrength {
    /** Scale AutoEQ curve by strength 0.0-1.0. 1.0 = full correction. */
    fun scale(points: List<AutoEqPoint>, strength: Float): List<AutoEqPoint> {
        val s = strength.coerceIn(0f, 1f)
        return points.map { AutoEqPoint(it.freq, it.gain * s) }
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
        return if (maxGain > 0f) -maxGain else 0f
    }
}
