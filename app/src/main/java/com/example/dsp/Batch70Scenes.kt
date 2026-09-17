package com.example.dsp

object Batch70Scenes {
    val ALL = listOf(
        ListeningScene("thuisbios", "Thuisbios", "🎬", "Film-EQ, max ANC, geen buur-lek", "Cinematic Wide Soundstage", AncMode.STRONG, safeVolume = true),
        ListeningScene("openkantoor", "Open kantoor", "🏢", "Praatjes weg, focus, veilig", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("wachtkamer", "Wachtkamer", "🏥", "Zacht, ambient voor oproep", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("afterwork", "Afterwork", "🌇", "Avond-ontspan, mild ANC", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("windfiets", "Windfiets", "🚴", "Windfilter + verkeer hoorbaar", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("nachtwerk", "Nachtwerk", "🌙", "Focus zonder buren te storen", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("speeltuin", "Speeltuin", "🛝", "Kids horen, volume laag", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("pomodoro", "Pomodoro", "🍅", "25 min focus-curve, veilig", "Podcast Voice", AncMode.STRONG, safeVolume = true)
    )
}
