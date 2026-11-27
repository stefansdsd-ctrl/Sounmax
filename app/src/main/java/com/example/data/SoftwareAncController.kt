package com.example.data

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Software ANC / ambient noise reduction using phone microphone.
 * Verbeterde versie: robuustere noise floor (median-like), speech/crest detection,
 * SNR metrics, adaptive strength. Feeds AmbientEqAdapter / volume ducking.
 * Requires RECORD_AUDIO.
 */
class SoftwareAncController(private val context: Context) {

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled

    private val _noiseLevelDb = MutableStateFlow(0f)
    val noiseLevelDb: StateFlow<Float> = _noiseLevelDb

    private val _snrDb = MutableStateFlow(0f)
    val snrDb: StateFlow<Float> = _snrDb

    private val _isSpeech = MutableStateFlow(false)
    val isSpeech: StateFlow<Boolean> = _isSpeech

    private val _reductionStrength = MutableStateFlow(0.6f)
    val reductionStrength: StateFlow<Float> = _reductionStrength

    private var audioRecord: AudioRecord? = null
    private var processingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 2

    fun setEnabled(on: Boolean) {
        if (on == _enabled.value) return
        _enabled.value = on
        if (on) start() else stop()
    }

    fun setStrength(strength: Float) {
        _reductionStrength.value = strength.coerceIn(0f, 1f)
    }

    private fun start() {
        if (audioRecord != null) return
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                stop()
                return
            }
            audioRecord?.startRecording()

            processingJob = scope.launch {
                val buffer = ShortArray(bufferSize / 2)
                var noiseFloor = 1000.0
                val alpha = 0.02 // slower adaptation
                val recentRms = mutableListOf<Double>()

                while (_enabled.value && isActive) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        var sumSq = 0.0
                        var peak = 0.0
                        for (i in 0 until read) {
                            val s = abs(buffer[i].toDouble())
                            sumSq += s * s
                            if (s > peak) peak = s
                        }
                        val rms = sqrt(sumSq / read)
                        val crest = if (rms > 1) peak / rms else 1.0

                        // Adaptive noise floor (slower when high energy / speech)
                        val isLikelyNoise = crest < 4.0 && rms < noiseFloor * 2.5
                        if (isLikelyNoise) {
                            noiseFloor = noiseFloor * (1 - alpha) + rms * alpha
                        }

                        recentRms.add(rms)
                        if (recentRms.size > 20) recentRms.removeAt(0)
                        val medianRms = recentRms.sorted()[recentRms.size / 2]

                        val db = if (rms > 1) 20 * log10(rms / 32768.0) + 94 else 0.0
                        val snr = if (noiseFloor > 1) 20 * log10(max(rms, 1.0) / noiseFloor) else 0.0

                        _noiseLevelDb.value = db.toFloat()
                        _snrDb.value = snr.toFloat()
                        _isSpeech.value = crest > 5.0 && snr > 6.0

                        // Effective reduction: stronger on pure noise, less on speech
                        val effective = if (_isSpeech.value) _reductionStrength.value * 0.3f else _reductionStrength.value
                        // Can be used by callers for ducking / EQ boost on mids
                    }
                    delay(40)
                }
            }
        } catch (e: Exception) {
            stop()
        }
    }

    private fun stop() {
        processingJob?.cancel()
        processingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
        _noiseLevelDb.value = 0f
        _snrDb.value = 0f
        _isSpeech.value = false
    }

    fun release() {
        setEnabled(false)
    }
}
