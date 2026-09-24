package com.example.dsp

import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

/** Curve-tools: gladstrijken, spiegelen, centreren, tilt, invert, schaal, shift, clip, isolatie. */
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

    fun clip(dsp: AudioDspManager, limit: Float = 6f) {
        applyBands(dsp, dsp.bandGains.value.map { it.coerceIn(-limit, limit) })
    }

    fun peakNorm(dsp: AudioDspManager, target: Float = 6f) {
        val src = dsp.bandGains.value
        if (src.isEmpty()) return
        val peak = src.maxOf { abs(it) }
        if (peak < 0.05f) return
        val factor = target / peak
        applyBands(dsp, src.map { it * factor })
    }

    fun deadZones(dsp: AudioDspManager, threshold: Float = 0.35f) {
        applyBands(dsp, dsp.bandGains.value.map { if (abs(it) < threshold) 0f else it })
    }

    fun isolateBass(dsp: AudioDspManager, keep: Int = 3) {
        applyBands(dsp, dsp.bandGains.value.mapIndexed { i, v -> if (i < keep) v else 0f })
    }

    fun isolateTreble(dsp: AudioDspManager, keep: Int = 3) {
        val src = dsp.bandGains.value
        val start = max(0, src.size - keep)
        applyBands(dsp, src.mapIndexed { i, v -> if (i >= start) v else 0f })
    }

    fun jitter(dsp: AudioDspManager, amp: Float = 0.4f) {
        applyBands(dsp, dsp.bandGains.value.map { it + Random.nextFloat() * 2f * amp - amp })
    }

    fun isolateMids(dsp: AudioDspManager, keep: Int = 4) {
        val src = dsp.bandGains.value
        if (src.isEmpty()) return
        val start = max(0, (src.size - keep) / 2)
        val end = start + keep
        applyBands(dsp, src.mapIndexed { i, v -> if (i in start until end) v else 0f })
    }

    fun snap(dsp: AudioDspManager, step: Float = 0.5f) {
        applyBands(dsp, dsp.bandGains.value.map { kotlin.math.round(it / step) * step })
    }

    fun vCurve(dsp: AudioDspManager, edge: Float = 1.2f, midCut: Float = 0.8f) {
        val src = dsp.bandGains.value
        if (src.size < 3) return
        val n = (src.size - 1).toFloat()
        applyBands(dsp, src.mapIndexed { i, v ->
            val t = i / n
            val shape = abs(t * 2f - 1f)
            v + edge * shape - midCut * (1f - shape)
        })
    }

    fun scoop(dsp: AudioDspManager, midBoost: Float = 1.2f, edgeCut: Float = 0.6f) {
        val src = dsp.bandGains.value
        if (src.size < 3) return
        val n = (src.size - 1).toFloat()
        applyBands(dsp, src.mapIndexed { i, v ->
            val t = i / n
            val mid = 1f - abs(t * 2f - 1f)
            v + midBoost * mid - edgeCut * (1f - mid)
        })
    }

    fun absGains(dsp: AudioDspManager) {
        applyBands(dsp, dsp.bandGains.value.map { abs(it) })
    }

    fun presence(dsp: AudioDspManager, boost: Float = 1.4f) {
        val src = dsp.bandGains.value
        applyBands(dsp, src.mapIndexed { i, v ->
            val inPresence = i == src.size - 4 || i == src.size - 3
            if (inPresence) v + boost else v
        })
    }
}
