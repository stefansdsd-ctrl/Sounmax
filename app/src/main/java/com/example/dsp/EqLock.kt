package com.example.dsp

import android.content.Context

/** Blokkeert band-sleep zodat een goeie curve niet per ongeluk verschuift. */
object EqLock {
    private const val PREFS = "soundmax_eq_lock"
    private const val KEY = "locked"

    fun isLocked(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

    fun setLocked(context: Context, locked: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, locked).apply()
    }

    fun toggle(context: Context): Boolean {
        val next = !isLocked(context)
        setLocked(context, next)
        return next
    }
}
