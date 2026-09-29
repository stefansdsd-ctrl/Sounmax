package com.example.data

import android.content.Context
import java.util.Calendar

/** School-uren: weekdagen auto college-scene. Default 8–15. Wijkt voor pendel/vlucht. */
object SchoolHours {
    private const val PREFS = "sounmax_school_hours"
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

    fun startHour(context: Context) = prefs(context).getInt(KEY_START, 8).coerceIn(0, 23)
    fun endHour(context: Context) = prefs(context).getInt(KEY_END, 15).coerceIn(0, 23)

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
        if (FlightHold.active(context)) return false
        if (CommuteHours.activeNow(context, hour)) return false
        if (SchedulePause.active(context)) return false
        val start = startHour(context)
        val end = endHour(context)
        return hour in start until end
    }

    fun apply(context: Context): Boolean {
        if (!activeNow(context)) return false
        val now = System.currentTimeMillis()
        if (now - prefs(context).getLong(KEY_LAST, 0L) < 10 * 60_000L) return false
        if (!com.example.media.CollegeOneTap.isOn(context)) {
            com.example.media.CollegeOneTap.toggle(context)
        }
        prefs(context).edit().putLong(KEY_LAST, now).apply()
        return true
    }

    fun label(context: Context): String {
        val w = "${startHour(context)}u–${endHour(context)}u"
        return when {
            !enabled(context) -> "School-uren uit"
            activeNow(context) -> "School nu ($w)"
            else -> "School $w"
        }
    }

    fun cycleWindow(context: Context): String {
        val on = enabled(context)
        val start = startHour(context)
        val end = endHour(context)
        return when {
            !on -> {
                setEnabled(context, true)
                setWindow(context, 8, 15)
                apply(context)
                label(context)
            }
            start == 8 && end == 15 -> {
                setWindow(context, 8, 16)
                apply(context)
                label(context)
            }
            start == 8 && end == 16 -> {
                setWindow(context, 9, 15)
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
