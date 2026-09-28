package com.example.data

import android.content.Context
import com.example.media.DailyHearingBudget
import java.util.Calendar

/**
 * Als de dagdosis vol is: één keer 2 u schema-pauze.
 * Voorkomt dat winkel/studie/museum-uren alsnog scenes pushen.
 */
object DoseAutoPause {
    private const val PREFS = "sounmax_dose_auto_pauze"
    private const val KEY_DAY = "tripped_day"

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean("on", true)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean("on", on).apply()
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        return next
    }

    fun trippedToday(context: Context): Boolean =
        prefs(context).getInt(KEY_DAY, -1) == dayKey()

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        if (trippedToday(context)) return SchedulePause.active(context)
        val over = HearingGuard.overDailyLimit(context) || DailyHearingBudget.overCap(context)
        if (!over) return false
        if (!SchedulePause.active(context)) SchedulePause.start(context, 120)
        prefs(context).edit().putInt(KEY_DAY, dayKey()).apply()
        return true
    }

    fun label(context: Context): String = when {
        !enabled(context) -> "Dosis-pauze uit"
        trippedToday(context) && SchedulePause.active(context) ->
            "Dosis-pauze aan (${SchedulePause.remainingMin(context)}m)"
        trippedToday(context) -> "Dosis-pauze gezet"
        else -> "Dosis-pauze klaar"
    }

    private fun dayKey(): Int {
        val c = Calendar.getInstance()
        return c.get(Calendar.YEAR) * 1000 + c.get(Calendar.DAY_OF_YEAR)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
