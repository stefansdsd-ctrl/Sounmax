package com.example.media

import android.content.Context
import java.time.LocalDate

/** Dagen op rij onder de dagelijkse gehoorcap. */
object ListenStreak {
    private const val PREFS = "soundmax_wellness"
    private const val KEY_STREAK = "listen_streak_days"
    private const val KEY_BEST = "listen_streak_best"
    private const val KEY_LAST = "listen_streak_last_day"

    fun tick(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val last = p.getString(KEY_LAST, null)
        if (last == today) return
        val over = DailyHearingBudget.overCap(context)
        val yesterday = LocalDate.now().minusDays(1).toString()
        val next = when {
            over -> 0
            last == yesterday -> p.getInt(KEY_STREAK, 0) + 1
            last == null -> 1
            else -> 1
        }
        val best = maxOf(p.getInt(KEY_BEST, 0), next)
        p.edit()
            .putInt(KEY_STREAK, next)
            .putInt(KEY_BEST, best)
            .putString(KEY_LAST, today)
            .apply()
    }

    fun days(context: Context): Int {
        tick(context)
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_STREAK, 0)
    }

    fun best(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_BEST, 0)

    fun label(context: Context): String {
        val d = days(context)
        val b = best(context)
        return if (d <= 0) "Streak 0 · record $b d"
        else "Streak $d d onder dosiscap · record $b"
    }
}
