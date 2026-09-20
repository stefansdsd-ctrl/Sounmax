package com.example.dsp

object Batch108Scenes {
    val ALL = listOf(
        ListeningScene("fietstunnel", "Fietstunnel", "🚲", "Echo + verkeer, alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("dakterras", "Dakterras", "🌇", "Wind + buren, avond", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("nachtwinkel", "Nachtwinkel", "🌙", "Koeling + bel, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("treinwerk", "Treinwerk", "💻", "Focus in de stiltecoupe", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("praktijklokaal", "Praktijklokaal", "🔧", "Machines + instructie", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("parkeergarage", "Parkeergarage", "🅿️", "Echo + auto's, alert", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kantoorkantine", "Kantoorkantine", "🥗", "Praters + borden, lunch", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
