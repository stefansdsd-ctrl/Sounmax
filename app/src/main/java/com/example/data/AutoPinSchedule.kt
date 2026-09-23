package com.example.data

import android.content.Context
import java.util.Calendar

/** Schakelt pin-set automatisch: Werk overdag, Onderweg avondspits, Thuis nacht. */
object AutoPinSchedule {
    private const val PREFS = "sounmax_auto_pin"
    private const val KEY_ON = "enabled"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

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

    fun suggestedProfileId(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 7..16 -> "work"
            in 17..19 -> "travel"
            else -> "home"
        }
    }

    fun applyNow(context: Context): PinProfiles.Profile {
        val id = suggestedProfileId()
        val profile = PinProfiles.ALL.first { it.id == id }
        PinProfiles.apply(context, profile)
        return profile
    }

    fun label(context: Context): String {
        val name = PinProfiles.ALL.first { it.id == suggestedProfileId() }.name
        return if (enabled(context)) "Auto: $name" else "Auto-set"
    }
}
