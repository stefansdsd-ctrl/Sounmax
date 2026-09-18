package com.example.dsp

object Batch74Scenes {
    val ALL = listOf(
        ListeningScene("waitingroom", "Wachtkamer", "🩺", "Fluister + alert + veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("dentist", "Tandarts", "🦷", "Zacht hoog, drill weg", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true),
        ListeningScene("diy", "Bouwmarkt", "🔨", "Transparantie + wind/zaag", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("pickupkids", "Ophalen kids", "🚗", "Alert buiten, veilig volume", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("lunchwalk", "Lunchwandeling", "🌞", "Windfilter, warme mids", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("longcall", "Lange call", "📞", "Stem voorop, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("garage", "Garage", "🛠️", "Motor/zaag weg, alert", "Flat Studio Monitor (0 dB)", AncMode.STRONG, safeVolume = true),
        ListeningScene("neighbors", "Burenfeest", "🎉", "Max ANC, focus of slaap", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true)
    )
}
