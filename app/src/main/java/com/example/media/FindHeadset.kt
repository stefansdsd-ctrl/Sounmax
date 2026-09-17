package com.example.media

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import com.example.ble.RealAncController

/**
 * Speelt een herkenbaar twee-toons chirp-patroon om de koptelefoon te vinden.
 * Stopt na 20s of via stop().
 */
object FindHeadset {
    private var tone: ToneGenerator? = null
    private val h = Handler(Looper.getMainLooper())

    fun ping(context: Context, bursts: Int = 8) {
        stop()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val old = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (max * 0.9f).toInt().coerceAtLeast(old), 0)
        tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        var left = bursts
        var high = true
        fun tick() {
            if (left <= 0) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, old, 0)
                stop()
                return
            }
            val kind = if (high) ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD else ToneGenerator.TONE_CDMA_PIP
            tone?.startTone(kind, if (high) 320 else 160)
            high = !high
            left--
            h.postDelayed({ tick() }, if (high) 220 else 480)
        }
        tick()
        RealAncController.findBeep()
        FindHeadsetHelper.ping()
        h.postDelayed({
            am.setStreamVolume(AudioManager.STREAM_MUSIC, old, 0)
            stop()
        }, 20_000L)
    }

    fun stop() {
        h.removeCallbacksAndMessages(null)
        try { tone?.release() } catch (_: Exception) {}
        tone = null
        FindHeadsetHelper.sharedStop()
    }
}
