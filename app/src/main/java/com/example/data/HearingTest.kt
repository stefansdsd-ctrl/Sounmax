package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.dsp.EqPreset
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Pure-tone hearing test (31-16kHz) per ear. Genereert gepersonaliseerde 10-band EQ.
 * Verbeterd: correcte L/R panning + adaptieve gain.
 */
object HearingTest {

    data class TestResult(
        val leftGains: List<Float>,
        val rightGains: List<Float>,
        val preset: EqPreset
    )

    private val testFreqs = listOf(31f, 62f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)

    suspend fun runTest(
        context: Context,
        onTone: (freq: Float, ear: String) -> Unit,
        isHeard: () -> Boolean
    ): TestResult {
        val left = mutableListOf<Float>()
        val right = mutableListOf<Float>()

        for (freq in testFreqs) {
            onTone(freq, "left")
            playTone(freq, -20f, 800, pan = -1f)
            left.add(if (isHeard()) 0f else 4f)
            delay(400)

            onTone(freq, "right")
            playTone(freq, -20f, 800, pan = 1f)
            right.add(if (isHeard()) 0f else 4f)
            delay(400)
        }

        val avgGains = left.zip(right) { l, r -> (l + r) / 2f }
        val preset = EqPreset(
            name = "Hearing Test",
            bandGains = avgGains.map { it.coerceIn(-6f, 8f) },
            bassBoost = 300,
            virtualizer = 150,
            loudness = 400,
            clarity = 8.5f,
            isCustom = true,
            category = "Personal",
            description = "Gepersonaliseerd via pure-tone test (L/R)"
        )

        return TestResult(left, right, preset)
    }

    private fun playTone(freq: Float, volumeDb: Float, durationMs: Int, pan: Float = 0f) {
        val sampleRate = 44100
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples * 2) // stereo
        val amplitude = (Short.MAX_VALUE * Math.pow(10.0, volumeDb / 20.0)).toInt().toShort()

        val leftGain = if (pan <= 0) 1f else 0f
        val rightGain = if (pan >= 0) 1f else 0f

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * freq * i / sampleRate
            val sample = (sin(angle) * amplitude).toInt().toShort()
            buffer[i * 2] = (sample * leftGain).toInt().toShort()     // L
            buffer[i * 2 + 1] = (sample * rightGain).toInt().toShort() // R
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .build())
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        Thread.sleep(durationMs.toLong())
        track.stop()
        track.release()
    }
}
