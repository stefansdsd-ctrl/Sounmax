package com.example.dsp

object Batch105Scenes {
    val ALL = listOf(
        ListeningScene("wasserette", "Wasserette", "🧺", "Machines + echo, lichte ANC", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("studentenhuis", "Studentenhuis", "🏠", "Keuken + praat, veilig volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("lunchwandeling", "Lunchwandeling", "🥗", "Buitenlucht + verkeer, alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("stilteruimte", "Stilteruimte", "🤐", "Focus + fluister, veilig", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("trapportaal", "Trapportaal", "🪜", "Tegel-echo + deuren, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("avondbushalte", "Avondbushalte", "🚏", "Straat + bus, alert blijven", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true)
    )
}
