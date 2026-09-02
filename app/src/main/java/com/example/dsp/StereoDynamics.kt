package com.example.dsp

import android.media.audiofx.DynamicsProcessing
import android.os.Build
import android.util.Log

/**
 * Per-kanaal EQ + limiter via DynamicsProcessing (API 28+).
 * Valt stil als de fabrikant geen session-0 effect toestaat.
 */
object StereoDynamics {
    private const val TAG = "StereoDynamics"
    private val FREQS = floatArrayOf(31f, 62f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)

    @Volatile
    private var engine: DynamicsProcessing? = null

    @Volatile
    var available: Boolean = false
        private set

    fun init(): Boolean {
        if (Build.VERSION.SDK_INT < 28) return false
        if (engine != null) return available
        return try {
            val cfg = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                2,
                true, FREQS.size,
                true, 1,
                false, 0,
                true
            ).setPreferredFrameDuration(10f).build()
            engine = DynamicsProcessing(0, 0, cfg).apply { enabled = true }
            available = true
            Log.i(TAG, "DynamicsProcessing 2ch/${FREQS.size} bands actief")
            true
        } catch (e: Exception) {
            available = false
            Log.w(TAG, "DynamicsProcessing niet beschikbaar: ${e.message}")
            false
        }
    }

    fun applyBands(leftDb: List<Float>, rightDb: List<Float>) {
        if (Build.VERSION.SDK_INT < 28) return
        val dp = engine ?: return
        if (!available) return
        try {
            listOf(0 to leftDb, 1 to rightDb).forEach { (ch, gains) ->
                val eq = DynamicsProcessing.Eq(true, true, FREQS.size)
                FREQS.forEachIndexed { i, hz ->
                    val g = gains.getOrElse(i) { 0f }.coerceIn(-12f, 12f)
                    eq.setBand(i, DynamicsProcessing.EqBand(true, hz, g))
                }
                dp.setPreEqByChannelIndex(ch, eq)
            }
        } catch (e: Exception) {
            Log.w(TAG, "setPreEq: ${e.message}")
        }
    }

    fun crossfeed(on: Boolean) {
        if (Build.VERSION.SDK_INT < 28) return
        val dp = engine ?: return
        if (!available) return
        try {
            val offsets = if (on) {
                listOf(1.5f, 1.2f, 0.8f, 0.4f, 0.2f, 0f, -0.4f, -0.8f, -1.2f, -1.6f)
            } else List(FREQS.size) { 0f }
            applyBands(offsets, offsets)
            for (ch in 0..1) {
                val lim = DynamicsProcessing.Limiter(
                    true, true, 0,
                    1f, 40f, 8f,
                    if (on) -2f else 0f,
                    if (on) 0.8f else 0f
                )
                dp.setLimiterByChannelIndex(ch, lim)
            }
        } catch (e: Exception) {
            Log.w(TAG, "crossfeed: ${e.message}")
        }
    }

    fun safeLimiter(on: Boolean) {
        if (Build.VERSION.SDK_INT < 28) return
        val dp = engine ?: return
        if (!available) return
        try {
            for (ch in 0..1) {
                val lim = DynamicsProcessing.Limiter(
                    true, true, 0,
                    1f, 50f, 8f,
                    if (on) -6f else 0f,
                    if (on) 2f else 0f
                )
                dp.setLimiterByChannelIndex(ch, lim)
            }
        } catch (e: Exception) {
            Log.w(TAG, "safeLimiter: ${e.message}")
        }
    }

    fun speechBoost(on: Boolean) {
        if (Build.VERSION.SDK_INT < 28) return
        val dp = engine ?: return
        if (!available) return
        try {
            for (ch in 0..1) {
                val mbc = DynamicsProcessing.Mbc(true, true, 1)
                val band = DynamicsProcessing.MbcBand(
                    true,
                    4000f,
                    10f,
                    80f,
                    if (on) 3f else 1f,
                    if (on) -18f else 0f,
                    6f,
                    -90f,
                    1f,
                    if (on) 2f else 0f,
                    if (on) 2f else 0f
                )
                mbc.setBand(0, band)
                dp.setMbcByChannelIndex(ch, mbc)
                val lim = DynamicsProcessing.Limiter(
                    true, true, 0,
                    1f, 60f, 10f,
                    if (on) -3f else 0f,
                    if (on) 1f else 0f
                )
                dp.setLimiterByChannelIndex(ch, lim)
            }
        } catch (e: Exception) {
            Log.w(TAG, "speechBoost: ${e.message}")
        }
    }

    fun setEnabled(on: Boolean) {
        try { engine?.enabled = on } catch (_: Exception) {}
    }

    fun release() {
        try { engine?.release() } catch (_: Exception) {}
        engine = null
        available = false
    }
}
