package com.example.dsp

/** Recente NL-scenes, gemerged via SceneLookup. */
object NewNlScenes {
    val ALL = listOf(
        ListeningScene("spits", "Spits", "🚦", "Max ANC + stemmen in volle OV", "Philips TAH6519 Pro ANC", AncMode.STRONG, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("thuisavond", "Thuisavond", "💿", "Zacht + ruimtelijk, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("nachttrein", "Nachttrein", "🚆", "Sterke ANC, veilig volume in stille coupé", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("intercity", "Intercity", "🚄", "Adaptieve ANC + stabiele stream", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, preferredLdac = LdacQualityMode.BALANCED_660),
        ListeningScene("koffietent", "Koffietent", "☕", "Stemmen erdoor, muziek op achtergrond", "Podcast Voice", AncMode.AMBIENT),
        ListeningScene("huisarts", "Huisarts", "🏥", "Transparantie voor naam-oproepen", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ziekenhuis", "Ziekenhuis", "🏦", "Zacht, alert op omroepen", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("thuiskids", "Thuis + kids", "🧒", "Transparantie + veilig volume", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("regenfiets", "Regenfiets", "🌧", "Windfilter + extra aanwezigheid", "Outdoor Wind Guard", AncMode.WIND_GUARD, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("weekendmarkt", "Weekendmarkt", "🫺", "Transparantie tussen kramen", "Podcast Voice", AncMode.AMBIENT),
        ListeningScene("terrasavond", "Terrasavond", "🍹", "Stemmen + lichte wind, zacht", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("zondagochtend", "Zondagochtend", "🥐", "Zacht, ruim, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("bibliotheekstil", "Bibliotheek stil", "📚", "Minimaal volume, focus", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("wasstraat", "Wasstraat", "🚿", "Max ANC tegen hogedruk + muziek", "Philips TAH6519 Pro ANC", AncMode.STRONG, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("kerk", "Kerk / aula", "⛪", "Zacht + ruimtelijk, veilig", "Classical & Live Concert 3D", AncMode.OFF, safeVolume = true)
    )
}
