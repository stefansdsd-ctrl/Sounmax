package com.example.dsp

object Batch110Scenes {
    val ALL = listOf(
        ListeningScene("theaterzaal", "Theaterzaal", "🎭", "Fluister + donker, alert fluister", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kerkzaal", "Kerkzaal", "⛪", "Galm + orgel, zacht", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("bioscoopzaal", "Bioscoopzaal", "🎬", "Donker + LFE, veilig", "Bass Boost Club", AncMode.ANC, safeVolume = true),
        ListeningScene("skatepark", "Skatepark", "🛹", "Beton + wind, alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("picknickpark", "Picknickpark", "🗑", "Buitenpraat + wind", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("callbooth", "Belcel", "📞", "Stem max, geen galm", "Vocal & Acoustic Warmth", AncMode.ANC, safeVolume = true),
        ListeningScene("nachttreinwerk", "Nachttreinwerk", "🚆", "Focus in stille coupe", "Flat Studio Monitor (0 dB)", AncMode.ANC, safeVolume = true)
    )
}
