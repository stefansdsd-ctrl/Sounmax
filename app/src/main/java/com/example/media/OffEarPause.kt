package com.example.media

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent

/**
 * Soft oordetectie: headset-A2DP aan maar geen muziek voor 25s → pauze-key.
 * Hervat niet automatisch (voorkomt ghost-play). Uit te zetten via prefs.
 */
object OffEarPause {
    private const val PREFS = "soundmax_wellness"
    const val KEY_ENABLED = "off_ear_pause"
    private const val IDLE_MS = 25_000L
    private val h = Handler(Looper.getMainLooper())
    private var started = false
    private var idleSince = 0L
    private var fired = false

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun start(context: Context) {
        if (started) return
        started = true
        val app = context.applicationContext
        fun tick() {
            val am = app.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (am != null && enabled(app)) {
                val a2dp = am.isBluetoothA2dpOn
                val playing = am.isMusicActive
                if (a2dp && !playing) {
                    if (idleSince == 0L) idleSince = System.currentTimeMillis()
                    if (!fired && System.currentTimeMillis() - idleSince >= IDLE_MS) {
                        pause(am)
                        fired = true
                    }
                } else {
                    idleSince = 0L
                    fired = false
                }
            }
            h.postDelayed({ tick() }, 5_000L)
        }
        h.post({ tick() })
    }

    private fun pause(am: AudioManager) {
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE))
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE))
    }
}
