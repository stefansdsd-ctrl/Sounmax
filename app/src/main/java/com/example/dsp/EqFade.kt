package com.example.dsp

/** Lineaire interpolatie tussen twee 10-bands curves (scene-wissel). */
object EqFade {
    const val DEFAULT_STEPS = 8
    const val STEP_MS = 40L

    fun lerp(from: FloatArray, to: FloatArray, t: Float): FloatArray {
        val n = minOf(from.size, to.size)
        val out = FloatArray(n)
        val x = t.coerceIn(0f, 1f)
        for (i in 0 until n) out[i] = from[i] + (to[i] - from[i]) * x
        return out
    }

    fun steps(from: FloatArray, to: FloatArray, count: Int = DEFAULT_STEPS): List<FloatArray> {
        if (count <= 1) return listOf(to.copyOf())
        return (1..count).map { i -> lerp(from, to, i / count.toFloat()) }
    }
}
