package com.example.data

import android.content.Context

/** Zoek holds/scenes op trefwoord en start/stop ze. */
object FeatureFinder {
    data class Hit(val id: String, val title: String, val hint: String)

    private const val PREFS = "sounmax_feature_finder"
    private const val KEY_Q = "last_query"

    val catalog = listOf(
        Hit("thermal", "Warmte-hold", "volume-cap bij ≥40°C"),
        Hit("hospital", "Ziekenhuis-hold", "stil + prioriteit 1"),
        Hit("lowbatt", "Lage-accu-hold", "DSP-spaarstand"),
        Hit("bedtime", "Bedtime-fade", "nachtvolume"),
        Hit("bt", "BT-herstel", "scene+volume na reconnect"),
        Hit("dose", "Gehoorcap", "weekdosis WHO"),
        Hit("walk", "Wandel-hold", "buiten + veiligheid"),
        Hit("bike", "Fiets-hold", "omgevingsgeluid"),
        Hit("drive", "Rit-hold", "auto"),
        Hit("wind", "Wind-hold", "ruis dempen"),
        Hit("rain", "Regen-hold", "weer"),
        Hit("focus", "Focus", "DND + EQ"),
        Hit("sleep", "Slaap", "timer + fade"),
        Hit("call", "Bel-hold", "transparantie tijdens gesprek"),
        Hit("game", "Game-hold", "lage latency"),
        Hit("cinema", "Cinema-hold", "film + dynamiek"),
        Hit("nap", "Dutje-hold", "kort slaap"),
        Hit("concert", "Concert-hold", "piekbeveiliging"),
        Hit("flight", "Vlucht-hold", "druk + ANC"),
        Hit("meeting", "Meeting-hold", "spraak"),
        Hit("train", "Trein-hold", "openbaar vervoer"),
        Hit("panic", "Alles uit", "stop alle holds"),
    )

    fun search(query: String): List<Hit> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return catalog
        return catalog.filter {
            it.id.contains(q) || it.title.lowercase().contains(q) || it.hint.contains(q)
        }
    }

    fun lastQuery(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_Q, "") ?: ""

    fun saveQuery(context: Context, q: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_Q, q).apply()
    }

    fun label(context: Context, id: String): String = when (id) {
        "thermal" -> ThermalHold.label(context)
        "hospital" -> HospitalHold.label(context)
        "lowbatt" -> LowBatteryHold.label(context)
        "bedtime" -> BedtimeFade.label(context)
        "bt" -> BtReconnectRestore.label(context)
        "panic" -> "Alles uit (${HoldPanic.activeCount(context)})"
        else -> catalog.firstOrNull { it.id == id }?.title ?: id
    }

    fun cycle(context: Context, id: String): String = when (id) {
        "thermal" -> ThermalHold.cycle(context)
        "hospital" -> HospitalHold.cycle(context)
        "lowbatt" -> LowBatteryHold.cycle(context)
        "bedtime" -> BedtimeFade.cycle(context)
        "bike" -> BikeHold.cycle(context)
        "drive" -> DriveHold.cycle(context)
        "rain" -> RainHold.cycle(context)
        "game" -> GameHold.cycle(context)
        "cinema" -> CinemaHold.cycle(context)
        "nap" -> NapHold.cycle(context)
        "concert" -> ConcertHold.cycle(context)
        "flight" -> FlightHold.cycle(context)
        "meeting" -> MeetingHold.cycle(context)
        "call" -> CallHold.cycle(context)
        "walk" -> WalkHold.cycle(context)
        "wind" -> WindHold.cycle(context)
        "train" -> TrainHold.cycle(context)
        "focus" -> FocusHours.cycleWindow(context)
        "sleep" -> SleepHours.cycleWindow(context)
        "bt" -> BtReconnectRestore.toggle(context).let { BtReconnectRestore.label(context) }
        "panic" -> HoldPanic.stopAll(context)
        else -> label(context, id)
    }
}
