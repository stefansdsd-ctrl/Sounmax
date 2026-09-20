package com.example.dsp

object Batch100Scenes {
    val ALL = listOf(
        ListeningScene("rechtbank", "Rechtbank", "⚖️", "Stilteplicht, stem advocaat helder", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("notaris", "Notaris", "🖋️", "Stille kamer, documenten + stem", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("fietsenmaker", "Fietsenmaker", "🚲", "Compressor + bel, podcast helder", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("waterbus", "Waterbus / pont", "⛴️", "Motor + wind, alert blijven", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("consultatiebureau", "Consultatiebureau", "👶", "Wachtzaal kids, zacht + alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("skatepark", "Skatepark", "🛹", "Borden + wind, veilig volume", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true)
    )
}
