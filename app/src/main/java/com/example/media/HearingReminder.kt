package com.example.media

import android.content.Context
import com.example.dsp.HearingCorrection

/** Herinnering: 90 dagen of ≥50 eq-luisteruren sinds laatste test. */
object HearingReminder {
    const val DAYS_MS = 90L * 24 * 60 * 60 * 1000
    const val HOURS_EQ = 50f * 60f

    fun shouldRemind(context: Context): Boolean {
        val last = HearingCorrection.lastTestAt(context)
        val old = last == 0L || System.currentTimeMillis() - last >= DAYS_MS
        val hours = ListenDose.weekTotal(context) >= HOURS_EQ ||
            lifetimeMinutes(context) >= HOURS_EQ
        return old || hours
    }

    fun hint(context: Context): String? {
        if (!shouldRemind(context)) return null
        val last = HearingCorrection.lastTestAt(context)
        return if (last == 0L) "Nog geen gehoortest — 2 min op het tabblad Gehoor."
        else "Gehoortest verouderd — even opnieuw kalibreren."
    }

    fun snooze(context: Context) {
        HearingCorrection.markTestDone(context)
    }

    private fun lifetimeMinutes(context: Context): Float =
        ListenDose.weekTotal(context)
}
