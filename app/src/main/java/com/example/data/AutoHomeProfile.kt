package com.example.data

import android.content.Context
import java.util.Calendar

/** Tijd-gebaseerd Sport / Werk / Slaap-profiel. */
object AutoHomeProfile {
    private const val PREFS = "sounmax_auto_profile"
    private const val KEY = "enabled"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, on).apply()
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        if (next) applySuggested(context)
        return next
    }

    fun suggestedId(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): String = when (hour) {
        in 6..8, in 17..21 -> "sport"
        in 22..23, in 0..5 -> "slaap"
        else -> "werk"
    }

    fun applySuggested(context: Context): String {
        val id = suggestedId()
        HomeToolProfiles.set(context, id)
        return id
    }

    fun tick(context: Context): String? {
        if (!enabled(context)) return null
        val id = suggestedId()
        if (HomeToolProfiles.activeId(context) != id) HomeToolProfiles.set(context, id)
        return id
    }

    fun label(context: Context): String {
        val name = HomeToolProfiles.ALL.firstOrNull { it.id == suggestedId() }?.name ?: "Werk"
        return if (enabled(context)) "Auto-profiel: $name" else "Auto-profiel uit"
    }
}
