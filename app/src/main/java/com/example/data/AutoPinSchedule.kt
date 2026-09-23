package com.example.data

import android.content.Context
import java.util.Calendar

/** Schakelt pin-set automatisch: Werk overdag, Onderweg avondspits, Thuis nacht. Weekend = Thuis. */
object AutoPinSchedule {
    private const val PREFS = "sounmax_auto_pin"
    private const val KEY_ON = "enabled"
    private const val KEY_WEEKEND_HOME = "weekend_home"
    private const val KEY_WORK_START = "work_start"
    private const val KEY_WORK_END = "work_end"
    private const val KEY_TRAVEL_START = "travel_start"
    private const val KEY_TRAVEL_END = "travel_end"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun enabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ON, false)

    fun weekendHome(context: Context): Boolean = prefs(context).getBoolean(KEY_WEEKEND_HOME, true)

    fun workStart(context: Context): Int = prefs(context).getInt(KEY_WORK_START, 7)
    fun workEnd(context: Context): Int = prefs(context).getInt(KEY_WORK_END, 16)
    fun travelStart(context: Context): Int = prefs(context).getInt(KEY_TRAVEL_START, 17)
    fun travelEnd(context: Context): Int = prefs(context).getInt(KEY_TRAVEL_END, 19)

    fun setHours(
        context: Context,
        workStart: Int,
        workEnd: Int,
        travelStart: Int,
        travelEnd: Int
    ) {
        prefs(context).edit()
            .putInt(KEY_WORK_START, workStart.coerceIn(0, 23))
            .putInt(KEY_WORK_END, workEnd.coerceIn(0, 23))
            .putInt(KEY_TRAVEL_START, travelStart.coerceIn(0, 23))
            .putInt(KEY_TRAVEL_END, travelEnd.coerceIn(0, 23))
            .apply()
        if (enabled(context)) applyNow(context)
    }

    fun hoursLabel(context: Context): String =
        "Werk ${workStart(context)}–${workEnd(context)} · Weg ${travelStart(context)}–${travelEnd(context)}"

    /** 7–16/17–19 → 8–17/18–20 → 6–15/16–18 → default */
    fun cycleHours(context: Context): String {
        val ws = workStart(context)
        val next = when (ws) {
            7 -> intArrayOf(8, 17, 18, 20)
            8 -> intArrayOf(6, 15, 16, 18)
            else -> intArrayOf(7, 16, 17, 19)
        }
        setHours(context, next[0], next[1], next[2], next[3])
        return hoursLabel(context)
    }

    fun setWeekendHome(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_WEEKEND_HOME, on).apply()
        if (enabled(context)) applyNow(context)
    }

    fun toggleWeekendHome(context: Context): Boolean {
        val next = !weekendHome(context)
        setWeekendHome(context, next)
        return next
    }

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ON, on).apply()
        if (on) applyNow(context)
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        return next
    }

    fun isWeekend(): Boolean {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    private fun inRange(hour: Int, start: Int, end: Int): Boolean {
        if (start == end) return false
        return if (start < end) hour in start until end else hour >= start || hour < end
    }

    fun suggestedProfileId(context: Context? = null): String {
        if (context != null && weekendHome(context) && isWeekend()) return "home"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (context == null) {
            return when (hour) {
                in 7..16 -> "work"
                in 17..19 -> "travel"
                else -> "home"
            }
        }
        return when {
            inRange(hour, workStart(context), workEnd(context)) -> "work"
            inRange(hour, travelStart(context), travelEnd(context)) -> "travel"
            else -> "home"
        }
    }

    fun applyNow(context: Context): PinProfiles.Profile {
        val id = suggestedProfileId(context)
        val profile = PinProfiles.ALL.first { it.id == id }
        PinProfiles.apply(context, profile)
        return profile
    }

    fun label(context: Context): String {
        val name = PinProfiles.ALL.first { it.id == suggestedProfileId(context) }.name
        val prefix = if (isWeekend() && weekendHome(context)) "Weekend" else "Auto"
        return if (enabled(context)) "$prefix: $name" else "Auto-set"
    }
}
