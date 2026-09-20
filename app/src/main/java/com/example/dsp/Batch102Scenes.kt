package com.example.dsp

object Batch102Scenes {
    val ALL = listOf(
        ListeningScene("bso", "BSO", "🎒", "Kids + echo, alert + veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("sportkleedkamer", "Sportkleedkamer", "👕", "Hall-echo + kasten, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("schouwburg", "Schouwburg", "🎭", "Foyer-ruis, fluister + veilig", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("fietsenstalling", "Fietsenstalling", "🅿️", "Hall + bel, alert blijven", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("stadspark", "Stadspark", "🌳", "Wind + verkeer, veilig volume", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kantoorpantry", "Kantoorpantry", "☕", "Magnetron + praat, lichte ANC", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true)
    )
}
