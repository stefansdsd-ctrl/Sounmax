package com.example.dsp

object Batch126Scenes {
    val ALL = listOf(
        ListeningScene("tramconducteur", "Tram-conducteur", "🚋", "Omroep + piep, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("tankstation", "Tankstation", "⛽", "Buitenpomp + wind, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("wasstraat", "Wasstraat", "🚿", "Sproeiers + galm, zacht", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("postkantoor", "Postkantoor", "✉️", "Balie + wachtrij, veilig", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("sportschoolgang", "Sportschool-gang", "🏋️", "Muzieklek + stem, veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("bushalteavond", "Avond-bushalte", "🚌", "Wind + omroep, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kelderberging", "Kelderberging", "📦", "Droge echo, lichte ANC", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("thuisbezorging", "Thuisbezorging", "🛵", "Deurbel + buiten, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
