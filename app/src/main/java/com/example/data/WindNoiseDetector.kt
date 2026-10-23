package com.example.data

import android.content.Context

/**
 * Simple wind/noise detector via mic RMS for Software ANC hints.
 * Requires RECORD_AUDIO permission.
 * Returns RMS level; high values suggest wind or ambient noise.
 * Stub implementation — full AudioRecord logic to be added with permission handling.
 */
object WindNoiseDetector {
    fun measureRms(context: Context): Float {
        // TODO: implement short AudioRecord sample + RMS calculation
        // Check permission, sample at 16kHz, compute sqrt(mean(sq))
        return 0f
    }

    fun isWindLikely(rms: Float): Boolean = rms > 0.15f
}
