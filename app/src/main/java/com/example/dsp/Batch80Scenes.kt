package com.example.dsp

object Batch80Scenes {
    val ALL = listOf(
        ListeningScene("libraryplus", "Bieb+", "📚", "Stilte + fluister-alert", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("gympeak", "Sportschool-piek", "🏋️", "Bass-kick, stemmen weg", "Electronic & Festival EDM", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("rainwalkplus", "Regenwandeling+", "🌧️", "Druppel + windfilter", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kitchensteam", "Keukenstoom", "🍲", "Afzuigkap + timer hoorbaar", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("latefocus", "Nacht-focus", "🌙", "Warm, geen pieken", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("platformrush", "Perron-spits", "🚉", "Omroep eerst, ratel weg", "Philips TAH6519 Pro ANC", AncMode.AMBIENT, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true),
        ListeningScene("cafechat", "Café-praat", "☕", "Stemmen voor, espresso-ruis weg", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("sleepwind", "Slaap-wind", "🛏️", "Zacht + ANC licht", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true)
    )
}
