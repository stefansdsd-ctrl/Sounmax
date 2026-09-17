package com.example.media

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/** Zet actieve scene na 15/30/60/90 min automatisch uit. */
object SceneAutoOff {
    private const val PREFS = SceneAutomation.PREFS
    private const val KEY_MIN = "scene_auto_off_min"
    private val handler = Handler(Looper.getMainLooper())
    private var pending: Runnable? = null

    fun minutes(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_MIN, 0)

    fun label(context: Context): String = when (val m = minutes(context)) {
        0 -> "Timer"
        else -> "Timer ${m}m"
    }

    fun cycle(context: Context) {
        val next = when (minutes(context)) {
            0 -> 15
            15 -> 30
            30 -> 60
            60 -> 90
            else -> 0
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_MIN, next).apply()
        arm(context)
        Toast.makeText(
            context,
            if (next == 0) "Scene-timer uit" else "Scene stopt over ${next} min",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun arm(context: Context) {
        pending?.let { handler.removeCallbacks(it) }
        pending = null
        val min = minutes(context)
        if (min <= 0) return
        val app = context.applicationContext
        val r = Runnable { OffOneTap.toggle(app) }
        pending = r
        handler.postDelayed(r, min * 60_000L)
    }
}
