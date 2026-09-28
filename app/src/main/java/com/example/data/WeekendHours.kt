package com.example.data

import android.content.Context
import java.util.Calendar

/** Weekend-uren: za/zo automatisch tuin/weekend-scene. Default 10–18. */
object WeekendHours {
    private const val PREFS = "sounmax_weekend_hours"
    private const val KEY_ON = "enabled"
    private const val KEY_START = "start_hour"
    private const val KEY_END = "end_hour"
    private const val KEY_LAST = "last_apply_ms"

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ON, false)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ON, on).apply()
        if (on) apply(context)
    }

    fun startHour(context: Context) = prefs(context).getInt(KEY_START, 10).coerceIn(0, 23)
    fun endHour(context: Context) = prefs(context).getInt(KEY_END, 18).coerceIn(0, 23)

    fun setWindow(context: Context, start: Int, end: Int) {
        prefs(context).edit()
            .putInt(KEY_START, start.coerceIn(0, 23))
            .putInt(KEY_END, end.coerceIn(0, 23))
            .apply()
    }

    fun weekendNow(): Boolean {
        val d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return d == Calendar.SATURDAY || d == Calendar.SUNDAY
    }

    fun activeNow(context: Context, hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Boolean {
        if (!enabled(context) || !weekendNow()) return false
        val start = startHour(context)
        val end = endHour(context)
        return hour in start until end
    }

    fun apply(context: Context): Boolean {
        if (!activeNow(context)) return false
        val now = System.currentTimeMillis()
        if (now - prefs(context).getLong(KEY_LAST, 0L) < 10 * 60_000L) return false
        if (!com.example.media.GardenOneTap.isOn(context)) {
            com.example.media.GardenOneTap.toggle(context)
        }
        prefs(context).edit().putLong(KEY_LAST, now).apply()
        return true
    }

    fun label(context: Context): String {
        val w = "${startHour(context)}u–${endHour(context)}u"
        return when {
            !enabled(context) -> "Weekend-uren uit"
            activeNow(context) -> "Weekend nu ($w)"
            else -> "Weekend $w"
        }
    }

    fun cycleWindow(context: Context): String {
        val on = enabled(context)
        val start = startHour(context)
        return when {
            !on -> {
                setEnabled(context, true)
                setWindow(context, 10, 18)
                apply(context)
                label(context)
            }
            start == 10 -> {
                setWindow(context, 9, 17)
                apply(context)
                label(context)
            }
            start == 9 -> {
                setWindow(context, 11, 20)
                apply(context)
                label(context)
            }
            else -> {
                setEnabled(context, false)
                label(context)
            }
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
