package com.example.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import com.example.widget.SoundMaxWidget

/**
 * Dubbel play/pause op de headset (binnen 500 ms) = volgende luister-scene.
 * Enkele druk laat het mediaplayer-gedrag ongemoeid.
 */
class HeadsetMediaButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_MEDIA_BUTTON) return
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (!wellness.getBoolean(PREF_KEY, true)) return
        val event = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
        } ?: return
        if (event.action != KeyEvent.ACTION_DOWN) return
        if (event.keyCode != KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE &&
            event.keyCode != KeyEvent.KEYCODE_HEADSETHOOK &&
            event.keyCode != KeyEvent.KEYCODE_MEDIA_PLAY
        ) return
        val now = SystemClock.elapsedRealtime()
        val last = lastDownAt
        lastDownAt = now
        if (now - last in 80..500) {
            lastDownAt = 0L
            buzz(context)
            SoundMaxWidget.cycleScene(context, +1)
        }
    }

    private fun buzz(context: Context) {
        try {
            val vib = if (Build.VERSION.SDK_INT >= 31) {
                context.getSystemService(VibratorManager::class.java).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= 26) {
                vib.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(35)
            }
        } catch (_: Exception) {
        }
    }

    companion object {
        const val PREF_KEY = "media_scene"
        @Volatile
        private var lastDownAt = 0L
    }
}
