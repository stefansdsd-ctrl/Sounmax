package com.example.dsp

object Batch83Scenes {
    val ALL = listOf(
        ListeningScene("tramspits", "Tram-spits", "🚊", "Ratel weg, omroep hoorbaar", "Philips TAH6519 Pro ANC", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("ahspits", "AH-spits", "🛒", "Koeling + omroep, veilig", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("coworkcall", "Cowork-call", "🎧", "Stem-isolatie, lage latency", "Vocal & Acoustic Warmth", AncMode.STRONG, preferredCodec = BluetoothCodec.APTX_ADAPTIVE, safeVolume = true),
        ListeningScene("tvavond", "TV-avond", "📺", "Dialoog voor, bas zacht", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("ebikewind", "E-bike-wind", "🚲", "Wind + verkeer alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("vrijdagavond", "Vrijdag-avond", "✨", "Zacht uitwaaien, veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true)
    )
}
