package com.example.dsp

object Batch82Scenes {
    val ALL = listOf(
        ListeningScene("hotdesk", "Hotdesk", "🪑", "Open kantoor, stemmen weg, veilig", "Vocal & Acoustic Warmth", AncMode.STRONG, safeVolume = true),
        ListeningScene("latebus", "Nachtbus", "🚌", "Wind + motor + alert bij halte", "Philips TAH6519 Pro ANC", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("packmove", "Inpakken", "📦", "Lage latency, voetstappen hoorbaar", "Rock & Metal Punch", AncMode.AMBIENT, preferredCodec = BluetoothCodec.APTX_ADAPTIVE, safeVolume = true),
        ListeningScene("audiobookplus", "Luisterboek+", "📖", "Stem-focus, zacht hoog, veilig", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("rainplatform", "Perron-regen", "🚉", "Regen + omroep + wind", "Philips TAH6519 Pro ANC", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("mondaystart", "Maandag-start", "☕", "Zacht focus, geen pieken", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true)
    )
}
