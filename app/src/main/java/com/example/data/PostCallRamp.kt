package com.example.data

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

/**
 * Na ophangen: muziek start op max 40% en klimt in 8 stappen terug naar het oude niveau.
 * Voorkomt een harde knal na een gesprek.
 */
object PostCallRamp {
    private const val PREFS = "sounmax_post_call_ramp"
    private val handler = Handler(Looper.getMainLooper())
    private var stepsLeft = 0
    private var target = 0

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) cancel()
        return label(context)
    }

    fun onEnter(context: Context) {
        if (!enabled(context)) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        prefs(context).edit().putInt("saved", am.getStreamVolume(AudioManager.STREAM_MUSIC)).apply()
    }

    fun onLeave(context: Context) {
        if (!enabled(context)) return
        val app = context.applicationContext
        val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val saved = prefs(app).getInt("saved", am.getStreamVolume(AudioManager.STREAM_MUSIC))
        val floor = (max * 40) / 100
        target = saved.coerceIn(0, max)
        val start = minOf(target, floor)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, start, 0)
        stepsLeft = 8
        prefs(app).edit().putBoolean("ramping", true).putLong("last", System.currentTimeMillis()).apply()
        handler.removeCallbacksAndMessages(null)
        tick(app)
    }

    fun active(context: Context) = prefs(context).getBoolean("ramping", false)

    fun label(context: Context) = when {
        !enabled(context) -> "Na-bel uit"
        active(context) -> "Na-bel (opbouw)"
        else -> "Na-bel aan"
    }

    private fun tick(context: Context) {
        handler.postDelayed({
            if (!enabled(context) || stepsLeft <= 0) {
                finish(context)
                return@postDelayed
            }
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (cur >= target) {
                finish(context)
                return@postDelayed
            }
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur + 1).coerceAtMost(target), 0)
            stepsLeft -= 1
            if (stepsLeft <= 0 || cur + 1 >= target) finish(context) else tick(context)
        }, 1000L)
    }

    private fun finish(context: Context) {
        stepsLeft = 0
        prefs(context).edit().putBoolean("ramping", false).apply()
    }

    private fun cancel() {
        stepsLeft = 0
        handler.removeCallbacksAndMessages(null)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
