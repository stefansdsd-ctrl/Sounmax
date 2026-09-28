package com.example.data

import android.content.Context
import java.util.Calendar

/** Focus-uren: weekdagen automatisch focus-scene. Default 20–22. Wijkt voor pendel/werk/gym. */
object FocusHours {
    private const val PREFS = "sounmax_focus_hours"
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

    fun startHour(context: Context) = prefs(context).getInt(KEY_START, 20).coerceIn(0, 23)
    fun endHour(context: Context) = prefs(context).getInt(KEY_END, 22).coerceIn(0, 23)

    fun setWindow(context: Context, start: Int, end: Int) {
        prefs(context).edit()
            .putInt(KEY_START, start.coerceIn(0, 23))
            .putInt(KEY_END, end.coerceIn(0, 23))
            .apply()
    }

    fun weekdayNow(): Boolean {
        val d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return d in Calendar.MONDAY..Calendar.FRIDAY
    }

    fun activeNow(context: Context, hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Boolean {
        if (!enabled(context) || !weekdayNow()) return false
        if (CommuteHours.activeNow(context, hour)) return false
        if (WorkHours.activeNow(context, hour)) return false
        if (GymHours.activeNow(context, hour)) return false
        val start = startHour(context)
        val end = endHour(context)
        return if (start < end) hour in start until end else hour >= start || hour < end
    }

    fun apply(context: Context): Boolean {
        if (!activeNow(context)) return false
        val now = System.currentTimeMillis()
        if (now - prefs(context).getLong(KEY_LAST, 0L) < 10 * 60_000L) return false
        if (!com.example.media.FocusOneTap.isOn(context)) {
            com.example.media.FocusOneTap.toggle(context)
        }
        prefs(context).edit().putLong(KEY_LAST, now).apply()
        return true
    }

    fun label(context: Context): String {
        val w = "${startHour(context)}u–${endHour(context)}u"
        return when {
            !enabled(context) -> "Focus-uren uit"
            activeNow(context) -> "Focus nu ($w)"
            else -> "Focus $w"
        }
    }

    fun cycleWindow(context: Context): String {
        val on = enabled(context)
        val start = startHour(context)
        return when {
            !on -> {
                setEnabled(context, true)
                setWindow(context, 20, 22)
                apply(context)
                label(context)
            }
            start == 20 -> {
                setWindow(context, 19, 21)
                apply(context)
                label(context)
            }
            start == 19 -> {
                setWindow(context, 21, 23)
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
