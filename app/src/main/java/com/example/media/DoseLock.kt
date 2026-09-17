package com.example.media

import android.content.Context

/**
 * Harde dosis-lock: bij overschrijden van DailyHearingBudget pauzeren
 * in plaats van alleen volume-cap.
 */
object DoseLock {
    const val KEY = "dose_lock_enabled"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY, false)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY, on).apply()
    }

    fun enforce(context: Context): Boolean {
        if (!enabled(context)) return false
        if (!DailyHearingBudget.overCap(context)) return false
        if (!MediaRemote.isMusicActive(context)) return false
        MediaRemote.pause(context)
        DailyHearingBudget.applySoftCap(context)
        return true
    }

    fun label(context: Context): String {
        val on = enabled(context)
        val over = DailyHearingBudget.overCap(context)
        return when {
            on && over -> "Dosis-lock · pauze tot middernacht"
            on -> "Dosis-lock aan"
            else -> "Dosis-lock uit"
        }
    }
}
