package com.example.dsp

object Batch97Scenes {
    val ALL = listOf(
        ListeningScene("hardlopen", "Hardlopen", "🏃", "Wind + stappen, volume veilig", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("teamsvergadering", "Teams / vergadering", "👥", "Collega's hoorbaar, echo weg", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("dierentuin", "Dierentuin", "🦁", "Kinderen + kooien, alert blijven", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("estepwind", "E-step / scooter", "🛵", "Windruis weg, verkeer hoorbaar", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("nachtmarkt", "Nachtmarkt", "🌆", "Kraampjes-ruis, stemmen helder", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("wasruimte", "Wasruimte / droger", "🧺", "Laag brom weg, podcast helder", "Podcast Voice", AncMode.STRONG, safeVolume = true)
    )
}
