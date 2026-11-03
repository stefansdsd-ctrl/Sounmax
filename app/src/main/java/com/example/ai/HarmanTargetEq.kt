package com.example.ai

import com.example.dsp.EqPreset

/**
 * Harman-inspired base EQ for closed-back ANC headphones like Philips TAH6519.
 * Better starting points based on typical measured responses and listener preference.
 * AI or user can build on these.
 */
object HarmanTargetEq {

    private val bands = listOf(31f, 62f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)

    /** Standard Harman-like correction for over-ear ANC (approx, refined). */
    fun neutralPreset(name: String = "Harman Neutral"): EqPreset {
        val gains = listOf(
            4.0f,  // 31 Hz - room-like sub
            3.5f,  // 62
            2.0f,  // 125
            0.5f,  // 250
            -0.5f, // 500 - clarity dip
            1.5f,  // 1k
            3.0f,  // 2k presence
            3.5f,  // 4k
            2.5f,  // 8k
            1.5f   // 16k air
        )
        return EqPreset(
            name = name,
            bandGains = gains,
            bassBoost = 450,
            virtualizer = 400,
            loudness = 500,
            clarity = 8.5f,
            isCustom = true,
            description = "Verfijnde Harman-neutrale basis voor TAH6519. Goede start voor AI of handmatig."
        )
    }

    /** More bass-forward variant. */
    fun warmPreset(): EqPreset = neutralPreset("Harman Warm").copy(
        bandGains = listOf(6.5f, 5.5f, 3.5f, 1.5f, 0.0f, 1.0f, 2.0f, 2.5f, 1.5f, 0.5f),
        bassBoost = 650,
        description = "Warmere Harman met meer body en sub."
    )

    /** Vocal / clarity focused. */
    fun vocalPreset(): EqPreset = neutralPreset("Harman Vocal").copy(
        bandGains = listOf(1.5f, 2.0f, 1.5f, 1.0f, 2.0f, 4.0f, 4.5f, 4.0f, 3.0f, 2.0f),
        clarity = 9.5f,
        description = "Stem- en podcast-vriendelijk, meer aanwezigheid."
    )

    /** Movie / immersive with more spatial feel. */
    fun moviePreset(): EqPreset = neutralPreset("Harman Movie").copy(
        bandGains = listOf(5.0f, 4.5f, 2.5f, 0.5f, -1.0f, 1.0f, 2.5f, 3.5f, 3.0f, 2.5f),
        virtualizer = 600,
        bassBoost = 550,
        description = "Film-achtig met meer diepte en spatial hint."
    )

    /** High clarity for detailed listening. */
    fun clarityPreset(): EqPreset = neutralPreset("Harman Clarity").copy(
        bandGains = listOf(2.0f, 2.5f, 1.0f, -0.5f, -1.0f, 2.0f, 4.0f, 4.5f, 4.0f, 3.0f),
        clarity = 9.8f,
        virtualizer = 300,
        description = "Maximale helderheid en detail."
    )

    fun allPresets(): List<EqPreset> = listOf(
        neutralPreset(),
        warmPreset(),
        vocalPreset(),
        moviePreset(),
        clarityPreset()
    )
}
