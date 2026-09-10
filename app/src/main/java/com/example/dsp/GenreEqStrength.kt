package com.example.dsp

/** Schaalt genre-offsets. 100 = vol, 0 = uit. Default 70%. */
object GenreEqStrength {
    @Volatile
    var factor: Float = 0.7f
        private set

    fun setPercent(percent: Int) {
        factor = (percent.coerceIn(0, 100) / 100f)
    }

    fun percent(): Int = (factor * 100f).toInt()

    fun scaleDb(offsets: List<Float>): List<Float> =
        offsets.map { it * factor }

    fun scaleBass(delta: Int): Int = (delta * factor).toInt()

    fun scaleClarity(delta: Float): Float = delta * factor
}
