package com.example.dsp

object Batch123Scenes {
    val ALL = listOf(
        ListeningScene("laadpaalwacht", "Laadpaal wacht", "🔌", "Buiten wachten, wind + alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("schoolpoort", "Schoolpoort", "🎓", "Kids + verkeer, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("stationsstalling", "Stationsstalling", "🚲", "Echo + piep, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("nachttaxi", "Nacht-taxi", "🚖", "Lage latency + zacht hoog", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("thuisintercom", "Thuis-intercom", "🔔", "Max transparantie deurbel", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ikeakinderhoek", "IKEA-kinderhoek", "🧸", "Rumoer + alert, veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ziekenhuislift", "Ziekenhuislift", "🛜", "Piep + fluister, veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("parkeerautomaat", "Parkeerautomaat", "🏃", "Hal-echo + stem, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
