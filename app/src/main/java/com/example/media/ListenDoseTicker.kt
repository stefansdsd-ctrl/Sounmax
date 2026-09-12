package com.example.media

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import com.example.data.WeeklyListenReport

/** Elke minuut volume×1 min bijschrijven als muziekstream > 0. */
object ListenDoseTicker {
    private val handler = Handler(Looper.getMainLooper())
    private var started = false

    fun start(context: Context) {
        if (started) return
        started = true
        val app = context.applicationContext
        val tick = object : Runnable {
            override fun run() {
                try {
                    val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
                    val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                    if (cur > 0) {
                        val pct = (100 * cur) / max
                        ListenDose.record(app, pct, 1)
                        WeeklyListenReport.addMinute(app, pct)
                        val wellness = app.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                        wellness.edit()
                            .putInt("dose_today", ListenDose.weekMinutes(app).lastOrNull()?.second?.toInt() ?: 0)
                            .putInt("dose_week", ListenDose.weekTotal(app).toInt())
                            .apply()
                        DailyHearingBudget.applySoftCap(app)
                    }
                } catch (_: Exception) {
                }
                handler.postDelayed(this, 60_000L)
            }
        }
        handler.postDelayed(tick, 60_000L)
    }
}
