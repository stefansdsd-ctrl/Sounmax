package com.example.data

import android.content.Context

/** Eén tik: alle tijdelijke holds uit. */
object HoldPanic {
    fun stopAll(context: Context): String {
        val stopped = mutableListOf<String>()
        fun tryStop(name: String, active: Boolean, stop: () -> Unit) {
            if (active) {
                stop()
                stopped += name
            }
        }
        tryStop("ziekenhuis", HospitalHold.active(context)) { HospitalHold.stop(context) }
        tryStop("warmte", ThermalHold.active(context)) { ThermalHold.stop(context) }
        tryStop("lage accu", LowBatteryHold.active(context)) { LowBatteryHold.stop(context) }
        tryStop("bedtime", BedtimeFade.active(context)) { BedtimeFade.stop(context) }
        tryStop("bel", CallHold.active(context)) { CallHold.stop(context) }
        tryStop("game", GameHold.active(context)) { GameHold.stop(context) }
        tryStop("cinema", CinemaHold.active(context)) { CinemaHold.stop(context) }
        tryStop("dutje", NapHold.active(context)) { NapHold.stop(context) }
        tryStop("concert", ConcertHold.active(context)) { ConcertHold.stop(context) }
        tryStop("vlucht", FlightHold.active(context)) { FlightHold.stop(context) }
        tryStop("meeting", MeetingHold.active(context)) { MeetingHold.stop(context) }
        tryStop("fiets", BikeHold.active(context)) { BikeHold.stop(context) }
        tryStop("rit", DriveHold.active(context)) { DriveHold.stop(context) }
        tryStop("regen", RainHold.active(context)) { RainHold.stop(context) }
        tryStop("wandel", WalkHold.active(context)) { WalkHold.stop(context) }
        tryStop("wind", WindHold.active(context)) { WindHold.stop(context) }
        tryStop("trein", TrainHold.active(context)) { TrainHold.stop(context) }
        return if (stopped.isEmpty()) "Geen actieve hold"
        else "Gestopt: ${stopped.joinToString(", ")}"
    }

    fun activeCount(context: Context): Int = listOf(
        HospitalHold.active(context),
        ThermalHold.active(context),
        LowBatteryHold.active(context),
        BedtimeFade.active(context),
        CallHold.active(context),
        GameHold.active(context),
        CinemaHold.active(context),
        NapHold.active(context),
        ConcertHold.active(context),
        FlightHold.active(context),
        MeetingHold.active(context),
        BikeHold.active(context),
        DriveHold.active(context),
        RainHold.active(context),
        WalkHold.active(context),
        WindHold.active(context),
        TrainHold.active(context),
    ).count { it }
}
