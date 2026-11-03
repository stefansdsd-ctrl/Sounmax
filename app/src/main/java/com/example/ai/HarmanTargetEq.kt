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
            3.5f,  // 31 Hz - controlled sub
            3.0f,  // 62
            1.8f,  // 125
            0.3f,  // 250
            -0.8f, // 500 - clarity dip
            1.8f,  // 1k
            3.2f,  // 2k presence
            3.8f,  // 4k
            2.8f,  // 8k
            1.8f   // 16k air
        )
        return EqPreset(
            name = name,
            bandGains = gains,
            bassBoost = 420,
            virtualizer = 380,
            loudness = 480,
            clarity = 8.7f,
            isCustom = true,
            description = "Verfijnde Harman-neutrale basis voor TAH6519. Goede start voor AI of handmatig."
        )
    }

    /** More bass-forward variant. */
    fun warmPreset(): EqPreset = neutralPreset("Harman Warm").copy(
        bandGains = listOf(6.0f, 5.0f, 3.2f, 1.2f, -0.2f, 0.8f, 1.8f, 2.2f, 1.2f, 0.3f),
        bassBoost = 620,
        description = "Warmere Harman met meer body en sub."
    )

    /** Vocal / clarity focused. */
    fun vocalPreset(): EqPreset = neutralPreset("Harman Vocal").copy(
        bandGains = listOf(1.2f, 1.8f, 1.2f, 0.8f, 1.8f, 4.2f, 4.8f, 4.2f, 3.2f, 2.2f),
        clarity = 9.6f,
        description = "Stem- en podcast-vriendelijk, meer aanwezigheid."
    )

    /** Movie / immersive with more spatial feel. */
    fun moviePreset(): EqPreset = neutralPreset("Harman Movie").copy(
        bandGains = listOf(4.8f, 4.2f, 2.2f, 0.3f, -1.2f, 0.8f, 2.2f, 3.2f, 2.8f, 2.2f),
        virtualizer = 620,
        bassBoost = 520,
        description = "Film-achtig met meer diepte en spatial hint."
    )

    /** High clarity for detailed listening. */
    fun clarityPreset(): EqPreset = neutralPreset("Harman Clarity").copy(
        bandGains = listOf(1.8f, 2.2f, 0.8f, -0.8f, -1.2f, 1.8f, 4.2f, 4.8f, 4.2f, 3.2f),
        clarity = 9.9f,
        virtualizer = 280,
        description = "Maximale helderheid en detail."
    )

    /** Podcast optimized: reduced low-end, boosted mid-presence. */
    fun podcastPreset(): EqPreset = neutralPreset("Harman Podcast").copy(
        bandGains = listOf(0.5f, 1.0f, 0.5f, 1.5f, 3.0f, 4.5f, 4.0f, 2.5f, 1.5f, 1.0f),
        bassBoost = 150,
        clarity = 9.2f,
        description = "Geoptimaliseerd voor podcast en spraak, minder bass-masking."
    )

    /** Gaming: enhanced footsteps and immersion. */
    fun gamingPreset(): EqPreset = neutralPreset("Harman Gaming").copy(
        bandGains = listOf(2.5f, 2.0f, 1.0f, -0.5f, -1.5f, 2.5f, 5.0f, 4.5f, 3.5f, 2.5f),
        virtualizer = 700,
        clarity = 9.0f,
        description = "Gaming-focus: betere footsteps en spatial awareness."
    )

    fun allPresets(): List<EqPreset> = listOf(
        neutralPreset(),
        warmPreset(),
        vocalPreset(),
        moviePreset(),
        clarityPreset(),
        podcastPreset(),
        gamingPreset()
    )
}
