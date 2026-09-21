package com.example.dsp

object Batch112Scenes {
    val ALL = listOf(
        ListeningScene("zolderwerk", "Zolderwerk", "🪟", "Warm + knars, focus veilig", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("bakfietsrit", "Bakfiets", "🚲", "Wind + kids, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("parkeerdek", "Parkeerdek", "🅿️", "Open dek, wind + auto's", "Philips TAH6519 Pro ANC", AncMode.WIND_GUARD),
        ListeningScene("printkamer", "Printkamer", "🖨️", "Zoem + papier, korte focus", "Vocal & Acoustic Warmth", AncMode.ANC),
        ListeningScene("wasdrogerthuis", "Wasdroger", "🧺", "Laag zoem thuis", "Night Chill & Lo-Fi Relax", AncMode.ANC, safeVolume = true),
        ListeningScene("brievenbusrij", "Brievenbus", "📮", "Korte rij, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("sluiswacht", "Sluiswacht", "🚢", "Water + wind, wachten", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true)
    )
}
