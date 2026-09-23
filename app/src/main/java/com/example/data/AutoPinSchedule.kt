package com.example.data

import android.content.Context
import java.util.Calendar

/** Schakelt pin-set automatisch: Werk overdag, Onderweg avondspits, Thuis nacht. Weekend = Thuis. */
object AutoPinSchedule {
    private const val PREFS = "sounmax_auto_pin"
    private const val KEY_ON = "enabled"
    private const val KEY_WEEKEND_HOME = "weekend_home"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun weekendHome(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_WEEKEND_HOME, true)

    fun setWeekendHome(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_WEEKEND_HOME, on).apply()
        if (enabled(context)) applyNow(context)
    }

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ON, on).apply()
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

    fun suggestedProfileId(context: Context? = null): String {
        if (context != null && weekendHome(context) && isWeekend()) return "home"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 7..16 -> "work"
            in 17..19 -> "travel"
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
