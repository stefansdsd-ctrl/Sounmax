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

    data class Item(val id: String, val name: String)

    private fun registry(context: Context): List<Triple<String, String, () -> Boolean>> = listOf(
        Triple("hospital", "ziekenhuis", { HospitalHold.active(context) }),
        Triple("thermal", "warmte", { ThermalHold.active(context) }),
        Triple("lowbatt", "lage accu", { LowBatteryHold.active(context) }),
        Triple("bedtime", "bedtime", { BedtimeFade.active(context) }),
        Triple("call", "bel", { CallHold.active(context) }),
        Triple("game", "game", { GameHold.active(context) }),
        Triple("cinema", "cinema", { CinemaHold.active(context) }),
        Triple("nap", "dutje", { NapHold.active(context) }),
        Triple("concert", "concert", { ConcertHold.active(context) }),
        Triple("flight", "vlucht", { FlightHold.active(context) }),
        Triple("meeting", "meeting", { MeetingHold.active(context) }),
        Triple("bike", "fiets", { BikeHold.active(context) }),
        Triple("drive", "rit", { DriveHold.active(context) }),
        Triple("rain", "regen", { RainHold.active(context) }),
        Triple("walk", "wandel", { WalkHold.active(context) }),
        Triple("wind", "wind", { WindHold.active(context) }),
        Triple("train", "trein", { TrainHold.active(context) }),
    )

    fun activeList(context: Context): List<Item> =
        registry(context).mapNotNull { (id, name, check) ->
            if (check()) Item(id, name) else null
        }

    fun activeCount(context: Context): Int = activeList(context).size

    fun stopOne(context: Context, id: String): String {
        when (id) {
            "hospital" -> HospitalHold.stop(context)
            "thermal" -> ThermalHold.stop(context)
            "lowbatt" -> LowBatteryHold.stop(context)
            "bedtime" -> BedtimeFade.stop(context)
            "call" -> CallHold.stop(context)
            "game" -> GameHold.stop(context)
            "cinema" -> CinemaHold.stop(context)
            "nap" -> NapHold.stop(context)
            "concert" -> ConcertHold.stop(context)
            "flight" -> FlightHold.stop(context)
            "meeting" -> MeetingHold.stop(context)
            "bike" -> BikeHold.stop(context)
            "drive" -> DriveHold.stop(context)
            "rain" -> RainHold.stop(context)
            "walk" -> WalkHold.stop(context)
            "wind" -> WindHold.stop(context)
            "train" -> TrainHold.stop(context)
            else -> return "Onbekend"
        }
        return "Gestopt: $id"
    }
}
