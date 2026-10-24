package com.example.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

/**
 * Wind/noise detector via short mic RMS sample for Software ANC hints.
 * Requires RECORD_AUDIO permission.
 * High RMS suggests wind or loud ambient noise.
 */
object WindNoiseDetector {
    private const val SAMPLE_RATE = 16000
    private const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
    private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
    private const val DURATION_MS = 200 // short sample

    fun measureRms(context: Context): Float {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) return 0f

        val minBuf = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING)
        if (minBuf <= 0) return 0f
        val bufferSize = maxOf(minBuf, SAMPLE_RATE * DURATION_MS / 1000 * 2)

        val recorder = try {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL,
                ENCODING,
                bufferSize
            )
        } catch (_: Exception) {
            return 0f
        }

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            return 0f
        }

        val buffer = ShortArray(SAMPLE_RATE * DURATION_MS / 1000)
        return try {
            recorder.startRecording()
            val read = recorder.read(buffer, 0, buffer.size)
            recorder.stop()
            if (read <= 0) 0f else computeRms(buffer, read)
        } catch (_: Exception) {
            0f
        } finally {
            recorder.release()
        }
    }

    private fun computeRms(samples: ShortArray, length: Int): Float {
        var sum = 0.0
        for (i in 0 until length) {
            val n = samples[i] / 32768.0
            sum += n * n
        }
        return sqrt(sum / length).toFloat()
    }

    fun isWindLikely(rms: Float): Boolean = rms > 0.12f
}
