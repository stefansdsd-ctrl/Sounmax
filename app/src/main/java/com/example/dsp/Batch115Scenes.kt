package com.example.dsp

object Batch115Scenes {
    val ALL = listOf(
        ListeningScene("bagageband", "Bagageband", "🧳", "Hal-rommel + omroep, max ANC", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("roltrap", "Roltrap", "🛗", "Stationstrap, wind + stemmen", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("broodjeszaak", "Broodjeszaak", "🥖", "Lunchrij, stemmen, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("garagepoort", "Garagepoort", "🚪", "Echo + motor, korte burst", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("dierenasiel", "Dierenasiel", "🐾", "Geblaf, alert + veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("perronkap", "Perronkap", "🚉", "Luifel-regen + omroep", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kleedhokje", "Kleedhokje", "👕", "Kleine echo, fluister, veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true)
    )
}
