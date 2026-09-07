package com.example.dsp

/** Schaalt genre-offsets. 100 = vol, 50 = half. Geen stapeling. */
object GenreEqStrength {
    @Volatile
    var factor: Float = 1f
        private set

    fun setPercent(percent: Int) {
        factor = (percent.coerceIn(25, 100) / 100f)
    }

    fun scaleDb(offsets: List<Float>): List<Float> =
        offsets.map { it * factor }

    fun scaleBass(delta: Int): Int = (delta * factor).toInt()

    fun scaleClarity(delta: Float): Float = delta * factor
}
