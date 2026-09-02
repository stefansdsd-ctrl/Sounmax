package com.example.dsp

/**
 * Fletcher–Munson / ISO 226-achtige contour:
 * bij laag volume meer bas + lucht, bij hoog volume vlakker.
 */
object LoudnessContour {
    // 31 62 125 250 500 1k 2k 4k 8k 16k
    private val MAX_BOOST = floatArrayOf(6.0f, 5.0f, 3.5f, 2.0f, 0.8f, 0f, 0.3f, 1.2f, 2.4f, 3.0f)

    fun offsetsDb(volumeRatio: Float): List<Float> {
        val t = (1f - volumeRatio.coerceIn(0.05f, 1f))
        return MAX_BOOST.map { it * t }
    }

    fun volumeRatio(current: Int, max: Int): Float {
        if (max <= 0) return 0.7f
        return (current.toFloat() / max).coerceIn(0f, 1f)
    }
}
