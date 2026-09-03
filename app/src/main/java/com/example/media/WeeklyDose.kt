package com.example.media

import android.content.SharedPreferences

/** Weekdosis luisteren (minuten) → label + persist. */
object WeeklyDose {
    const val KEY_MINUTES = "week_dose_minutes"
    const val KEY_LABEL = "dose_label"

    fun remember(prefs: SharedPreferences, minutes: Int) {
        prefs.edit()
            .putInt(KEY_MINUTES, minutes)
            .putString(KEY_LABEL, label(minutes))
            .apply()
    }

    fun label(minutes: Int): String = when {
        minutes >= 900 -> "Weekdosis hoog (${minutes} min) — extra pauzes"
        minutes >= 600 -> "Weekdosis ${minutes} min — rust na 18:00"
        minutes >= 300 -> "Weekdosis ${minutes} min — oké"
        minutes <= 0 -> "Nog geen dosis deze week"
        else -> "Lichte week (${minutes} min)"
    }
}
