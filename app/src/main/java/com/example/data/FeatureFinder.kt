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
        Hit("talk", "Gesprek", "2 min volume 35%"),
        Hit("door", "Deur", "90 s volume 20%"),
        Hit("game", "Game-hold", "lage latency"),
        Hit("cinema", "Cinema-hold", "film + dynamiek"),
        Hit("nap", "Dutje-hold", "kort slaap"),
        Hit("concert", "Concert-hold", "piekbeveiliging"),
        Hit("flight", "Vlucht-hold", "druk + ANC"),
        Hit("meeting", "Meeting-hold", "spraak"),
        Hit("train", "Trein-hold", "openbaar vervoer"),
        Hit("panic", "Alles uit", "stop alle holds"),
        Hit("solo", "Solo-hold", "één leefstijl-hold tegelijk"),
        Hit("why", "Waarom nu", "uitleg suggestie"),
        Hit("reset", "Audio-reset", "holds uit, keten schoon"),
        Hit("next", "Volgende slot", "wat komt erna"),
        Hit("safe", "Veilig", "reset + hard gehoorcap 70%"),
        Hit("ear", "Oorpauze", "45 min luisteren → 5 min stil"),
    )

    private val aliases = mapOf(
        "wandelen" to "walk", "buiten" to "walk", "lopen" to "walk",
        "fietsen" to "bike", "auto" to "drive", "rijden" to "drive",
        "waaien" to "wind", "regenachtig" to "rain", "weer" to "rain",
        "werk" to "focus", "dnd" to "focus", "nacht" to "sleep", "slaapstand" to "sleep",
        "gesprek" to "talk", "praten" to "talk", "praat" to "talk", "talk" to "talk",
        "deur" to "door", "bel" to "door", "klingel" to "door", "doorbell" to "door",
        "bellen" to "call", "film" to "cinema",
        "vliegtuig" to "flight", "ov" to "train", "metro" to "train",
        "stop" to "panic", "uit" to "panic", "reset" to "reset",
        "accu" to "lowbatt", "batterij" to "lowbatt", "warm" to "thermal",
        "bus" to "train", "tram" to "train", "ns" to "train",
        "gamen" to "game", "meeting" to "meeting", "vergadering" to "meeting",
        "dutje" to "nap", "slaapje" to "nap", "concert" to "concert",
        "solo" to "solo", "één" to "solo", "een" to "solo",
        "waarom" to "why", "uitleg" to "why", "dosis" to "dose",
        "schoon" to "reset", "resetten" to "reset", "volgende" to "next", "straks" to "next",
        "eq" to "focus", "equalizer" to "focus",
        "veilig" to "safe", "safety" to "safe", "oorpauze" to "ear",
        "gehoor" to "safe", "cap" to "safe", "bescherm" to "safe",
        "pauze" to "ear", "rust" to "ear", "oor" to "ear", "break" to "ear"
    )

    fun search(query: String): List<Hit> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return catalog
        val aliasId = aliases[q]
        if (aliasId != null) return catalog.filter { it.id == aliasId }
        return catalog.filter {
            it.id.contains(q) || it.title.lowercase().contains(q) || it.hint.contains(q) ||
                aliases.any { (k, v) -> v == it.id && k.contains(q) }
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
        "walk" -> WalkHold.label(context)
        "wind" -> WindHold.label(context)
        "train" -> TrainHold.label(context)
        "focus" -> FocusHours.label(context)
        "sleep" -> SleepHours.label(context)
        "panic" -> "Alles uit (${HoldPanic.activeCount(context)})"
        "solo" -> HoldSolo.label(context)
        "why" -> WhyNow.text(context)
        "reset" -> AudioReset.label(context)
        "next" -> NextHint.short()
        "safe" -> QuickSafe.label(context)
        "ear" -> EarRest.label(context)
        "talk" -> TalkSoft.label(context)
        "door" -> DoorListen.label(context)
        else -> catalog.firstOrNull { it.id == id }?.title ?: id
    }

    fun cycle(context: Context, id: String): String {
        if (id != "panic" && id != "solo" && id != "why" && id != "dose" && id != "bt" && id != "reset" && id != "next" && id != "safe" && id != "ear" && id != "talk" && id != "door") {
            HoldSolo.prepareStart(context, id)
        }
        return when (id) {
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
            "solo" -> HoldSolo.toggle(context)
            "why" -> WhyNow.text(context)
            "reset" -> AudioReset.run(context)
            "next" -> NextHint.short()
            "safe" -> QuickSafe.run(context)
            "ear" -> EarRest.cycle(context)
            "talk" -> TalkSoft.cycle(context)
            "door" -> DoorListen.cycle(context)
            else -> label(context, id)
        }
    }
}
