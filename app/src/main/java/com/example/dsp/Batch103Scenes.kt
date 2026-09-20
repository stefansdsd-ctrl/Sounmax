package com.example.dsp

object Batch103Scenes {
    val ALL = listOf(
        ListeningScene("huiswerktafel", "Huiswerktafel", "📝", "Focus + fluister, veilig volume", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("collegecampus", "Collegecampus", "🎓", "Plein + aula, alert blijven", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("marktplein", "Marktplein", "🧺", "Kraampjes + wind, veilig", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("ziekenhuisgang", "Ziekenhuisgang", "🏥", "Paginering + fluister, veilig", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("wachtkamerplus", "Wachtkamer", "🪑", "TV + praat, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("biebfoyer", "Bieb-foyer", "📚", "Inloop + stiltezone, veilig", "Flat Studio Monitor (0 dB)", AncMode.AMBIENT, safeVolume = true)
    )
}
