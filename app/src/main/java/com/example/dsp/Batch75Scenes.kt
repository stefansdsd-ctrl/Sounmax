package com.example.dsp

object Batch75Scenes {
    val ALL = listOf(
        ListeningScene("pharmacywait", "Apotheekwacht", "💊", "Fluister, stemmen alert", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("carwashpro", "Wasstraat+", "🚿", "Spuit/roller weg, veilig", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true),
        ListeningScene("homeexam", "Thuis-examen", "📝", "Max focus, min lek", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("nightshop", "Avondwinkel", "🏪", "Alert + zacht volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("boattrip", "Boottocht", "⛵", "Wind + motor filter", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("cafefocus", "Koffiefocus", "☕", "Praat weg, warme mids", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("rainmetro", "Regenmetro", "🌧️", "Tunnel + regen ANC", "Philips TAH6519 Pro ANC", AncMode.STRONG, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true),
        ListeningScene("sundayafter", "Zondagmiddag", "🌿", "Zacht, geen druk", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true)
    )
}
