package com.example.dsp

object Batch101Scenes {
    val ALL = listOf(
        ListeningScene("milieustraat", "Milieustraat", "♻️", "Vracht + containers, veilig volume", "Outdoor Wind Guard", AncMode.STRONG, safeVolume = true),
        ListeningScene("schoolkantine", "Schoolkantine", "🍽️", "Echo + stemmen, veilig volume", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("zwembadtribune", "Zwembadtribune", "🏊", "Hall-echo + fluit, alert blijven", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("studiezaal", "Studiezaal", "📗", "Stilte + focus, veilig", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("buurthuis", "Buurthuis", "🏘️", "Praat + koffie, lichte ANC", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kappersstoel", "Kappersstoel", "✂️", "Föhn + praat, alert + veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
