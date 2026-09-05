package com.example.dsp

/** Recente NL-scenes, gemerged via SceneLookup. */
object NewNlScenes {
    val ALL = listOf(
        ListeningScene("spits", "Spits", "🚦", "Max ANC + stemmen in volle OV", "Philips TAH6519 Pro ANC", AncMode.STRONG, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("thuisavond", "Thuisavond", "💽", "Zacht + ruimtelijk, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true)
    )
}
