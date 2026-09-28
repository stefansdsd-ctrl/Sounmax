package com.example.data

import android.content.Context
import java.util.Calendar

/** Museum-uren: weekdagen auto museum-scene. Default 11–15. Wijkt voor pendel/werk/gym/vlucht. */
object MuseumHours {
    private const val PREFS = "sounmax_museum_hours"
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

    fun startHour(context: Context) = prefs(context).getInt(KEY_START, 11).coerceIn(0, 23)
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
        if (WorkHours.activeNow(context, hour)) return false
        if (GymHours.activeNow(context, hour)) return false
        val start = startHour(context)
        val end = endHour(context)
        return hour in start until end
    }

    fun apply(context: Context): Boolean {
        if (!activeNow(context)) return false
        val now = System.currentTimeMillis()
        if (now - prefs(context).getLong(KEY_LAST, 0L) < 10 * 60_000L) return false
        if (!com.example.media.MuseumOneTap.isOn(context)) {
            com.example.media.MuseumOneTap.toggle(context)
        }
        prefs(context).edit().putLong(KEY_LAST, now).apply()
        return true
    }

    fun label(context: Context): String {
        val w = "${startHour(context)}u–${endHour(context)}u"
        return when {
            !enabled(context) -> "Museum-uren uit"
            activeNow(context) -> "Museum nu ($w)"
            else -> "Museum $w"
        }
    }

    fun cycleWindow(context: Context): String {
        val on = enabled(context)
        val start = startHour(context)
        return when {
            !on -> {
                setEnabled(context, true)
                setWindow(context, 11, 15)
                apply(context)
                label(context)
            }
            start == 11 -> {
                setWindow(context, 10, 14)
                apply(context)
                label(context)
            }
            start == 10 -> {
                setWindow(context, 13, 17)
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
