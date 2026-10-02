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
        Hit("street", "Straat", "25 s volume 15%"),
        Hit("find", "Zoekpiep", "8 s piep + trilling"),
        Hit("codec", "Codec", "LDAC/AAC/SBC-probe"),
        Hit("hear", "Bel-transparantie", "spraakmodus software"),
        Hit("slim", "Compact", "zeldzame scenes verbergen"),
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
        Hit("duck", "Meld-duck", "8 s volume 40% bij melding"),
        Hit("pocket", "Zak-guard", "proximity sluit volume-omhoog"),
        Hit("shake", "Schud-aware", "schud → 12 s volume 25%"),
        Hit("flip", "Flip-stil", "face-down → volume 18%"),
        Hit("charge", "Nachtladen", "22–07 aan lader, cap 35%"),
        Hit("ramp", "Zachte start", "geen volume-klap bij connect"),
        Hit("leak", "Stilte-cap", "trillen/stil → max 50%"),
        Hit("speaker", "Speaker-drop", "loskoppelen → 28%"),
        Hit("alarm", "Wekker-cap", "25 min voor wekker, cap 40%"),
        Hit("route", "Route-drop", "audio naar speaker → 24%"),
        Hit("peak", "Piek-cap", "boven 90% → 78%"),
        Hit("ring", "Beltoon-duck", "beltoon → 20%"),
        Hit("morning", "Ochtend-cap", "06–09 boven 62% → 48%"),
        Hit("voip", "VoIP-duck", "WhatsApp/Teams → 16%"),
        Hit("evening", "Avond-cap", "21–23 boven 68% → 50%"),
        Hit("restore", "Duck-herstel", "volume terug na bel/VoIP"),
        Hit("jump", "Sprong-cap", "plotse +3 stappen terug"),
        Hit("lunch", "Lunch-cap", "12–13:30 boven 72% → 55%"),
        Hit("weekend", "Weekend-cap", "za/zo 10–18 boven 80% → 64%"),
        Hit("metered", "Mobiel-cap", "metered/mobiel boven 70% → 52%"),
        Hit("night", "Nacht-cap", "23–06 boven 60% → 42%"),
        Hit("dnd", "Niet-storen-cap", "DND aan boven 65% → 45%"),
        Hit("airplane", "Vliegtuig-cap", "vliegtuigmodus boven 68% → 48%"),
        Hit("saver", "Spaar-cap", "batterijspaarstand boven 74% → 56%"),
        Hit("screen", "Scherm-uit-cap", "scherm uit boven 76% → 58%"),
        Hit("hotspot", "Hotspot-cap", "hotspot aan boven 70% → 50%"),
        Hit("lock", "Slot-cap", "vergrendeld boven 72% → 54%"),
        Hit("silent", "Stil-cap", "stille beltoon boven 64% → 46%"),
        Hit("vpn", "VPN-cap", "VPN aan boven 70% → 52%"),
        Hit("wired", "Kabel-cap", "kabel/USB boven 78% → 62%"),
        Hit("unplug", "Los-pauze", "headset eruit pauzeert media"),
        Hit("hbatt", "Headset-accu", "percentage via Bluetooth"),
    )

    private val aliases = mapOf(
        "wandelen" to "walk", "buiten" to "walk", "lopen" to "walk",
        "fietsen" to "bike", "auto" to "drive", "rijden" to "drive",
        "waaien" to "wind", "regenachtig" to "rain", "weer" to "rain",
        "werk" to "focus", "dnd" to "dnd", "nacht" to "night", "slaapstand" to "sleep",
        "gesprek" to "talk", "praten" to "talk", "praat" to "talk", "talk" to "talk",
        "deur" to "door", "bel" to "door", "klingel" to "door", "doorbell" to "door",
        "straat" to "street", "oversteken" to "street", "zebrapad" to "street", "kruisen" to "street",
        "zoek" to "find", "piep" to "find", "find" to "find", "beep" to "find", "zoeken" to "find",
        "koptelefoon" to "find", "headset" to "find",
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
        "pauze" to "ear", "rust" to "ear", "oor" to "ear", "break" to "ear",
        "ldac" to "codec", "aac" to "codec", "sbc" to "codec", "aptx" to "codec", "codec" to "codec",
        "transparantie" to "hear", "transparency" to "hear", "spraakmodus" to "hear",
        "compact" to "slim", "slim" to "slim", "catalogus" to "slim", "scenes" to "slim",
        "duck" to "duck", "melding" to "duck", "notificatie" to "duck",
        "zak" to "pocket", "pocket" to "pocket", "broekzak" to "pocket",
        "schud" to "shake", "shake" to "shake", "schudden" to "shake", "omgeving" to "shake",
        "flip" to "flip", "tafel" to "flip", "facedown" to "flip", "omdraaien" to "flip",
        "laden" to "charge", "lader" to "charge", "nachtladen" to "charge", "opladen" to "charge",
        "start" to "ramp", "ramp" to "ramp", "klap" to "ramp", "zacht" to "ramp",
        "lek" to "leak", "lekkage" to "leak", "stilte" to "leak", "kantoor" to "leak",
        "los" to "speaker", "speaker" to "speaker", "disconnect" to "speaker",
        "wek" to "alarm", "wekker" to "alarm", "alarm" to "alarm",
        "route" to "route", "lawaai" to "route", "plug" to "route",
        "piek" to "peak", "max" to "peak", "hard" to "peak",
        "beltoon" to "ring", "ring" to "ring", "ringtone" to "ring",
        "ochtend" to "morning", "morning" to "morning", "ocht" to "morning",
        "voip" to "voip", "whatsapp" to "voip", "teams" to "voip", "zoom" to "voip",
        "avond" to "evening", "evening" to "evening",
        "herstel" to "restore", "terug" to "restore", "restore" to "restore",
        "sprong" to "jump", "jump" to "jump", "rocker" to "jump",
        "lunch" to "lunch", "middag" to "lunch",
        "weekend" to "weekend", "zaterdag" to "weekend", "zondag" to "weekend",
        "mobiel" to "metered", "metered" to "metered", "data" to "metered", "4g" to "metered", "5g" to "metered",
        "night" to "night", "storen" to "dnd", "nietstoren" to "dnd",
        "vliegtuigmodus" to "airplane", "airplane" to "airplane", "flightmode" to "airplane",
        "spaar" to "saver", "saver" to "saver", "spaarstand" to "saver", "powersave" to "saver",
        "scherm" to "screen", "screen" to "screen", "schermuit" to "screen", "zakmodus" to "screen",
        "hotspot" to "hotspot", "tether" to "hotspot", "delen" to "hotspot",
        "slot" to "lock", "lock" to "lock", "vergrendeld" to "lock", "keyguard" to "lock",
        "stil" to "silent", "silent" to "silent", "tril" to "silent", "mute" to "silent",
        "vpn" to "vpn", "kabel" to "wired", "wired" to "wired", "usb" to "wired", "oortjes" to "wired",
        "loskoppel" to "unplug", "unplug" to "unplug", "pauzeer" to "unplug",
        "headsetaccu" to "hbatt", "accupercent" to "hbatt", "batt" to "hbatt"
    )

    private val noSolo = setOf(
        "panic", "solo", "why", "dose", "bt", "reset", "next", "safe", "ear",
        "talk", "door", "street", "find", "codec", "hear", "slim", "duck", "pocket", "shake", "flip", "charge",
        "ramp", "leak", "speaker", "alarm", "route", "peak", "ring", "morning", "voip", "evening", "restore", "jump", "lunch",
        "weekend", "metered", "night", "dnd", "airplane", "saver", "screen", "hotspot", "lock", "silent", "vpn", "wired", "unplug", "hbatt"
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
        "street" -> StreetListen.label(context)
        "find" -> FindBeep.label(context)
        "codec" -> CodecProbe.label(context)
        "hear" -> CallTransparency.label(context)
        "slim" -> HiddenScenes.chipLabel(context)
        "duck" -> NotifyDuck.label(context)
        "pocket" -> PocketGuard.label(context)
        "shake" -> ShakeAware.label(context)
        "flip" -> FlipQuiet.label(context)
        "charge" -> ChargeNightCap.label(context)
        "ramp" -> ConnectRamp.label(context)
        "leak" -> QuietLeakCap.label(context)
        "speaker" -> SpeakerGuard.label(context)
        "alarm" -> AlarmSoon.label(context)
        "route" -> NoisyRoute.label(context)
        "peak" -> PeakCap.label(context)
        "ring" -> RingDuck.label(context)
        "morning" -> MorningSoft.label(context)
        "voip" -> CommDuck.label(context)
        "evening" -> EveningSoft.label(context)
        "restore" -> DuckRestore.label(context)
        "jump" -> JumpGuard.label(context)
        "lunch" -> LunchSoft.label(context)
        "weekend" -> WeekendSoft.label(context)
        "metered" -> MeteredSoft.label(context)
        "night" -> NightSoft.label(context)
        "dnd" -> DndSoft.label(context)
        "airplane" -> AirplaneSoft.label(context)
        "saver" -> SaverSoft.label(context)
        "screen" -> ScreenOffSoft.label(context)
        "hotspot" -> HotspotSoft.label(context)
        "lock" -> LockSoft.label(context)
        "silent" -> SilentSoft.label(context)
        "vpn" -> VpnSoft.label(context)
        "wired" -> WiredSoft.label(context)
        "unplug" -> UnplugPause.label(context)
        "hbatt" -> HeadsetBatt.label(context)
        else -> catalog.firstOrNull { it.id == id }?.title ?: id
    }

    fun cycle(context: Context, id: String): String {
        if (id !in noSolo) {
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
            "street" -> StreetListen.cycle(context)
            "find" -> FindBeep.cycle(context)
            "codec" -> CodecProbe.cycle(context)
            "hear" -> CallTransparency.cycle(context)
            "slim" -> {
                HiddenScenes.toggleAutoSlim(context)
                HiddenScenes.chipLabel(context)
            }
            "duck" -> NotifyDuck.cycle(context)
            "pocket" -> PocketGuard.cycle(context)
            "shake" -> ShakeAware.cycle(context)
            "flip" -> FlipQuiet.cycle(context)
            "charge" -> ChargeNightCap.cycle(context)
            "ramp" -> ConnectRamp.cycle(context)
            "leak" -> QuietLeakCap.cycle(context)
            "speaker" -> SpeakerGuard.cycle(context)
            "alarm" -> AlarmSoon.cycle(context)
            "route" -> NoisyRoute.cycle(context)
            "peak" -> PeakCap.cycle(context)
            "ring" -> RingDuck.cycle(context)
            "morning" -> MorningSoft.cycle(context)
            "voip" -> CommDuck.cycle(context)
            "evening" -> EveningSoft.cycle(context)
            "restore" -> DuckRestore.cycle(context)
            "jump" -> JumpGuard.cycle(context)
            "lunch" -> LunchSoft.cycle(context)
            "weekend" -> WeekendSoft.cycle(context)
            "metered" -> MeteredSoft.cycle(context)
            "night" -> NightSoft.cycle(context)
            "dnd" -> DndSoft.cycle(context)
            "airplane" -> AirplaneSoft.cycle(context)
            "saver" -> SaverSoft.cycle(context)
            "screen" -> ScreenOffSoft.cycle(context)
            "hotspot" -> HotspotSoft.cycle(context)
            "lock" -> LockSoft.cycle(context)
            "silent" -> SilentSoft.cycle(context)
            "vpn" -> VpnSoft.cycle(context)
            "wired" -> WiredSoft.cycle(context)
            "unplug" -> UnplugPause.cycle(context)
            "hbatt" -> HeadsetBatt.cycle(context)
            else -> label(context, id)
        }
    }
}
