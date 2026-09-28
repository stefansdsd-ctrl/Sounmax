package com.example.data

import android.content.Context

/** Pauzeert alle auto-uren 1/2/4 uur zodat overlapping scenes niet vechten. */
object SchedulePause {
    private const val PREFS = "sounmax_schedule_pause"
    private const val KEY_UNTIL = "until_ms"

    fun active(context: Context): Boolean =
        prefs(context).getLong(KEY_UNTIL, 0L) > System.currentTimeMillis()

    fun remainingMin(context: Context): Int {
        val left = prefs(context).getLong(KEY_UNTIL, 0L) - System.currentTimeMillis()
        return if (left <= 0) 0 else ((left + 59_999L) / 60_000L).toInt()
    }

    fun start(context: Context, minutes: Int) {
        prefs(context).edit()
            .putLong(KEY_UNTIL, System.currentTimeMillis() + minutes.coerceIn(30, 480) * 60_000L)
            .apply()
    }

    fun stop(context: Context) {
        prefs(context).edit().putLong(KEY_UNTIL, 0L).apply()
    }

    fun cycle(context: Context): String {
        val left = remainingMin(context)
        return when {
            !active(context) -> {
                start(context, 60)
                label(context)
            }
            left <= 60 -> {
                start(context, 120)
                label(context)
            }
            left <= 120 -> {
                start(context, 240)
                label(context)
            }
            else -> {
                stop(context)
                label(context)
            }
        }
    }

    fun label(context: Context): String =
        if (active(context)) "Schema-pauze ${remainingMin(context)}m"
        else "Schema-pauze uit"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
