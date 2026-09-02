package com.example.dsp

/**
 * Software-ANC tot echte Philips GATT-UUIDs bekend zijn.
 * Maskeert rumble / tilt spraak via DynamicsProcessing + EQ-offsets.
 */
object SoftwareAnc {
    data class Profile(
        val label: String,
        val offsetsDb: List<Float>,
        val speechBoost: Boolean,
        val limiter: Boolean,
        val bassDelta: Int
    )

    fun profile(mode: AncMode): Profile = when (mode) {
        AncMode.STRONG -> Profile(
            label = "software-ANC max",
            offsetsDb = offs(2.5f, 2.2f, 1.6f, 0.8f, 0.2f, -0.2f, -0.6f, -1.0f, -1.4f, -1.8f),
            speechBoost = false,
            limiter = true,
            bassDelta = 40
        )
        AncMode.ADAPTIVE -> Profile(
            label = "software-ANC adaptief",
            offsetsDb = offs(1.4f, 1.2f, 0.8f, 0.4f, 0.1f, 0f, -0.2f, -0.4f, -0.6f, -0.8f),
            speechBoost = false,
            limiter = true,
            bassDelta = 20
        )
        AncMode.AMBIENT -> Profile(
            label = "software-transparantie",
            offsetsDb = offs(-2.0f, -1.6f, -0.8f, 0.2f, 1.2f, 2.2f, 2.6f, 2.0f, 0.8f, -0.4f),
            speechBoost = true,
            limiter = true,
            bassDelta = -80
        )
        AncMode.WIND_GUARD -> Profile(
            label = "software-windfilter",
            offsetsDb = offs(-3.0f, -2.6f, -2.0f, -1.2f, -0.2f, 0.6f, 1.0f, 0.8f, 0.2f, -0.4f),
            speechBoost = false,
            limiter = true,
            bassDelta = -60
        )
        AncMode.OFF -> Profile(
            label = "passief",
            offsetsDb = List(10) { 0f },
            speechBoost = false,
            limiter = false,
            bassDelta = 0
        )
    }

    fun apply(mode: AncMode) {
        val profile = profile(mode)
        StereoDynamics.init()
        StereoDynamics.applyBands(profile.offsetsDb, profile.offsetsDb)
        StereoDynamics.speechBoost(profile.speechBoost)
        StereoDynamics.safeLimiter(profile.limiter)
    }

    private fun offs(vararg v: Float): List<Float> = v.toList()
}
