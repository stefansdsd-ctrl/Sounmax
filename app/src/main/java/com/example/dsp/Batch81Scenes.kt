package com.example.dsp

object Batch81Scenes {
    val ALL = listOf(
        ListeningScene("traffichold", "File", "🚗", "Lage latency + wegruis weg", "Rock & Metal Punch", AncMode.WIND_GUARD, preferredCodec = BluetoothCodec.APTX_ADAPTIVE, safeVolume = true),
        ListeningScene("zoomclass", "Online-les", "💻", "Docent-stem max, veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, preferredCodec = BluetoothCodec.APTX_ADAPTIVE, safeVolume = true),
        ListeningScene("clinicwait", "Wachtkamer+", "🏥", "Fluister + alert + veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("batterysaveplus", "Accu-20", "🔋", "LDAC 330 + ANC uit", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("rainbikeplus", "Regen-fiets+", "🚲", "Wind + regen + verkeer-alert", "Philips TAH6519 Pro ANC", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("sundayreset", "Zondag-reset", "🌿", "Zacht, ruimtelijk, veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true)
    )
}
