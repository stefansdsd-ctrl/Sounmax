package com.example.data

import android.content.Context

/** Zoek en toggle holds/caps. Prefix, bevat, en 1-teken typo. */
data class FeatureHit(val id: String, val title: String, val keys: String)

object FeatureFinder {
    private const val PREFS = "sounmax_feature_finder"

    private val catalog = listOf(
        FeatureHit("solo", "Solo", "solo hold een tegelijk"),
        FeatureHit("safe", "Veilig", "gehoor veilig cap volume"),
        FeatureHit("talk", "Gesprek", "gesprek talk spraak"),
        FeatureHit("door", "Deur", "deur bel deurbel"),
        FeatureHit("street", "Straat", "straat verkeer luisteren"),
        FeatureHit("find", "Zoek", "zoek headset beep vind"),
        FeatureHit("panic", "Alles uit", "panic stop alles uit hold"),
        FeatureHit("ride", "Rit", "uber bolt lyft taxi sixt chauffeur rit"),
        FeatureHit("health", "Zorg", "thuisarts apotheek zorg patient portaal"),
        FeatureHit("parcel", "Pakket", "postnl dhl dpd ups fedex gls pakket koerier bezorg"),
        FeatureHit("class", "Les", "magister somtoday itslearning classroom zermelo les school rooster"),
        FeatureHit("lang", "Taal", "duolingo babbel busuu mondly rosetta taal les uitspraak"),
        FeatureHit("pod", "Podcast", "podcast pocketcasts antennapod castbox luister"),
        FeatureHit("voice", "Spraak", "whatsapp telegram signal voice note spraakbericht"),
        FeatureHit("radio", "Radio", "tunein mytuner npo radio fm luisteren"),
        FeatureHit("tr", "Vertaal", "translate deepl vertalen vertaler live tolk"),
        FeatureHit("rec", "Opname", "recorder voicenote dictation otter opname memo voice recorder"),
        FeatureHit("hear", "Hoor", "transcribe amplifier talkback ondertitel live hoor geluid versterker"),
        FeatureHit("run", "Hardloop", "hardlopen rennen wind cardio joggen run"),
        FeatureHit("bike", "Fiets", "fiets fietsen cycling wind helm e-bike"),
        FeatureHit("walk", "Avondloop", "lopen wandelen avond donker verkeer walk"),
        FeatureHit("vehicle", "Rij-cap", "auto rijden vehicle claxon navigatie in_vehicle"),
        FeatureHit("still", "Bureau", "stilzitten bureau desk still werken kantoor"),
        FeatureHit("aftercall", "Na-bel", "na bel ophangen opbouw volume knal gesprek"),
        FeatureHit("data", "Data-cap", "data mobiel metered stream 4g 5g bundel"),
        FeatureHit("heat", "Warmte", "warmte hitte thermal throttling accu heet chip"),
        FeatureHit("psave", "Spaar", "spaarstand powersave batterijbesparing accubesparing zuinig"),
        FeatureHit("lowbatt", "Accu", "accu batterij laag leeg percentage 20 procent opladen"),
        FeatureHit("focus", "Focus", "niet storen dnd zen concentratie focusmodus stilte"),
        FeatureHit("ringer", "Stil", "tril stil silent ringer vibrate beltoon mute schakelaar"),
        FeatureHit("night", "Nacht", "nacht slaap stil 2230 bedtime quiet hours avond"),
        FeatureHit("nav", "Nav", "maps waze navigatie"),
        FeatureHit("navduck", "NavDuck", "navduck duck maps waze aanwijzing guidance stem route"),
        FeatureHit("alarm", "Alarm", "alarm wekker klok ringing clock duck hoorbaar"),
        FeatureHit("ring", "Bel", "bel bellen ringtone inkomend gemist call duck hoorbaar"),
        FeatureHit("access", "Toegang", "toegang talkback toegankelijkheid screenreader klik duck"),
        FeatureHit("handoff", "Wissel", "wissel handoff speler spotify yt music app wissel knal"),
        FeatureHit("resume", "Hervat", "hervat pauze reclame resume play stilte knal"),
        FeatureHit("hunt", "Zoek", "zoek bladeren snel skippen drie skips hunt cap"),
        FeatureHit("arm", "Wacht", "wacht volume pauze slotscherm knal play arm cap"),
        FeatureHit("skip", "Skip", "skip nummer wissel track volgende kort stilte knal"),
        FeatureHit("morning", "Ochtend", "ochtend eerste play dag volume knal vergeten luid"),
        FeatureHit("timer", "Timer", "timer kookwekker countdown piep keuken duck hoorbaar"),
        FeatureHit("tts", "TTS", "tts talkback voorlezen toegankelijkheid schermlezer selecteer om te spreken duck"),
        FeatureHit("ov", "OV", "ns trein tram bus ov transit"),
        FeatureHit("shop", "Shop", "winkel boodschappen bol"),
        FeatureHit("meet", "Meet", "zoom teams meet webex"),
        FeatureHit("pay", "Betaal", "bank betalen wallet"),
        FeatureHit("sleep", "Slaap", "slaap meditatie"),
        FeatureHit("weather", "Weer", "regen wind weer"),
        FeatureHit("leak", "Lek", "speaker lek headset a2dp geluid lekt"),
        FeatureHit("roam", "Roam", "roaming buitenland data stream cap"),
        FeatureHit("wifi", "Wi-Fi", "wifi ssid thuis netwerk volume plek"),
        FeatureHit("route", "Route", "route speaker a2dp wegvalt losgekoppeld knal telefoon luidspreker"),
        FeatureHit("net", "Net", "offline net internet cloud ai gemini vliegtuig geen verbinding"),
        FeatureHit("spike", "Sprong", "sprong volume knal stap omhoog per ongeluk piek dempen"),
        FeatureHit("boot", "Start", "start openen opstarten boot vergeten luid knal eerste keer"),
        FeatureHit("mic", "Mic", "mic microfoon opname recorder spraakbericht vertaler dichtklappen"),
        FeatureHit("cam", "Cam", "camera foto video sluiter cam duck"),
        FeatureHit("wacht", "Wacht", "wacht wachttoon voip bellen signalering ringback"),
        FeatureHit("game", "Game", "game spel sfx geluidseffect duck muziek"),
        FeatureHit("ui", "Tik", "tik toets klik ui sonification keyboard duck muziek"),
        FeatureHit("micback", "Herstel", "herstel terug volume na mic opname spraakbericht ramp"),
        FeatureHit("chat", "Chat", "chat whatsapp signal telegram bericht mail sms duck"),
        FeatureHit("ping", "Ping", "ping melding notificatie alarm beltoon hoorbaar anc duck"),
        FeatureHit("assist", "Assist", "assistent gemini assistant stem ok google hey duck"),
        FeatureHit("rain", "Regen", "regen bui wind verkeer hoorbaar cap luisteren"),
        FeatureHit("library", "Bieb", "bibliotheek lezen studeren stil rustig cap"),
        FeatureHit("office", "Kantoor", "kantoor werk werkdag bureau 9 17 cap"),
        FeatureHit("agenda", "Agenda", "agenda meeting afspraak vergadering calendar demp"),
        FeatureHit("link", "Link", "link latency ping wifi 4g internet meting snelheid verbinding"),
        FeatureHit("buds", "Buds", "buds a2dp route headset gezondheid speaker lek check"),
        FeatureHit("sos", "Nood", "nood nl-alert amber emergency overheid sirene alert duck")
    )

    fun lastQuery(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("q", "") ?: ""

    fun saveQuery(context: Context, q: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("q", q).apply()
    }

    fun recent(context: Context): List<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("recent", "")
            .orEmpty()
            .split(',')
            .filter { it.isNotBlank() }

    fun search(query: String): List<FeatureHit> {
        val tokens = query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return emptyList()
        return catalog.mapNotNull { hit ->
            val words = "${hit.id} ${hit.title} ${hit.keys}".lowercase().split(Regex("\\s+"))
            val score = tokens.sumOf { t -> scoreToken(words, t) }
            if (score == 0) null else score to hit
        }.sortedByDescending { it.first }.map { it.second }
    }

    private fun scoreToken(words: List<String>, token: String): Int {
        if (words.any { it.startsWith(token) }) return 3
        if (words.any { it.contains(token) }) return 2
        if (token.length >= 4 && words.any { levenshtein(it, token) <= 1 }) return 1
        return 0
    }

    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (kotlin.math.abs(a.length - b.length) > 1) return 2
        val prev = IntArray(b.length + 1) { it }
        val cur = IntArray(b.length + 1)
        for (i in a.indices) {
            cur[0] = i + 1
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                cur[j + 1] = minOf(cur[j] + 1, prev[j + 1] + 1, prev[j] + cost)
            }
            for (j in prev.indices) prev[j] = cur[j]
        }
        return prev[b.length]
    }

    fun cycle(context: Context, id: String): String {
        remember(context, id)
        return when (id) {
            "solo" -> HoldSolo.toggle(context)
            "safe" -> {
                if (!HearingGuard.enabled(context)) HearingGuard.setEnabled(context, true)
                HearingGuard.setHardCap(context, !HearingGuard.hardCap(context))
                HearingGuard.status(context)
            }
            "talk" -> TalkSoft.cycle(context)
            "door" -> DoorListen.cycle(context)
            "street" -> StreetListen.cycle(context)
            "find" -> FindBeep.cycle(context)
            "panic" -> HoldPanic.stopAll(context)
            "ride" -> RideSoft.cycle(context)
            "health" -> HealthSoft.cycle(context)
            "parcel" -> ParcelSoft.cycle(context)
            "class" -> ClassSoft.cycle(context)
            "lang" -> LanguageSoft.cycle(context)
            "pod" -> PodcastSoft.cycle(context)
            "voice" -> VoiceNoteSoft.cycle(context)
            "radio" -> RadioSoft.cycle(context)
            "tr" -> TranslateSoft.cycle(context)
            "rec" -> RecorderSoft.cycle(context)
            "hear" -> HearSoft.cycle(context)
            "run" -> RunWind.cycle(context)
            "bike" -> BikeWind.cycle(context)
            "walk" -> WalkSafe.cycle(context)
            "vehicle" -> VehicleSafe.cycle(context)
            "still" -> StillSafe.cycle(context)
            "aftercall" -> PostCallRamp.cycle(context)
            "data" -> MeteredCap.cycle(context)
            "heat" -> ThermalCap.cycle(context)
            "psave" -> PowerSaveCap.cycle(context)
            "lowbatt" -> LowBatteryCap.cycle(context)
            "focus" -> FocusQuietCap.cycle(context)
            "ringer" -> SilentRingerCap.cycle(context)
            "night" -> NightQuietCap.cycle(context)
            "nav" -> NavSoft.cycle(context)
            "navduck" -> NavDuck.cycle(context)
            "alarm" -> AlarmDuck.cycle(context)
            "ring" -> RingDuck.cycle(context)
            "access" -> AccessDuck.cycle(context)
            "handoff" -> HandoffDuck.cycle(context)
            "resume" -> ResumeDuck.cycle(context)
            "hunt" -> HuntDuck.cycle(context)
            "arm" -> ArmDuck.cycle(context)
            "skip" -> SkipDuck.cycle(context)
            "morning" -> MorningDuck.cycle(context)
            "timer" -> TimerDuck.cycle(context)
            "tts" -> TtsDuck.cycle(context)
            "ov" -> TransitSoft.cycle(context)
            "shop" -> ShopSoft.cycle(context)
            "meet" -> MeetSoft.cycle(context)
            "pay" -> PaySoft.cycle(context)
            "sleep" -> SleepSoft.cycle(context)
            "weather" -> WeatherSoft.cycle(context)
            "leak" -> LeakGuard.cycle(context)
            "roam" -> RoamCap.cycle(context)
            "wifi" -> WifiVolumeMemory.cycle(context)
            "route" -> RouteCap.cycle(context)
            "net" -> OfflineGuard.cycle(context)
            "spike" -> SpikeGuard.cycle(context)
            "boot" -> BootQuiet.cycle(context)
            "mic" -> MicLive.cycle(context)
            "cam" -> CamDuck.cycle(context)
            "wacht" -> WaitDuck.cycle(context)
            "sos" -> EmergencyDuck.cycle(context)
            "game" -> GameDuck.cycle(context)
            "ui" -> UiDuck.cycle(context)
            "micback" -> MicRestore.cycle(context)
            "chat" -> ChatDuck.cycle(context)
            "ping" -> PingDuck.cycle(context)
            "assist" -> AssistDuck.cycle(context)
            "rain" -> RainListen.cycle(context)
            "library" -> LibrarySoft.cycle(context)
            "office" -> OfficeSoft.cycle(context)
            "agenda" -> MeetingDuck.cycle(context)
            "link" -> LinkProbe.cycle(context)
            "buds" -> BudHealth.cycle(context)
            else -> "Onbekend: $id"
        }
    }

    private fun remember(context: Context, id: String) {
        if (id.isBlank()) return
        val next = (listOf(id) + recent(context)).distinct().take(6).joinToString(",")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString("recent", next).apply()
    }
}
