package com.example.data

import android.content.Context
import java.util.Calendar

/**
 * Simpele weekdosis: minuten per dag + advies.
 * Verwacht dat ListenDose elke minuut `addMinute()` aanroept.
 */
object WeeklyListenReport {
    private const val PREFS = "listen_dose"

    fun addMinute(context: Context, volumePercent: Int) {
        if (volumePercent <= 0) return
        val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val key = "m_${year}_$day"
        p.edit().putInt(key, p.getInt(key, 0) + 1).apply()
    }

    data class Day(val label: String, val minutes: Int)

    fun last7Days(context: Context): List<Day> {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val cal = Calendar.getInstance()
        val names = arrayOf("zo", "ma", "di", "wo", "do", "vr", "za")
        return (6 downTo 0).map { offset ->
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -offset)
            val key = "m_${c.get(Calendar.YEAR)}_${c.get(Calendar.DAY_OF_YEAR)}"
            Day(names[c.get(Calendar.DAY_OF_WEEK) - 1], p.getInt(key, 0))
        }
    }

    fun hint(minutesToday: Int): String = when {
        minutesToday >= 180 -> "Pauze: je zit boven 3 uur"
        minutesToday >= 120 -> "2 uur beluisterd — volume checken"
        minutesToday >= 60 -> "1 uur — oké als volume ≤ 60%"
        else -> "Weekdosis rustig"
    }
}
