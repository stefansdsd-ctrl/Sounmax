package com.example.data

import android.content.Context

/** Zoek en toggle holds/caps. Vervangt de kapotte placeholder. */
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
        FeatureHit("nav", "Nav", "maps waze navigatie"),
        FeatureHit("ov", "OV", "ns trein tram bus ov transit"),
        FeatureHit("shop", "Shop", "winkel boodschappen bol"),
        FeatureHit("meet", "Meet", "zoom teams meet webex"),
        FeatureHit("pay", "Betaal", "bank betalen wallet"),
        FeatureHit("sleep", "Slaap", "slaap meditatie"),
        FeatureHit("weather", "Weer", "regen wind weer")
    )

    fun lastQuery(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("q", "") ?: ""

    fun saveQuery(context: Context, q: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("q", q).apply()
    }

    fun search(query: String): List<FeatureHit> {
        val tokens = query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return emptyList()
        return catalog.filter { hit ->
            val blob = "${hit.id} ${hit.title} ${hit.keys}".lowercase()
            tokens.all { blob.contains(it) }
        }
    }

    fun cycle(context: Context, id: String): String = when (id) {
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
        "nav" -> NavSoft.cycle(context)
        "ov" -> TransitSoft.cycle(context)
        "shop" -> ShopSoft.cycle(context)
        "meet" -> MeetSoft.cycle(context)
        "pay" -> PaySoft.cycle(context)
        "sleep" -> SleepSoft.cycle(context)
        "weather" -> WeatherSoft.cycle(context)
        else -> "Onbekend: $id"
    }
}
