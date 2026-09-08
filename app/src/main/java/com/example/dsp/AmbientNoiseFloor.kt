package com.example.dsp

import android.content.Context
import android.media.AudioManager
import kotlin.math.max
import kotlin.math.min

/**
 * Schat omgevingsruis. Zonder mic: volume/ringer/activiteit/RSSI.
 * Met opt-in RECORD_AUDIO: blend mic-RMS (zwaarder gewicht).
 */
object AmbientNoiseFloor {
    private const val PREFS = "scene_automation"

    @Volatile var lastIntensity: Float = 0.45f
        private set

    @Volatile var lastLabel: String = "gemiddeld"
        private set

    @Volatile var lastSource: String = "proxy"
        private set

    fun estimate(
        context: Context,
        activity: String? = null,
        rssiDbm: Int? = null,
        outdoor: Boolean = false
    ): Float {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val music = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val musicMax = max(1, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        val musicNorm = music.toFloat() / musicMax

        val ringer = when (am.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> 0.15f
            AudioManager.RINGER_MODE_VIBRATE -> 0.25f
            else -> 0.45f
        }

        val act = when (activity?.lowercase()) {
            "in_vehicle", "in_car", "driving" -> 0.85f
            "on_bicycle", "cycling" -> 0.70f
            "walking", "on_foot" -> 0.55f
            "running" -> 0.65f
            "still" -> 0.25f
            else -> 0.40f
        }

        val rf = when {
            rssiDbm == null -> 0.4f
            rssiDbm <= -80 -> 0.7f
            rssiDbm <= -65 -> 0.5f
            else -> 0.3f
        }

        val place = if (outdoor) 0.2f else 0f
        val proxy = (act * 0.40f) + (musicNorm * 0.20f) + (ringer * 0.15f) + (rf * 0.15f) + place

        val micOn = MicRmsProbe.enabled(context, PREFS)
        val mic = if (micOn) MicRmsProbe.sample(context) else null
        val raw = if (mic != null) {
            lastSource = "mic"
            mic * 0.70f + proxy * 0.30f
        } else {
            lastSource = "proxy"
            proxy
        }
        val v = min(1f, max(0f, raw))
        lastIntensity = v
        lastLabel = when {
            v >= 0.75f -> "luid"
            v >= 0.50f -> "druk"
            v >= 0.30f -> "gemiddeld"
            else -> "stil"
        }
        return v
    }

    fun adaptiveScale(intensity: Float): Float = 0.35f + intensity * 0.65f
}
