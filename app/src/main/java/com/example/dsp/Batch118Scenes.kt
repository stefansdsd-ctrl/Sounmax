package com.example.dsp

object Batch118Scenes {
    val ALL = listOf(
        ListeningScene("ovchippoortplus", "OV-poort spits", "🚪", "Piep + menigte bij inchecken", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("apotheekwacht", "Apotheekwacht", "💊", "Stil wachten, naam hoorbaar", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("fietsenstallingplus", "Fietsenkelder", "🚲", "Echo + kettingslot, kort", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("regenperron", "Regenperron", "🌧️", "Luifel-regen + omroep", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("avondcollege", "Avondcollege", "📚", "Collegezaal, stem helder", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("stiltecoupeplus", "Stiltecoupé+", "🤐", "Max rust in de stiltezone", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("thuisfilmlaat", "Late film", "🎬", "Zacht dynamisch, buren-vriendelijk", "Cinematic Wide Soundstage", AncMode.ADAPTIVE, safeVolume = true)
    )
}
