package com.example.dsp

object Batch111Scenes {
    val ALL = listOf(
        ListeningScene("thuisbioscoop", "Thuisbioscoop", "🎥", "Donker + LFE thuis, veilig", "Bass Boost Club", AncMode.ANC, safeVolume = true),
        ListeningScene("festivalcamping", "Festivalcamping", "🏕", "Tenten + bas op afstand", "Electronic & Festival EDM", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("tramhaltewacht", "Tramhalte", "🚋", "Wachten, alert + ANC-piep", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE),
        ListeningScene("coworklounge", "Cowork-lounge", "💼", "Praat op achtergrond, focus", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("kringloophal", "Kringloophal", "🛒", "Grote hal, echo + praters", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("nachtservicebalie", "Nachtservice", "🌃", "Balie laat, fluister + alert", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("boswandel", "Boswandel", "🌲", "Buiten, wind + natuur", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true)
    )
}
