package com.example.data

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Koptelefoon zoeken: 8 s piep via STREAM_MUSIC (Bluetooth) + telefoontrilling.
 * GATT-payload volgt later; dit werkt op elk gekoppeld headset.
 */
object FindBeep {
    private const val PREFS = "sounmax_find_beep"
    private const val DURATION_MS = 8L * 1000
    private val handler = Handler(Looper.getMainLooper())
    private var tone: ToneGenerator? = null
    private var pulse: Runnable? = null

    fun active(context: Context): Boolean {
        if (!prefs(context).getBoolean("on", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return false
        }
        return true
    }

    fun cycle(context: Context): String {
        return if (active(context)) {
            stop(context)
            "Zoekpiep uit"
        } else {
            start(context)
            label(context)
        }
    }

    fun start(context: Context) {
        stopTone()
        prefs(context).edit()
            .putBoolean("on", true)
            .putLong("until", System.currentTimeMillis() + DURATION_MS)
            .apply()
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val bump = (max * 80) / 100
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) < bump) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, bump, 0)
        }
        vibrate(context)
        startToneLoop(context)
        handler.postDelayed({ stop(context) }, DURATION_MS)
    }

    fun apply(context: Context): Boolean {
        if (!prefs(context).getBoolean("on", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return true
        }
        return false
    }

    fun stop(context: Context) {
        prefs(context).edit().putBoolean("on", false).remove("until").apply()
        stopTone()
    }

    fun remainingSec(context: Context): Int =
        (remainingMs(context) / 1000L).toInt().coerceAtLeast(0)

    fun label(context: Context): String =
        if (active(context)) "Zoekpiep (${remainingSec(context)}s)"
        else "Zoekpiep uit"

    private fun startToneLoop(context: Context) {
        runCatching {
            tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        }
        val tick = object : Runnable {
            override fun run() {
                if (!prefs(context).getBoolean("on", false)) return
                runCatching { tone?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400) }
                handler.postDelayed(this, 700)
            }
        }
        pulse = tick
        handler.post(tick)
    }

    private fun stopTone() {
        pulse?.let { handler.removeCallbacks(it) }
        pulse = null
        runCatching { tone?.stopTone() }
        runCatching { tone?.release() }
        tone = null
    }

    private fun vibrate(context: Context) {
        runCatching {
            val v = if (Build.VERSION.SDK_INT >= 31) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 200, 150, 200, 150, 400), -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(800)
            }
        }
    }

    private fun remainingMs(context: Context): Long {
        val until = prefs(context).getLong("until", 0L)
        return until - System.currentTimeMillis()
    }

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
