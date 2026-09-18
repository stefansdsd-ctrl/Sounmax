package com.example.dsp

object Batch73Scenes {
    val ALL = listOf(
        ListeningScene("neighborhood", "Buurtlawaai", "🏡️", "Buren/bouw weg, focus aan", "Flat Studio Monitor (0 dB)", AncMode.STRONG, safeVolume = true),
        ListeningScene("cookeve", "Avondkook", "🍲", "Keuken + timer hoorbaar", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("shortgym", "Sportkort", "⏱️", "20 min cardio, winddicht", "Podcast Voice", AncMode.WIND_GUARD, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("stillnight", "Stille nacht", "🌌", "Minimaal lek, zacht hoog", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("inboxzero", "Inbox-sprint", "📥", "45 min mail-focus, ANC max", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("platformwait", "Perronwacht", "🚉", "Omroep hoorbaar, wind weg", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true)
    )
}
