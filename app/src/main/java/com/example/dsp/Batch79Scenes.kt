package com.example.dsp

object Batch79Scenes {
    val ALL = listOf(
        ListeningScene("hearrest", "Gehoor-pauze", "🧘", "5 min zacht + ANC uit", "Flat Studio Monitor (0 dB)", AncMode.OFF, safeVolume = true),
        ListeningScene("examhall", "Tentamenhal", "📝", "Max focus, geen bass", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("concertpit", "Concertbak", "🎸", "Limiter + wind, veilig", "Classical & Live Concert 3D", AncMode.OFF, safeVolume = true),
        ListeningScene("openplanplus", "Open kantoor+", "🖥️", "Stemisolatie + lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("refcheck", "Referentie-check", "🎛️", "Vlak, stereo-test", "Flat Studio Monitor (0 dB)", AncMode.OFF),
        ListeningScene("earfatigue", "Oor-moeheid", "😮‍💨", "Warm + volume-cap", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("callclarity", "Bel-helder", "📞", "Stem naar voren, ambient", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("windfietsplus", "Windfiets+", "🌬️", "Sterker windfilter buiten", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true)
    )
}
