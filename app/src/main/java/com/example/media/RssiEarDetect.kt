package com.example.media

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent

/**
 * Software-oordetectie via GATT/A2DP-RSSI.
 * Zwak signaal (≤ drempel) + muziek → pauze na HOLD_MS.
 * Sterk signaal na pauze → geen auto-play.
 */
object RssiEarDetect {
    private const val PREFS = "soundmax_wellness"
    const val KEY_ENABLED = "rssi_ear_detect"
    const val KEY_THRESHOLD = "rssi_ear_threshold"
    const val KEY_OFF_EAR = "rssi_off_ear"
    private const val DEFAULT_THRESHOLD = -82
    private const val HOLD_MS = 8_000L
    private var weakSince = 0L
    private var paused = false

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun threshold(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_THRESHOLD, DEFAULT_THRESHOLD)

    fun tick(context: Context, rssi: Int) {
        if (!enabled(context)) return
        if (rssi == 0) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val weak = rssi <= threshold(context)
        val now = System.currentTimeMillis()
        if (weak) {
            if (weakSince == 0L) weakSince = now
            if (!paused && now - weakSince >= HOLD_MS) {
                pause(context)
                paused = true
                prefs.edit().putBoolean(KEY_OFF_EAR, true).apply()
            }
        } else {
            weakSince = 0L
            paused = false
            prefs.edit().putBoolean(KEY_OFF_EAR, false).apply()
        }
    }

    fun isOffEar(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_OFF_EAR, false)

    private fun pause(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        if (!am.isMusicActive) return
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE))
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE))
    }
}
