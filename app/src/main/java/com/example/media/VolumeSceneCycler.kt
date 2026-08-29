package com.example.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Dubbel volume-omhoog binnen 450 ms = volgende scene.
 * Volume wordt teruggezet zodat het nummer niet harder gaat.
 */
class VolumeSceneCycler(
    private val context: Context,
    private val onNextScene: () -> Unit
) {
    private val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var lastUpAt = 0L
    private var lastVolume = -1
    private var enabled = true
    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (!enabled) return
            if (intent?.action != VOLUME_CHANGED) return
            val stream = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
            if (stream != AudioManager.STREAM_MUSIC) return
            val vol = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1)
            if (vol < 0) return
            val prev = lastVolume
            lastVolume = vol
            if (prev < 0 || vol <= prev) return
            val now = SystemClock.elapsedRealtime()
            if (now - lastUpAt in 80..450) {
                lastUpAt = 0L
                try {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, prev, 0)
                    lastVolume = prev
                } catch (_: Exception) {
                }
                buzz()
                onNextScene()
            } else {
                lastUpAt = now
            }
        }
    }

    fun setEnabled(value: Boolean) {
        enabled = value
    }

    fun start() {
        if (registered) return
        lastVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val filter = IntentFilter(VOLUME_CHANGED)
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }
            registered = true
        } catch (_: Exception) {
        }
    }

    fun stop() {
        if (!registered) return
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
        registered = false
    }

    private fun buzz() {
        try {
            val vib = if (Build.VERSION.SDK_INT >= 31) {
                context.getSystemService(VibratorManager::class.java).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= 26) {
                vib.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(40)
            }
        } catch (_: Exception) {
        }
    }

    companion object {
        const val VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION"
    }
}
