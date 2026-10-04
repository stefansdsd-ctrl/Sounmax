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
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return catalog.filter { hit ->
            hit.title.lowercase().contains(q) || hit.keys.contains(q) || hit.id.contains(q)
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
