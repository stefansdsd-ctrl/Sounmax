package com.example.data

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Software ANC (Quietly-style): uses phone mic + simple spectral subtraction
 * to reduce ambient noise before audio reaches the headphones.
 * Lightweight, no extra hardware. Works with any Bluetooth headset.
 *
 * Pipeline: AudioRecord (VOICE_COMMUNICATION) → energy VAD → simple noise floor subtraction → AudioTrack (MEDIA).
 */
class SoftwareAncController(
    private val scope: CoroutineScope
) {
    private var isRunning = false
    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null

    private val sampleRate = 48000
    private val bufferSize = 1024
    private var noiseFloor = 0.0
    private var isSpeech = false

    fun start() {
        if (isRunning) return
        isRunning = true
        scope.launch(Dispatchers.IO) {
            startPipeline()
        }
    }

    fun stop() {
        isRunning = false
        audioRecord?.stop()
        audioRecord?.release()
        audioTrack?.stop()
        audioTrack?.release()
        audioRecord = null
        audioTrack = null
    }

    private suspend fun startPipeline() {
        val minBuf = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minBuf, bufferSize * 2)
        )

        val outBuf = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        audioTrack = AudioTrack.Builder()
            .setAudioFormat(AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build())
            .setBufferSizeInBytes(maxOf(outBuf, bufferSize * 2))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioRecord?.startRecording()
        audioTrack?.play()

        val buffer = ShortArray(bufferSize)
        while (isRunning && scope.isActive) {
            val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
            if (read > 0) {
                processBuffer(buffer, read)
                audioTrack?.write(buffer, 0, read)
            }
        }
    }

    private fun processBuffer(buffer: ShortArray, length: Int) {
        // Simple energy-based VAD + noise floor tracking
        var energy = 0.0
        for (i in 0 until length) {
            val sample = buffer[i].toDouble()
            energy += sample * sample
        }
        energy = sqrt(energy / length)

        if (!isSpeech && energy < noiseFloor * 1.5) {
            // Update noise floor when quiet
            noiseFloor = noiseFloor * 0.95 + energy * 0.05
        }
        isSpeech = energy > noiseFloor * 2.2

        if (!isSpeech) {
            // Simple subtraction: reduce noise floor components
            val reduction = (noiseFloor * 0.6).toInt().coerceAtMost(800)
            for (i in 0 until length) {
                val s = buffer[i].toInt()
                val absS = abs(s)
                if (absS < reduction * 2) {
                    buffer[i] = (s * 0.4).toInt().toShort()
                }
            }
        }
    }

    fun isActive() = isRunning
}
