package com.example.data

import android.content.Context

/**
 * Oorpauze: na 45 min luisteren 5 min schema-pauze.
 * Geen extra hold; safety-holds blijven.
 */
object EarRest {
    private const val PREFS = "sounmax_ear_rest"
    private const val LIMIT_MS = 45L * 60_000
    private const val PAUSE_MIN = 5

    fun enabled(context: Context) = prefs(context).getBoolean("on", false)

    fun setEnabled(context: Context, on: Boolean) {
        val e = prefs(context).edit().putBoolean("on", on)
        if (on) e.putLong("started", System.currentTimeMillis())
        else e.remove("started")
        e.apply()
    }

    fun cycle(context: Context): String {
        if (!enabled(context)) {
            setEnabled(context, true)
            return label(context)
        }
        apply(context)
        if (SchedulePause.active(context)) return label(context)
        setEnabled(context, false)
        return "Oorpauze uit"
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        val started = prefs(context).getLong("started", 0L)
        if (started == 0L) {
            prefs(context).edit().putLong("started", System.currentTimeMillis()).apply()
            return false
        }
        if (System.currentTimeMillis() - started < LIMIT_MS) return false
        if (!SchedulePause.active(context)) SchedulePause.start(context, PAUSE_MIN)
        prefs(context).edit().putLong("started", System.currentTimeMillis()).apply()
        return true
    }

    fun remainingMin(context: Context): Int {
        if (!enabled(context)) return 0
        val started = prefs(context).getLong("started", 0L)
        val left = LIMIT_MS - (System.currentTimeMillis() - started)
        return (left / 60_000).toInt().coerceAtLeast(0)
    }

    fun label(context: Context): String = when {
        !enabled(context) -> "Oorpauze uit"
        SchedulePause.active(context) ->
            "Oorpauze (${SchedulePause.remainingMin(context)}m stil)"
        else -> "Oorpauze (${remainingMin(context)}m tot pauze)"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
