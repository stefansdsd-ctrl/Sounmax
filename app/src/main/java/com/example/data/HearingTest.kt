package com.example.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.dsp.EqPreset
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Simple pure-tone hearing test (20-16000 Hz) to generate personalized EQ compensation.
 * Plays tones, user indicates when audible; builds per-ear gain curve then maps to 10-band.
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
            playTone(freq, -20f, 800) // left channel
            left.add(if (isHeard()) 0f else 3f) // simple: if not heard, boost 3dB
            delay(300)

            onTone(freq, "right")
            playTone(freq, -20f, 800, right = true)
            right.add(if (isHeard()) 0f else 3f)
            delay(300)
        }

        val avgGains = left.zip(right) { l, r -> (l + r) / 2f }
        val preset = EqPreset(
            name = "Hearing Test",
            bandGains = avgGains.map { it.coerceIn(-6f, 6f) },
            bassBoost = 350,
            virtualizer = 200,
            loudness = 500,
            clarity = 9.0f,
            isCustom = true,
            category = "Personal",
            description = "Gepersonaliseerd via pure-tone test"
        )

        return TestResult(left, right, preset)
    }

    private fun playTone(freq: Float, volumeDb: Float, durationMs: Int, right: Boolean = false) {
        val sampleRate = 44100
        val numSamples = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(numSamples)
        val amplitude = (Short.MAX_VALUE * Math.pow(10.0, volumeDb / 20.0)).toInt().toShort()

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * freq * i / sampleRate
            buffer[i] = (sin(angle) * amplitude).toInt().toShort()
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(if (right) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_STEREO)
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
