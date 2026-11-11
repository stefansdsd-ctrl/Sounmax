package com.example.data

import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Adaptive mic-ANC: meet ambient noise via mic RMS en past ANC niveau aan.
 * Geen extra permissie beyond RECORD_AUDIO.
 * Verbeterde versie met noise-floor tracking en hysteresis.
 */
class AdaptiveMicAnc(
    private val onAncLevel: (Int) -> Unit  // 0=off, 1=low, 2=medium, 3=high
) {
    private var noiseFloor = -40.0
    private var lastLevel = 2

    suspend fun startMonitoring() {
        val bufferSize = AudioRecord.getMinBufferSize(16000, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT)
        val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, 16000, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        recorder.startRecording()
        val buffer = ShortArray(bufferSize / 2)

        try {
            while (kotlinx.coroutines.currentCoroutineContext().isActive) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    var sum = 0.0
                    for (i in 0 until read) sum += buffer[i] * buffer[i]
                    val rms = sqrt(sum / read)
                    val db = 20 * log10(rms / 32768.0 + 1e-10)
                    noiseFloor = noiseFloor * 0.95 + db * 0.05  // slow track
                    val delta = db - noiseFloor
                    val level = when {
                        delta > 18 -> 3
                        delta > 10 -> 2
                        delta > 4 -> 1
                        else -> 0
                    }
                    if (level != lastLevel) {
                        onAncLevel(level)
                        lastLevel = level
                    }
                }
                delay(800)
            }
        } finally {
            recorder.stop()
            recorder.release()
        }
    }
}
