package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.Spatializer
import android.os.Build

/** Spatial audio status voor headphones (API 32+). Chip of info. */
object SpatialAudio {
    fun isSupported(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val spatializer = am.spatializer ?: return false
        return spatializer.immersiveAudioLevel != Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_NONE
    }

    fun isAvailable(context: Context): Boolean {
        if (!isSupported(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.spatializer?.isAvailable == true
    }

    fun isEnabled(context: Context): Boolean {
        if (!isSupported(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.spatializer?.isEnabled == true
    }

    fun canSpatialize(context: Context, attributes: AudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build(), format: AudioFormat = AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(48000).setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build()): Boolean {
        if (!isSupported(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.spatializer?.canBeSpatialized(attributes, format) == true
    }

    fun status(context: Context): String = when {
        !isSupported(context) -> "Spatial: niet ondersteund"
        !isAvailable(context) -> "Spatial: niet beschikbaar"
        !isEnabled(context) -> "Spatial: uit"
        else -> "Spatial: aan"
    }
}
