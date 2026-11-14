package com.example.dsp

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Spatializer
import android.os.Build
import android.util.Log

/**
 * Controls Android Spatializer (spatial audio / head tracking) for better immersion.
 * Available on Android 12L+ / 13+ with compatible devices.
 */
object SpatialAudioController {
    private const val TAG = "SpatialAudio"
    private var spatializer: Spatializer? = null

    fun isSupported(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.spatializer?.let { it.immersiveAudioLevel != Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_NONE } == true
    }

    fun enable(context: Context, enable: Boolean): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
            Log.w(TAG, "Spatializer requires Android 12L+")
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sp = am.spatializer ?: return false
        spatializer = sp

        return try {
            // Note: actual enable is often controlled by system/settings; this probes availability
            // For full control, apps can request head tracking if device supports.
            val level = sp.immersiveAudioLevel
            Log.i(TAG, "Spatializer level: $level, requested enable=$enable")
            // System-level; user must enable in settings usually. We report capability.
            true
        } catch (e: Exception) {
            Log.e(TAG, "Spatializer error", e)
            false
        }
    }

    fun getStatus(context: Context): String {
        if (!isSupported(context)) return "Spatial audio niet ondersteund"
        return "Spatial audio beschikbaar (Android Spatializer)"
    }
}
