package com.example.dsp

object Batch109Scenes {
    val ALL = listOf(
        ListeningScene("bibliotheekzaal", "Bibliotheekzaal", "📚", "Stilte + fluister, focus", "Flat Studio Monitor (0 dB)", AncMode.ANC, safeVolume = true),
        ListeningScene("zwembad", "Zwembad", "🏊", "Echo + splash, alert", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kapsalon", "Kapsalon", "✂️", "Föhn + praat, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("marktkraam", "Marktkraam", "🧺", "Roeptoeter + wind", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("eetcafe", "Eetcafe", "🍽️", "Borden + gesprek, lunch", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ziekenhuiswacht", "Ziekenhuiswacht", "🏥", "Piepjes + wachten, rust", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("thuiskantoor", "Thuiskantoor", "🏠", "Focus zonder buren", "Flat Studio Monitor (0 dB)", AncMode.ANC, safeVolume = true)
    )
}
