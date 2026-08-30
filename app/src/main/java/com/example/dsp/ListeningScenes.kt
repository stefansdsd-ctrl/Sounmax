package com.example.dsp

object ListeningScenes {
    val ALL = listOf(
        ListeningScene("commute", "Pendelen", "🚃", "Maximale ANC + heldere stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("sport", "Sport", "🏃", "Punchy bas, windfilter", "Hip-Hop & Urban R&B", AncMode.WIND_GUARD),
        ListeningScene("gym", "Gym", "💪", "Strakke kick + ANC", "Electronic & Festival EDM", AncMode.STRONG),
        ListeningScene("focus", "Focus", "🎧", "Vlak + zachte loudness", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE),
        ListeningScene("office", "Kantoor", "🏢", "Spraakhelder, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("night", "Nacht", "🌙", "Zacht hoog, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("film", "Film", "🎬", "Breed stereo, diepe bas", "Classical & Live Concert 3D", AncMode.STRONG),
        ListeningScene("game", "Game", "🎮", "Lage latency + spatial", "Rock & Metal Punch", AncMode.ADAPTIVE, preferredCodec = BluetoothCodec.APTX_ADAPTIVE),
        ListeningScene("podcast", "Podcast", "🎙️", "Stemhelderheid", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("call", "Omgeving", "🗣️", "Transparantie / stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("plane", "Vliegtuig", "✈️", "Max ANC + veilige loudness", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("train", "Trein", "🚆", "ANC tegen rails + stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("rain", "Regen", "🌧️", "Warme mids, ANC tegen straat", "Night Chill & Lo-Fi Relax", AncMode.STRONG),
        ListeningScene("study", "Studie", "📚", "Stem + detail, geen zware bas", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("cook", "Koken", "🍳", "Transparantie in de keuken", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("kids", "Kids", "🧒", "Alert + veilig volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("party", "Party", "🎉", "Max punch + loudness", "Hip-Hop & Urban R&B", AncMode.OFF),
        ListeningScene("walk", "Wandelen", "🚶", "Transparantie + windfilter", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD),
        ListeningScene("bike", "Fietsen", "🚲", "Windfilter + alertheid", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD),
        ListeningScene("library", "Bibliotheek", "📖", "Fluister-EQ, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("meeting", "Vergadering", "💼", "Max transparantie, stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("wfh", "Thuiswerk", "🏠", "Heldere calls + lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("car", "Auto", "🚗", "Lage latency, wind/wegruis", "Rock & Metal Punch", AncMode.WIND_GUARD, preferredCodec = BluetoothCodec.APTX_ADAPTIVE),
        ListeningScene("sleep", "Inslapen", "😴", "Zacht + timer-vriendelijk", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("rest", "Rust", "♻️", "Pauze: zacht + veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("cafe", "Café", "☕", "Warme mids + lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("concert", "Concert", "🎤", "Live-ruimte + punch", "Classical & Live Concert 3D", AncMode.OFF)
    )

    fun byId(id: String?): ListeningScene? = ALL.firstOrNull { it.id == id }

    fun suggestedForHour(hour: Int): ListeningScene {
        return when (hour) {
            in 6..8 -> ALL.first { it.id == "commute" }
            in 9..11 -> ALL.first { it.id == "focus" }
            in 12..13 -> ALL.first { it.id == "office" }
            in 14..16 -> ALL.first { it.id == "study" }
            in 17..19 -> ALL.first { it.id == "commute" }
            in 20..21 -> ALL.first { it.id == "film" }
            in 22..23 -> ALL.first { it.id == "sleep" }
            else -> ALL.first { it.id == "night" }
        }
    }

    fun suggestedForNow(): ListeningScene {
        val cal = java.util.Calendar.getInstance()
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val weekend = cal.get(java.util.Calendar.DAY_OF_WEEK) in setOf(
            java.util.Calendar.SATURDAY, java.util.Calendar.SUNDAY
        )
        if (!weekend) return suggestedForHour(hour)
        return when (hour) {
            in 8..11 -> ALL.first { it.id == "walk" }
            in 12..16 -> ALL.first { it.id == "cafe" }
            in 17..20 -> ALL.first { it.id == "film" }
            in 21..23 -> ALL.first { it.id == "sleep" }
            else -> ALL.first { it.id == "night" }
        }
    }
}
