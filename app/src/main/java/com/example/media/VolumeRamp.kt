package com.example.media

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

/** 800 ms volume-ramp bij scene-wissel (issue #6). */
object VolumeRamp {
    private val h = Handler(Looper.getMainLooper())
    private const val STEPS = 8
    private const val STEP_MS = 100L

    fun fadeToPercent(context: Context, targetPct: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val from = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val to = ((max * targetPct.coerceIn(0, 100)) / 100f).toInt().coerceIn(0, max)
        h.removeCallbacksAndMessages(null)
        var i = 1
        fun tick() {
            val v = from + ((to - from) * i) / STEPS
            am.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0)
            if (i < STEPS) {
                i++
                h.postDelayed({ tick() }, STEP_MS)
            }
        }
        tick()
    }
}
