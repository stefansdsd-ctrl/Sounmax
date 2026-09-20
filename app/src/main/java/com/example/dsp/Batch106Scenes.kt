package com.example.dsp

object Batch106Scenes {
    val ALL = listOf(
        ListeningScene("foodcourt", "Foodcourt", "🍱", "Hall-echo + praters, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("yogales", "Yogales", "🧘", "Zacht + adem, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("makerspace", "Makerspace", "🛠️", "Machines + praat, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("polderweg", "Polderweg", "🌾", "Wind + auto's, alert blijven", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("garagebox", "Garagebox", "🔧", "Tegel-echo + gereedschap", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("buurtsuper", "Buurtsuper", "🛒", "Kassa + gangpad, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
