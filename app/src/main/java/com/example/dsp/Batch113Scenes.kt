package com.example.dsp

object Batch113Scenes {
    val ALL = listOf(
        ListeningScene("fietskoerier", "Fietskoerier", "📦", "Wind + verkeer, alert, veilig", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("regenoverkapping", "Regenoverkapping", "☔", "Perron-luifel, omroep + drup", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("thuiswassen", "Thuiswassen", "👕", "Vouwen + zacht zoem", "Night Chill & Lo-Fi Relax", AncMode.ANC, safeVolume = true),
        ListeningScene("nachtportier", "Nachtportier", "🛎️", "Stil + alert op bel", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("collegeopname", "College-opname", "📼", "Spraakmax, herhaalcollege", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("polderbus", "Polderbus", "🚌", "Motor + wind, lange rit", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("oorpauze", "Oorpauze", "👂", "Sessie-reset, fluister, veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true)
    )
}
