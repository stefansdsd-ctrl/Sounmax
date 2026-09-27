package com.example.media

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Laat de telefoon rinkelen zodat je hem vanaf het horloge terugvindt. */
object FindPhoneHelper {
    private var ringing = false
    private var current: Ringtone? = null
    private val handler = Handler(Looper.getMainLooper())
    private val stopRunnable = Runnable { stop() }

    fun isRinging(): Boolean = ringing

    fun ping(context: Context, durationMs: Long = 8_000L) {
        if (ringing) {
            stop()
            return
        }
        ringing = true
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val ringtone = runCatching { RingtoneManager.getRingtone(context.applicationContext, uri) }.getOrNull()
        current = ringtone
        ringtone?.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        runCatching { ringtone?.play() }
        vibrate(context)
        handler.removeCallbacks(stopRunnable)
        handler.postDelayed(stopRunnable, durationMs)
    }

    fun stop() {
        handler.removeCallbacks(stopRunnable)
        runCatching { current?.stop() }
        current = null
        ringing = false
    }

    private fun vibrate(context: Context) {
        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400, 200, 400), -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(longArrayOf(0, 400, 200, 400, 200, 400), -1)
        }
    }
}
