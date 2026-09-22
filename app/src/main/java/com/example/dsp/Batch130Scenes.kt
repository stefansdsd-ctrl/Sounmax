package com.example.dsp

object Batch130Scenes {
    val ALL = listOf(
        ListeningScene("veiligluister", "Veilig luisteren", "🛡️", "Cap + zacht + lichte ANC", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("werkcallruis", "Werkcall ruis", "💼", "Stem voorop, kantoorruis weg", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("ovspitstrap", "OV-spits trap", "🪜", "Alert op stappen + omroep", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("regenoversteek", "Regen-oversteek", "🚦", "Wind + verkeer, alert blijven", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("nachtthuiswerk", "Nacht thuiswerk", "🌙", "Focus zonder oorpijn", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("wasstraatbinnen", "Wasstraat", "🫧", "Max ANC tegen rollers", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("kinderfeestzaal", "Kinderfeest", "🎈", "Alert + veilig volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("bouwsteiger", "Bouwsteiger", "🏗️", "Max ANC tegen hamers", "Philips TAH6519 Pro ANC", AncMode.STRONG)
    )
}
