package com.example.media

import android.content.SharedPreferences
import java.util.Calendar

/**
 * WHO-achtige luisterdosis: 80 dB(A) × 40 uur/week als referentie.
 * We slaan minuten + geschat dB op en geven pauze-advies.
 */
object WeeklyDose {
    const val KEY_MINUTES = "week_dose_minutes"
    const val KEY_LABEL = "dose_label"
    const val KEY_WEEK = "week_dose_week_of_year"
    const val KEY_DAY_MINUTES = "day_dose_minutes"
    const val KEY_DAY = "day_dose_day_of_year"
    const val KEY_DB = "dose_est_db"

    private const val REF_DB = 80.0
    private const val REF_WEEK_MIN = 40 * 60

    fun remember(prefs: SharedPreferences, minutes: Int, estimatedDb: Int = 80) {
        val cal = Calendar.getInstance()
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        val day = cal.get(Calendar.DAY_OF_YEAR)
        val storedWeek = prefs.getInt(KEY_WEEK, week)
        val storedDay = prefs.getInt(KEY_DAY, day)
        val weekMin = if (storedWeek == week) prefs.getInt(KEY_MINUTES, 0) + minutes else minutes
        val dayMin = if (storedDay == day) prefs.getInt(KEY_DAY_MINUTES, 0) + minutes else minutes
        prefs.edit()
            .putInt(KEY_MINUTES, weekMin)
            .putInt(KEY_DAY_MINUTES, dayMin)
            .putInt(KEY_WEEK, week)
            .putInt(KEY_DAY, day)
            .putInt(KEY_DB, estimatedDb)
            .putString(KEY_LABEL, label(weekMin, dayMin, estimatedDb))
            .apply()
    }

    fun exposureRatio(weekMinutes: Int, db: Int): Double {
        val extra = (db - REF_DB) / 3.0
        val allowed = REF_WEEK_MIN / Math.pow(2.0, extra.coerceIn(-4.0, 6.0))
        return if (allowed <= 0) 1.0 else weekMinutes / allowed
    }

    fun label(minutes: Int, dayMinutes: Int = 0, db: Int = 80): String {
        val ratio = exposureRatio(minutes, db)
        val pct = (ratio * 100).toInt().coerceAtLeast(0)
        return when {
            ratio >= 1.0 -> "WHO-limiet ${pct}% — pauze nu (${minutes} min / ${db} dB)"
            ratio >= 0.75 -> "Weekdosis hoog (${pct}%) — extra pauzes"
            dayMinutes >= 180 -> "Vandaag ${dayMinutes} min — oor-pauze na 50 min"
            minutes >= 300 -> "Weekdosis ${minutes} min — oké (${pct}%)"
            minutes <= 0 -> "Nog geen dosis deze week"
            else -> "Lichte week (${minutes} min, ${pct}%)"
        }
    }
}
