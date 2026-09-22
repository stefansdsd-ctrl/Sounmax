package com.example.dsp

object Batch129Scenes {
    val ALL = listOf(
        ListeningScene("regenspitsfiets", "Regen-spits fiets", "🌧️", "Wind + banden, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("nsstoringperron", "NS-storing perron", " Delayed", "Omroep + drukte, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("videobellenbuiten", "Videobellen buiten", "📹", "Stem helder, wind weg", "Podcast Voice", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("babyinslaap", "Baby in slaap", "🍼", "Zacht + veilig volume", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("kookwekker", "Kookwekker keuken", "⏱️", "Alert op piep, zacht muziek", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("burenboor", "Buren-boor", "🔩", "Max ANC tegen boor", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("picknickwind", "Picknick-wind", "🧺", "Buiten + windfilter", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("autowegfile", "Autoweg-file", "🚗", "Motor + claxon, lichte ANC", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true)
    )
}
