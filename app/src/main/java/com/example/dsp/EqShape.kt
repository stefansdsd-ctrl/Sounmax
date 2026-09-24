package com.example.dsp

/** Curve-tools: gladstrijken, spiegelen, centreren, tilt, invert, schaal, shift. */
object EqShape {
    private const val MIN = -12f
    private const val MAX = 12f

    fun applyBands(dsp: AudioDspManager, bands: List<Float>) {
        bands.forEachIndexed { i, g -> dsp.updateBandGain(i, g.coerceIn(MIN, MAX)) }
    }

    fun smooth(dsp: AudioDspManager) {
        val src = dsp.bandGains.value
        if (src.isEmpty()) return
        val out = src.mapIndexed { i, v ->
            val left = src.getOrElse(i - 1) { v }
            val right = src.getOrElse(i + 1) { v }
            (left + v * 2f + right) / 4f
        }
        applyBands(dsp, out)
    }

    fun mirror(dsp: AudioDspManager) {
        applyBands(dsp, dsp.bandGains.value.asReversed())
    }

    fun normalize(dsp: AudioDspManager) {
        val src = dsp.bandGains.value
        if (src.isEmpty()) return
        val mean = src.average().toFloat()
        applyBands(dsp, src.map { it - mean })
    }

    fun tilt(dsp: AudioDspManager, brighter: Boolean) {
        val src = dsp.bandGains.value
        if (src.size < 2) return
        val n = (src.size - 1).toFloat()
        val step = if (brighter) 0.6f else -0.6f
        applyBands(dsp, src.mapIndexed { i, v -> v + step * (i / n * 2f - 1f) })
    }

    fun invert(dsp: AudioDspManager) {
        applyBands(dsp, dsp.bandGains.value.map { -it })
    }

    fun scale(dsp: AudioDspManager, factor: Float) {
        applyBands(dsp, dsp.bandGains.value.map { it * factor })
    }

    fun shift(dsp: AudioDspManager, steps: Int) {
        val src = dsp.bandGains.value
        if (src.isEmpty() || steps == 0) return
        val n = src.size
        val k = ((steps % n) + n) % n
        applyBands(dsp, src.mapIndexed { i, _ -> src[(i - k + n) % n] })
    }
}
