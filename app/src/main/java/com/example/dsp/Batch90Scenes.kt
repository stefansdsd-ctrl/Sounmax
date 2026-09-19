package com.example.dsp

object Batch90Scenes {
    val ALL = listOf(
        ListeningScene("postnlpunt", "PostNL-punt", "📦", "Scanner + bel, naam-omroep", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("etosrij", "Etos-rij", "💊", "Kassa-piep plat, stem voor", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("decathlonhal", "Decathlon-hal", "🏀", "Hoge hal, echo dempen", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("tankstation", "Tankstation", "⛽", "Pomp + wind, omroep alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kapperszaak", "Kapperszaak", "✂️", "Föhn + gesprek, zacht", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("flixbus", "Flixbus", "🚌", "Lange rit, motor-dreun weg", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true)
    )
}
