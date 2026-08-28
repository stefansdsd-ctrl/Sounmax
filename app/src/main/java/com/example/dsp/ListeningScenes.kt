package com.example.dsp

object ListeningScenes {
    val ALL = listOf(
        ListeningScene("commute", "Pendelen", "🚆", "Maximale ANC + heldere stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("sport", "Sport", "🏃", "Punchy bas, windfilter", "Hip-Hop & Urban R&B", AncMode.WIND_GUARD),
        ListeningScene("gym", "Gym", "💪", "Strakke kick + ANC", "Electronic & Festival EDM", AncMode.STRONG),
        ListeningScene("focus", "Focus", "🎧", "Vlak + zachte loudness", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE),
        ListeningScene("night", "Nacht", "🌙", "Zacht hoog, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("film", "Film", "🎬", "Breed stereo, diepe bas", "Classical & Live Concert 3D", AncMode.STRONG),
        ListeningScene("game", "Game", "🎮", "Lage latency + spatial", "Rock & Metal Punch", AncMode.ADAPTIVE),
        ListeningScene("podcast", "Podcast", "🎙️", "Stemhelderheid", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("call", "Omgeving", "🗣️", "Transparantie / stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT)
    )

    fun suggestedForHour(hour: Int): ListeningScene {
        return when (hour) {
            in 6..8 -> ALL.first { it.id == "commute" }
            in 9..16 -> ALL.first { it.id == "focus" }
            in 17..19 -> ALL.first { it.id == "commute" }
            in 20..21 -> ALL.first { it.id == "film" }
            else -> ALL.first { it.id == "night" }
        }
    }
}
