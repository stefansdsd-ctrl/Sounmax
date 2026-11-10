package com.example.data

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.sqrt

/**
 * Detecteert spraak via mic RMS + eenvoudige zero-crossing voor conversation detection.
 * Activeert automatisch transparantie bij praten (geen extra permissie beyond RECORD_AUDIO).
 */
object ConversationDetector {
    private const val PREFS = "sounmax_conversation"
    private const val KEY_ENABLED = "enabled"
    private const val SAMPLE_RATE = 16000
    private const val THRESHOLD_RMS = 0.08f
    private const val THRESHOLD_ZC = 0.15f

    fun isEnabled(context: Context) = prefs(context).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, on).apply()
    }

    /**
     * Kort meten (niet-blocking ideaal via coroutine). Retourneert true bij waarschijnlijke spraak.
     */
    fun isSpeaking(context: Context): Boolean {
        if (!isEnabled(context)) return false
        return runCatching {
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            if (bufferSize <= 0) return false
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                recorder.release()
                return false
            }
            val buffer = ShortArray(bufferSize / 2)
            recorder.startRecording()
            val read = recorder.read(buffer, 0, buffer.size)
            recorder.stop()
            recorder.release()
            if (read <= 0) return false
            val rms = rms(buffer, read)
            val zc = zeroCrossingRate(buffer, read)
            rms > THRESHOLD_RMS && zc > THRESHOLD_ZC
        }.getOrDefault(false)
    }

    private fun rms(samples: ShortArray, count: Int): Float {
        var sum = 0.0
        for (i in 0 until count) {
            val v = samples[i] / 32768.0
            sum += v * v
        }
        return sqrt(sum / count).toFloat()
    }

    private fun zeroCrossingRate(samples: ShortArray, count: Int): Float {
        if (count < 2) return 0f
        var crossings = 0
        for (i in 1 until count) {
            if ((samples[i - 1] >= 0 && samples[i] < 0) || (samples[i - 1] < 0 && samples[i] >= 0)) {
                crossings++
            }
        }
        return crossings.toFloat() / count
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
