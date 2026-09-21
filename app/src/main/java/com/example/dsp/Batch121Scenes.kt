package com.example.dsp

object Batch121Scenes {
    val ALL = listOf(
        ListeningScene("avondspitsfiets", "Avondspits fiets", "🚲", "Wind + verkeer, alert blijven", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("apotheekwacht", "Apotheekwacht", "💊", "Korte wacht, stemmen zacht", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("wasstraatlang", "Wasstraat lang", "🚿", "Water + rollers, ANC aan", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true),
        ListeningScene("thuisklusavond", "Thuis-klus avond", "🔧", "Boor + radio, gehoor veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("stadsparkbank", "Parkbank", "🌳", "Buitenlucht + stemmen", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("nachtwinkelkort", "Nachtwinkel kort", "🏪", "Bel + koelkast, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("collegezaalplus", "Collegezaal+", "📚", "Docent helder, zaalruis weg", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("regenterras", "Regenterras", "🌧️", "Druppel + gesprek, zacht", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true)
    )
}
