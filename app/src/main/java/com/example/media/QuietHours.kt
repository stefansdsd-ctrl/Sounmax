package com.example.media

import android.content.Context
import java.util.Calendar

/** Configureerbare stille uren: standaard 22:00–07:00, veilig volume. */
object QuietHours {
    const val KEY_ENABLED = "quiet_hours"
    const val KEY_SAFE = "safe_volume"
    const val KEY_START = "quiet_start_hour"
    const val KEY_END = "quiet_end_hour"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun startHour(context: Context): Int =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_START, 22)

    fun endHour(context: Context): Int =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_END, 7)

    fun setWindow(context: Context, start: Int, end: Int) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putInt(KEY_START, start).putInt(KEY_END, end).apply()
    }

    fun label(context: Context): String {
        if (!enabled(context)) return "Stille uren uit"
        return "Stil ${startHour(context)}–${endHour(context)}"
    }

    fun isQuietNow(context: Context): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val start = startHour(context)
        val end = endHour(context)
        return if (start >= end) hour >= start || hour < end else hour in start until end
    }

    fun enforce(context: Context) {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ENABLED, true)) return
        if (isQuietNow(context)) {
            prefs.edit().putBoolean(KEY_SAFE, true).apply()
        }
    }

    /** 22–7 → 23–8 → 21–6 → uit → 22–7 */
    fun cycle(context: Context): String {
        val on = enabled(context)
        val start = startHour(context)
        return when {
            !on -> {
                setEnabled(context, true)
                setWindow(context, 22, 7)
                "Stille uren 22–7"
            }
            start == 22 -> {
                setWindow(context, 23, 8)
                "Stille uren 23–8"
            }
            start == 23 -> {
                setWindow(context, 21, 6)
                "Stille uren 21–6"
            }
            else -> {
                setEnabled(context, false)
                "Stille uren uit"
            }
        }
    }
}
