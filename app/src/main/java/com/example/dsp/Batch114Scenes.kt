package com.example.dsp

object Batch114Scenes {
    val ALL = listOf(
        ListeningScene("glasbak", "Glasbak", "🍾", "Buiten, glas + wind, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("treinbistro", "Treinbistro", "🥪", "NS-bistro, omroep + rammel", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("koelcel", "Koelcel", "❄️", "Magazijn-hum, max ANC", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("schoolfiets", "Schoolfiets", "🎒", "Ochtendrit, wind + verkeer", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("koffiecorner", "Koffiecorner", "☕", "Kantoorautomaat, stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("nachtparkeer", "Nachtparkeer", "🅿️", "Leeg dek, echo + alert", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("zwembadgang", "Zwembadgang", "🏊", "Natte echo, omroep, veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true)
    )
}
