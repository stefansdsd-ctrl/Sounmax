package com.example.dsp

object Batch72Scenes {
    val ALL = listOf(
        ListeningScene("sundayreset", "Zondagreset", "🌿", "Zacht, veilig, geen druk", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("openoffice", "Open kantoor", "🗂️", "Spraak weg, focus aan", "Flat Studio Monitor (0 dB)", AncMode.STRONG, safeVolume = true),
        ListeningScene("rainwalk", "Regenwandeling", "🌂", "Wind + straat, warme mids", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("batterysave", "Accubesparing", "🔋", "330 kbps, lichte ANC", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("morningbrief", "Ochtendbrief", "☀️", "Nieuws-stem, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("winddown", "Afschakelen", "🌙", "Zacht hoog, slaap-klaar", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true)
    )
}
