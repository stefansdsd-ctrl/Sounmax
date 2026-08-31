package com.example.media

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.sin

/** Speelt afwisselend L/R pieptonen zodat je de headset kunt vinden. */
class FindHeadsetHelper {
    private var track: AudioTrack? = null
    @Volatile var isPlaying: Boolean = false
        private set

    fun start(durationMs: Int = 12_000) {
        stop()
        val sampleRate = 44100
        val samples = (sampleRate * durationMs / 1000).coerceAtMost(sampleRate * 20)
        val stereo = ShortArray(samples * 2)
        val beepHz = 880.0
        val beepLen = sampleRate / 4
        val gap = sampleRate / 6
        var i = 0
        var left = true
        while (i < samples) {
            val burst = minOf(beepLen, samples - i)
            for (s in 0 until burst) {
                val env = when {
                    s < 200 -> s / 200f
                    s > burst - 200 -> (burst - s) / 200f
                    else -> 1f
                }
                val v = (sin(2.0 * Math.PI * beepHz * s / sampleRate) * 0.55 * env * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                val idx = (i + s) * 2
                if (left) {
                    stereo[idx] = v
                    stereo[idx + 1] = 0
                } else {
                    stereo[idx] = 0
                    stereo[idx + 1] = v
                }
            }
            i += burst + gap
            left = !left
        }
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT
        )
        track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBuf, stereo.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
            .also {
                it.write(stereo, 0, stereo.size)
                it.setVolume(AudioTrack.getMaxVolume())
                it.play()
            }
        isPlaying = true
    }

    fun stop() {
        isPlaying = false
        try {
            track?.stop()
            track?.release()
        } catch (_: Exception) {
        }
        track = null
    }

    companion object {
        private val shared = FindHeadsetHelper()
        fun ping() {
            shared.start()
        }
    }
}
