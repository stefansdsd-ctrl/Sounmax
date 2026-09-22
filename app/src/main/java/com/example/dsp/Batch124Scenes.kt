package com.example.dsp

object Batch124Scenes {
    val ALL = listOf(
        ListeningScene("bouwmarktgang", "Bouwmarkt-gang", "🛠️", "Hoge plafonds + zaaggeluid, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("huisartsentafel", "Huisartstafel", "🩺", "Fluister + wacht, veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ovchipopladen", "OV-chip opladen", "💳", "Hal-beep + stem, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("nachtwinkelhal", "Nachtwinkel-hal", "🌙", "Koelkastzoem + zacht", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kinderopvanghal", "Kinderopvang-hal", "👶", "Kids-rumoer, veilig volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("fietspuntstalling", "Fietspunt", "🔧", "Pomp + metaal, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("sporthaltribune", "Sporthal-tribune", "🏐", "Galm + fluit, veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("pakketwandel", "Pakket-rondje", "📦", "Buiten lopen + deurbel-alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true)
    )
}
