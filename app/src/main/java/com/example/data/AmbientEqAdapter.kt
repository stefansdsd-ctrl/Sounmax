package com.example.data

import com.example.dsp.EqPreset
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs

/**
 * Ambient-aware EQ adapter: past EQ licht aan op basis van omgevingsgeluid.
 * Boost mids bij spraak/noise, mild bass cut in stilte voor helderheid.
 * Werkt samen met AdaptiveMicAnc.
 */
class AmbientEqAdapter(
    private val onEqAdjust: (List<Float>) -> Unit  // delta gains for 10 bands
) {
    private var lastDeltas = List(10) { 0f }

    suspend fun start(noiseLevelProvider: suspend () -> Double) {  // dB above floor
        while (kotlinx.coroutines.currentCoroutineContext().isActive) {
            val deltaDb = noiseLevelProvider()
            val newDeltas = when {
                deltaDb > 12 -> listOf(0f, 0.5f, 1.5f, 2.0f, 1.5f, 1.0f, 0.5f, 0f, 0f, 0f)  // mids boost
                deltaDb > 6 -> listOf(0f, 0f, 0.8f, 1.2f, 0.8f, 0.4f, 0f, 0f, 0f, 0f)
                deltaDb < 2 -> listOf(-0.5f, -0.3f, 0f, 0f, 0f, 0f, 0.3f, 0.5f, 0.3f, 0f)  // slight clarity in quiet
                else -> List(10) { 0f }
            }
            if (newDeltas != lastDeltas) {
                onEqAdjust(newDeltas)
                lastDeltas = newDeltas
            }
            delay(1500)
        }
    }

    fun applyToPreset(base: EqPreset, deltas: List<Float>): EqPreset {
        val newGains = base.bandGains.zip(deltas) { g, d -> (g + d).coerceIn(-10f, 10f) }
        return base.copy(bandGains = newGains, name = base.name + " +Ambient")
    }
}
