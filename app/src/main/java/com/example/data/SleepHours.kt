package com.example.data

import android.content.Context
import java.util.Calendar

/** Slaap-uren: elke dag automatisch slaap-scene. Default 22–7. Wijkt voor vlucht. */
object SleepHours {
    private const val PREFS = "sounmax_sleep_hours"
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

    fun startHour(context: Context) = prefs(context).getInt(KEY_START, 22).coerceIn(0, 23)
    fun endHour(context: Context) = prefs(context).getInt(KEY_END, 7).coerceIn(0, 23)

    fun setWindow(context: Context, start: Int, end: Int) {
        prefs(context).edit()
            .putInt(KEY_START, start.coerceIn(0, 23))
            .putInt(KEY_END, end.coerceIn(0, 23))
            .apply()
    }

    fun activeNow(context: Context, hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Boolean {
        if (!enabled(context)) return false
        if (FlightHold.active(context)) return false
        val start = startHour(context)
        val end = endHour(context)
        return if (start < end) hour in start until end else hour >= start || hour < end
    }

    fun apply(context: Context): Boolean {
        if (!activeNow(context)) return false
        val now = System.currentTimeMillis()
        if (now - prefs(context).getLong(KEY_LAST, 0L) < 10 * 60_000L) return false
        if (!com.example.media.SleepOneTap.isOn(context)) {
            com.example.media.SleepOneTap.toggle(context)
        }
        prefs(context).edit().putLong(KEY_LAST, now).apply()
        return true
    }

    fun label(context: Context): String {
        val w = "${startHour(context)}u–${endHour(context)}u"
        return when {
            !enabled(context) -> "Slaap-uren uit"
            activeNow(context) -> "Slaap nu ($w)"
            else -> "Slaap $w"
        }
    }

    fun cycleWindow(context: Context): String {
        val on = enabled(context)
        val start = startHour(context)
        return when {
            !on -> {
                setEnabled(context, true)
                setWindow(context, 22, 7)
                apply(context)
                label(context)
            }
            start == 22 -> {
                setWindow(context, 23, 7)
                apply(context)
                label(context)
            }
            start == 23 -> {
                setWindow(context, 21, 6)
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
