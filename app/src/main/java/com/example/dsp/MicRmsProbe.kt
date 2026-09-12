package com.example.dsp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.example.media.SceneAutomation
import com.example.media.TalkThrough
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Korte, opt-in microfoon-RMS-meting. Geen persistente opname.
 * Prefs-key: mic_rms_enabled (SceneAutomation.PREFS).
 */
object MicRmsProbe {
    const val PREF_ENABLED = "mic_rms_enabled"

    @Volatile var lastRms: Float = -1f
        private set
    @Volatile var lastIntensity: Float = -1f
        private set
    @Volatile var lastAtMs: Long = 0L
        private set

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    fun enabled(context: Context, prefsName: String = SceneAutomation.PREFS): Boolean {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_ENABLED, false) && hasPermission(context)
    }

    fun setEnabled(context: Context, on: Boolean, prefsName: String = SceneAutomation.PREFS) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            .edit().putBoolean(PREF_ENABLED, on).apply()
    }

    /**
     * ~80 ms UNPROCESSED/VOICE_RECOGNITION sample. Thread-safe; skip als recent.
     * Voedt TalkThrough.onRms als talk-through aan staat.
     * @return 0..1 intensiteit, of null bij fout/geen permissie.
     */
    @Synchronized
    fun sample(context: Context, minIntervalMs: Long = defaultInterval(context)): Float? {
        if (!hasPermission(context)) return null
        val now = System.currentTimeMillis()
        if (lastIntensity >= 0f && now - lastAtMs < minIntervalMs) {
            feedTalkThrough(context, lastIntensity)
            return lastIntensity
        }

        val rate = 16_000
        val minBuf = AudioRecord.getMinBufferSize(
            rate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBuf <= 0) return null
        val bufSize = max(minBuf, rate / 6)
        val rec = try {
            AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                rate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufSize
            )
        } catch (_: SecurityException) {
            return null
        } catch (_: Exception) {
            return null
        }
        if (rec.state != AudioRecord.STATE_INITIALIZED) {
            rec.release()
            return null
        }
        val shortBuf = ShortArray(bufSize)
        return try {
            rec.startRecording()
            val n = rec.read(shortBuf, 0, shortBuf.size)
            rec.stop()
            if (n <= 64) {
                lastIntensity.takeIf { it >= 0f }?.also { feedTalkThrough(context, it) }
            } else {
                var acc = 0.0
                var used = 0
                for (i in 0 until n) {
                    val s = shortBuf[i].toDouble()
                    acc += s * s
                    used++
                }
                val rms = sqrt(acc / max(1, used)).toFloat()
                lastRms = rms
                val db = 20f * log10(max(1f, rms))
                val intensity = min(1f, max(0f, (db - 26f) / 46f))
                lastIntensity = intensity
                lastAtMs = now
                feedTalkThrough(context, intensity)
                intensity
            }
        } catch (_: Exception) {
            lastIntensity.takeIf { it >= 0f }
        } finally {
            try { rec.release() } catch (_: Exception) {}
        }
    }

    private fun defaultInterval(context: Context): Long =
        if (TalkThrough.enabled(context)) 1_500L else 8_000L

    private fun feedTalkThrough(context: Context, intensity: Float) {
        if (TalkThrough.enabled(context) && enabled(context)) {
            TalkThrough.onRms(intensity)
        } else if (TalkThrough.isActive()) {
            TalkThrough.release()
        }
    }
}
