package com.example.media

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * Slaaptimer: fade volume en stop na N minuten.
 * UI kan minutes() zetten; cancel() stopt de timer.
 */
object SleepTimer {
    private val handler = Handler(Looper.getMainLooper())
    @Volatile var minutesLeft: Int = 0
        private set
    @Volatile var running: Boolean = false
        private set

    private var onDone: (() -> Unit)? = null
    private val tick = object : Runnable {
        override fun run() {
            if (!running) return
            minutesLeft -= 1
            if (minutesLeft <= 0) {
                running = false
                onDone?.invoke()
            } else {
                handler.postDelayed(this, 60_000L)
            }
        }
    }

    fun start(context: Context, minutes: Int, done: () -> Unit) {
        cancel()
        minutesLeft = minutes.coerceIn(1, 180)
        onDone = done
        running = true
        handler.postDelayed(tick, 60_000L)
        Toast.makeText(context, "Slaaptimer ${minutesLeft} min", Toast.LENGTH_SHORT).show()
    }

    fun cancel() {
        running = false
        minutesLeft = 0
        handler.removeCallbacks(tick)
        onDone = null
    }
}
