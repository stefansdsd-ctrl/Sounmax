package com.example.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

/**
 * Speelt een luid chirp-patroon om de koptelefoon te vinden.
 * Werkt alleen als de headset verbonden is als audio-output.
 */
object FindHeadset {
    private var tone: ToneGenerator? = null
    private val h = Handler(Looper.getMainLooper())

    fun ping(context: Context, bursts: Int = 6) {
        stop()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val old = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (max * 0.85f).toInt().coerceAtLeast(old), 0)
        tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        var left = bursts
        fun tick() {
            if (left <= 0) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, old, 0)
                stop()
                return
            }
            tone?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 280)
            left--
            h.postDelayed({ tick() }, 450)
        }
        tick()
    }

    fun stop() {
        h.removeCallbacksAndMessages(null)
        try { tone?.release() } catch (_: Exception) {}
        tone = null
    }
}
