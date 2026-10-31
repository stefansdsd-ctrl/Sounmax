package com.example.ai

import com.example.dsp.EqPreset

/**
 * Harman-inspired base EQ for closed-back ANC headphones like Philips TAH6519.
 * Provides a more neutral starting point that AI or user can build on.
 * Gains are approximate based on typical measured responses of similar models.
 */
object HarmanTargetEq {

    private val bands = listOf(31f, 62f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)

    /** Standard Harman-like correction for over-ear ANC (approx). */
    fun neutralPreset(name: String = "Harman Neutral"): EqPreset {
        val gains = listOf(
            3.5f,  // 31 Hz - slight sub boost
            3.0f,  // 62
            1.5f,  // 125
            0.0f,  // 250
            -0.5f, // 500 - slight mid dip for clarity
            1.0f,  // 1k
            2.5f,  // 2k presence
            3.0f,  // 4k
            2.0f,  // 8k
            1.0f   // 16k air
        )
        return EqPreset(
            name = name,
            bandGains = gains,
            bassBoost = 400,
            virtualizer = 350,
            loudness = 480,
            clarity = 8.5f,
            isCustom = true,
            description = "Harman-achtige neutrale basis voor TAH6519. AI of handmatig verder finetunen."
        )
    }

    /** More bass-forward variant. */
    fun warmPreset(): EqPreset = neutralPreset("Harman Warm").copy(
        bandGains = listOf(6.0f, 5.0f, 3.0f, 1.0f, 0.0f, 1.0f, 2.0f, 2.5f, 1.5f, 0.5f),
        description = "Warmere Harman met meer body."
    )

    /** Vocal / clarity focused. */
    fun vocalPreset(): EqPreset = neutralPreset("Harman Vocal").copy(
        bandGains = listOf(1.0f, 1.5f, 1.0f, 0.5f, 1.5f, 3.5f, 4.0f, 3.5f, 2.5f, 1.5f),
        clarity = 9.2f,
        description = "Stem- en podcast-vriendelijk."
    )

    fun allPresets(): List<EqPreset> = listOf(neutralPreset(), warmPreset(), vocalPreset())
}
