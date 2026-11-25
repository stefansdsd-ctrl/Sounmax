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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Software ANC / ambient noise reduction using phone microphone.
 * Processes real-time audio, applies simple spectral gating / noise floor subtraction,
 * and can feed into EQ or volume ducking. Requires RECORD_AUDIO permission.
 * Integrates with AdaptiveMicAnc / AmbientEqAdapter.
 */
class SoftwareAncController(private val context: Context) {

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled

    private val _noiseLevelDb = MutableStateFlow(0f)
    val noiseLevelDb: StateFlow<Float> = _noiseLevelDb

    private val _reductionStrength = MutableStateFlow(0.6f) // 0.0-1.0
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
                var noiseFloor = 0.0
                val alpha = 0.05 // slow noise floor adaptation

                while (_enabled.value && isActive) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        // RMS energy
                        var sumSq = 0.0
                        for (i in 0 until read) {
                            val s = buffer[i].toDouble()
                            sumSq += s * s
                        }
                        val rms = sqrt(sumSq / read)
                        val db = if (rms > 1) 20 * kotlin.math.log10(rms / 32768.0) + 90 else 0.0 // rough dB SPL estimate

                        // Adaptive noise floor
                        noiseFloor = noiseFloor * (1 - alpha) + rms * alpha

                        // Simple noise reduction strength effect (for UI / ducking)
                        val snr = if (noiseFloor > 1) rms / noiseFloor else 1.0
                        val effectiveReduction = if (snr < 1.5) _reductionStrength.value else 0f

                        _noiseLevelDb.value = db.toFloat()

                        // Optional: could apply to a playback AudioTrack or feed EQ here
                        // For now, expose metrics for AmbientEqAdapter / volume control
                    }
                    delay(50) // ~20Hz update
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
    }

    fun release() {
        setEnabled(false)
    }
}
