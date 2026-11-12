package com.example.data

import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Adaptive mic-ANC v2: betere spraak/wind detectie, snellere response, lagere CPU.
 * Noise-floor tracking + multi-feature classification.
 */
class AdaptiveMicAnc(
    private val onAncLevel: (Int) -> Unit  // 0=off/transparantie, 1=low, 2=medium, 3=high
) {
    private var noiseFloor = -45.0
    private var lastLevel = 2
    private var speechStreak = 0

    suspend fun startMonitoring() {
        val sampleRate = 8000
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT)
        val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2)
        if (recorder.state != AudioRecord.STATE_INITIALIZED) return
        recorder.startRecording()
        val buffer = ShortArray(bufferSize / 2)

        try {
            while (kotlinx.coroutines.currentCoroutineContext().isActive) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    var sumSq = 0.0
                    var zcr = 0
                    var peak = 0
                    var prevSign = buffer[0] >= 0
                    for (i in 0 until read) {
                        val s = buffer[i].toInt()
                        sumSq += s * s
                        peak = maxOf(peak, abs(s))
                        val sign = s >= 0
                        if (sign != prevSign) zcr++
                        prevSign = sign
                    }
                    val rms = sqrt(sumSq / read)
                    val db = 20 * log10(rms / 32768.0 + 1e-10)
                    val zcrRate = zcr.toDouble() / read
                    val crest = if (rms > 1) peak.toDouble() / rms else 1.0

                    // Update noise floor slowly when quiet
                    if (db < noiseFloor + 4) {
                        noiseFloor = noiseFloor * 0.98 + db * 0.02
                    }
                    val delta = db - noiseFloor

                    val isSpeech = zcrRate in 0.08..0.35 && delta > 4 && crest < 8
                    if (isSpeech) speechStreak++ else speechStreak = maxOf(0, speechStreak - 1)

                    val level = when {
                        speechStreak >= 2 || isSpeech -> 0  // force transparency on speech
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
                delay(800)  // sneller maar nog batterijvriendelijk
            }
        } finally {
            try { recorder.stop() } catch (_: Exception) {}
            recorder.release()
        }
    }
}
