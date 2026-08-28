package com.example.dsp

object ListeningScenes {
    val ALL = listOf(
        ListeningScene("commute", "Pendelen", "🚆", "Maximale ANC + heldere stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("sport", "Sport", "🏃", "Punchy bas, windfilter", "Hip-Hop & Urban R&B", AncMode.WIND_GUARD),
        ListeningScene("focus", "Focus", "🎧", "Vlak + zachte loudness", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE),
        ListeningScene("night", "Nacht", "🌙", "Zacht hoog, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("film", "Film", "🎬", "Breed stereo, diepe bas", "Classical & Live Concert 3D", AncMode.STRONG),
        ListeningScene("call", "Omgeving", "🗣️", "Transparantie / stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT)
    )
}
