package com.example.dsp

import android.content.Context
import com.example.ble.RealAncController

/**
 * Software-ANC + doorverwijzing naar [RealAncController] voor hardware.
 * ADAPTIVE schaalt met AmbientNoiseFloor (0–1).
 */
object SoftwareAnc {
    data class Profile(
        val label: String,
        val offsetsDb: List<Float>,
        val speechBoost: Boolean,
        val limiter: Boolean,
        val bassDelta: Int
    )

    fun profile(mode: AncMode, intensity: Float = AmbientNoiseFloor.lastIntensity): Profile = when (mode) {
        AncMode.STRONG -> Profile(
            label = "software-ANC max",
            offsetsDb = offs(2.5f, 2.2f, 1.6f, 0.8f, 0.2f, -0.2f, -0.6f, -1.0f, -1.4f, -1.8f),
            speechBoost = false,
            limiter = true,
            bassDelta = 40
        )
        AncMode.ADAPTIVE -> {
            val s = AmbientNoiseFloor.adaptiveScale(intensity)
            Profile(
                label = "software-ANC adaptief (${AmbientNoiseFloor.lastLabel})",
                offsetsDb = offs(
                    1.4f * s, 1.2f * s, 0.8f * s, 0.4f * s, 0.1f * s,
                    0f, -0.2f * s, -0.4f * s, -0.6f * s, -0.8f * s
                ),
                speechBoost = false,
                limiter = true,
                bassDelta = (20 * s).toInt()
            )
        }
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

    /** Alleen soft-EQ (zonder hardware). */
    fun apply(mode: AncMode, intensity: Float = AmbientNoiseFloor.lastIntensity) {
        val profile = profile(mode, intensity)
        StereoDynamics.init()
        StereoDynamics.applyBands(profile.offsetsDb, profile.offsetsDb)
        StereoDynamics.speechBoost(profile.speechBoost)
        StereoDynamics.safeLimiter(profile.limiter)
    }

    /** Hardware + soft. Gebruik dit vanuit scenes/UI. */
    fun applyWithHardware(context: Context, mode: AncMode, intensity: Float = AmbientNoiseFloor.lastIntensity): Boolean {
        return RealAncController.apply(context, mode, intensity)
    }

    private fun offs(vararg v: Float): List<Float> = v.toList()
}
