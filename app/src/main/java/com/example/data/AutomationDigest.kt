package com.example.data

import android.content.Context
import com.example.media.DailyHearingBudget
import com.example.media.DoseLock

/** Eén regel met alle actieve auto-regels — stopt het giswerk in Meer. */
object AutomationDigest {
    fun lines(context: Context): List<String> {
        val out = mutableListOf<String>()
        if (SchedulePause.active(context)) out += SchedulePause.label(context)
        if (DoseAutoPause.trippedToday(context)) out += DoseAutoPause.label(context)
        if (RainHold.active(context)) out += RainHold.label(context)
        if (GameHold.active(context)) out += GameHold.label(context)
        if (ConcertHold.active(context)) out += ConcertHold.label(context)
        if (TrainHold.active(context)) out += TrainHold.label(context)
        if (DriveHold.active(context)) out += DriveHold.label(context)
        if (BikeHold.active(context)) out += BikeHold.label(context)
        if (WalkHold.active(context)) out += WalkHold.label(context)
        if (NapHold.active(context)) out += NapHold.label(context)
        if (FlightHold.active(context)) out += FlightHold.label(context)
        if (MeetingHold.active(context)) out += MeetingHold.label(context)
        if (DoseLock.enabled(context)) out += DoseLock.label(context)
        if (DailyHearingBudget.overCap(context)) out += DailyHearingBudget.chipLabel(context)
        if (HearingGuard.overDailyLimit(context)) out += HearingGuard.status(context)
        return out
    }

    fun summary(context: Context): String {
        val l = lines(context)
        return when {
            l.isEmpty() -> "Geen auto-regel actief"
            l.size == 1 -> l.first()
            else -> "${l.size} actief · ${l.first()}"
        }
    }
}
