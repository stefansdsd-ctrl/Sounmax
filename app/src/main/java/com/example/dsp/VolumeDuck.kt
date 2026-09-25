package com.example.dsp

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

/** Tijdelijke volume-dip (omroep, kassa, deur) en automatisch herstel. */
object VolumeDuck {
    private const val PREF = "sounmax_volume_duck"
    private const val KEY_SAVED = "saved_index"
    private const val KEY_UNTIL = "until_ms"
    const val DEFAULT_MS = 15_000L
    const val DUCK_PERCENT = 25

    private val main = Handler(Looper.getMainLooper())
    private var restoreTask: Runnable? = null

    fun isActive(context: Context): Boolean {
        val until = prefs(context).getLong(KEY_UNTIL, 0L)
        return until > System.currentTimeMillis()
    }

    fun remainingSec(context: Context): Int {
        val until = prefs(context).getLong(KEY_UNTIL, 0L)
        val left = ((until - System.currentTimeMillis()) / 1000L).toInt()
        return left.coerceAtLeast(0)
    }

    fun start(context: Context, durationMs: Long = DEFAULT_MS): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= 0) return false
        cancelPending()
        prefs(context).edit()
            .putInt(KEY_SAVED, cur)
            .putLong(KEY_UNTIL, System.currentTimeMillis() + durationMs)
            .apply()
        val target = (max * DUCK_PERCENT / 100).coerceAtLeast(0).coerceAtMost(cur)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        val app = context.applicationContext
        restoreTask = Runnable { restore(app) }
        main.postDelayed(restoreTask!!, durationMs)
        return true
    }

    fun restore(context: Context): Boolean {
        cancelPending()
        val p = prefs(context)
        val saved = p.getInt(KEY_SAVED, -1)
        p.edit().remove(KEY_SAVED).remove(KEY_UNTIL).apply()
        if (saved < 0) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, saved.coerceIn(0, max), 0)
        return true
    }

    fun toggle(context: Context): Boolean =
        if (isActive(context)) {
            restore(context)
            false
        } else {
            start(context)
            true
        }

    private fun cancelPending() {
        restoreTask?.let { main.removeCallbacks(it) }
        restoreTask = null
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
}
