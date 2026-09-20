package com.example.dsp

object Batch99Scenes {
    val ALL = listOf(
        ListeningScene("ikearestaurant", "IKEA-restaurant", "🇸🇪", "Kantine-ruis weg, stem tafelgenoot", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("voetbalkantine", "Voetbalkantine", "⚽", "Echo + stemmen, veilig volume", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("campingdouche", "Campingdouche", "🚿", "Tegel-echo, podcast helder", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("belastingkantoor", "Belastingkantoor", "📄", "Wachtzaal, focus + stil", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("collegecampus", "Campus / aula", "🎓", "Hal-ruis weg, college helder", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("marktkoopman", "Weekmarkt", "🥒", "Kreten + wind, alert blijven", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true)
    )
}
