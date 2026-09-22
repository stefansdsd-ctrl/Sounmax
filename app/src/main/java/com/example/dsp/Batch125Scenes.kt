package com.example.dsp

object Batch125Scenes {
    val ALL = listOf(
        ListeningScene("ahzelfscanrij", "AH zelfscan-rij", "🛒", "Beep + stem, alert en veilig", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("nsbalkonwacht", "NS-perronbalkon", "🚉", "Wind + omroep, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("apotheekbalie", "Apotheek-balie", "💊", "Fluister + wachtnummer", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("bibliotheekbalie", "Bieb-uitleen", "📚", "Stilte + piep-scanner", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("werkfietsregen", "Werkfiets-regen", "🚲", "Wind + bandenspat, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kantoorprintgang", "Printgang", "🖨️", "Zoem + stem, lichte ANC", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("avondsupermacht", "Avond-supermarkt", "🌙", "Koelkast + muzak, zacht", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("pakketautomaat", "Pakketautomaat", "📬", "Hal-echo + code-alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
