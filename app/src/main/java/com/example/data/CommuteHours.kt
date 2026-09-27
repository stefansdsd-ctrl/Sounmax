package com.example.data

import android.content.Context
import java.util.Calendar

/** Pendel-uren: weekdagen automatisch commute-scene. Default 7-9 en 16-18. */
object CommuteHours {
    private const val PREFS = "sounmax_commute_hours"
    private const val KEY_ON = "enabled"
    private const val KEY_AM_START = "am_start"
    private const val KEY_AM_END = "am_end"
    private const val KEY_PM_START = "pm_start"
    private const val KEY_PM_END = "pm_end"
    private const val KEY_LAST = "last_apply_ms"

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ON, false)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ON, on).apply()
        if (on) apply(context)
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        return next
    }

    fun amStart(context: Context) = prefs(context).getInt(KEY_AM_START, 7).coerceIn(0, 23)
    fun amEnd(context: Context) = prefs(context).getInt(KEY_AM_END, 9).coerceIn(0, 23)
    fun pmStart(context: Context) = prefs(context).getInt(KEY_PM_START, 16).coerceIn(0, 23)
    fun pmEnd(context: Context) = prefs(context).getInt(KEY_PM_END, 18).coerceIn(0, 23)

    fun setWindows(context: Context, amS: Int, amE: Int, pmS: Int, pmE: Int) {
        prefs(context).edit()
            .putInt(KEY_AM_START, amS.coerceIn(0, 23))
            .putInt(KEY_AM_END, amE.coerceIn(0, 23))
            .putInt(KEY_PM_START, pmS.coerceIn(0, 23))
            .putInt(KEY_PM_END, pmE.coerceIn(0, 23))
            .apply()
    }

    fun weekdayNow(): Boolean {
        val d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return d in Calendar.MONDAY..Calendar.FRIDAY
    }

    fun activeNow(context: Context, hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Boolean {
        if (!enabled(context) || !weekdayNow()) return false
        val inAm = hour in amStart(context) until amEnd(context)
        val inPm = hour in pmStart(context) until pmEnd(context)
        return inAm || inPm
    }

    fun apply(context: Context): Boolean {
        if (!activeNow(context)) return false
        val now = System.currentTimeMillis()
        if (now - prefs(context).getLong(KEY_LAST, 0L) < 10 * 60_000L) return false
        com.example.media.CommuteOneTap.toggle(context)
        if (!com.example.media.CommuteOneTap.isOn(context)) {
            com.example.media.CommuteOneTap.toggle(context)
        }
        prefs(context).edit().putLong(KEY_LAST, now).apply()
        return true
    }

    fun label(context: Context): String {
        val w = "${amStart(context)}–${amEnd(context)} / ${pmStart(context)}–${pmEnd(context)}"
        return when {
            !enabled(context) -> "Pendel-uren uit"
            activeNow(context) -> "Pendel nu ($w)"
            else -> "Pendel $w"
        }
    }

    fun cycleWindow(context: Context): String {
        val on = enabled(context)
        val am = amStart(context)
        return when {
            !on -> {
                setEnabled(context, true)
                setWindows(context, 7, 9, 16, 18)
                apply(context)
                label(context)
            }
            am == 7 -> {
                setWindows(context, 6, 9, 15, 19)
                apply(context)
                label(context)
            }
            am == 6 -> {
                setWindows(context, 8, 10, 17, 19)
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
