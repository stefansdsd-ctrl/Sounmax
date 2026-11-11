package com.example.data

import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Adaptive mic-ANC: ambient noise via RMS + zero-crossing voor spraak.
 * Bij spraak → lager ANC (transparantie). Noise-floor + hysteresis.
 * Lagere duty cycle voor batterij.
 */
class AdaptiveMicAnc(
    private val onAncLevel: (Int) -> Unit  // 0=off/transparantie, 1=low, 2=medium, 3=high
) {
    private var noiseFloor = -40.0
    private var lastLevel = 2

    suspend fun startMonitoring() {
        val bufferSize = AudioRecord.getMinBufferSize(8000, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT)
        val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, 8000, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        recorder.startRecording()
        val buffer = ShortArray(bufferSize / 2)

        try {
            while (kotlinx.coroutines.currentCoroutineContext().isActive) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    var sum = 0.0
                    var zcr = 0
                    var prevSign = buffer[0] >= 0
                    for (i in 0 until read) {
                        val s = buffer[i]
                        sum += s * s
                        val sign = s >= 0
                        if (sign != prevSign) zcr++
                        prevSign = sign
                    }
                    val rms = sqrt(sum / read)
                    val db = 20 * log10(rms / 32768.0 + 1e-10)
                    val zcrRate = zcr.toDouble() / read
                    val isSpeech = zcrRate > 0.12 && db > noiseFloor + 3

                    noiseFloor = noiseFloor * 0.96 + db * 0.04
                    val delta = db - noiseFloor
                    val level = when {
                        isSpeech -> 0
                        delta > 16 -> 3
                        delta > 8 -> 2
                        delta > 3 -> 1
                        else -> 0
                    }
                    if (level != lastLevel) {
                        onAncLevel(level)
                        lastLevel = level
                    }
                }
                delay(1200)
            }
        } finally {
            recorder.stop()
            recorder.release()
        }
    }
}
