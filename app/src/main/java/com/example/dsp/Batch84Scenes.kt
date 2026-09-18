package com.example.dsp

object Batch84Scenes {
    val ALL = listOf(
        ListeningScene("schoolplein", "Schoolplein", "🏫", "Kinderen hard, omroep zacht", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("keukenbellen", "Keuken-bel", "📞", "Afzuigkap weg, stem voor", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, preferredCodec = BluetoothCodec.APTX_ADAPTIVE, safeVolume = true),
        ListeningScene("regenkantoor", "Regen-kantoor", "🌧️", "Glas-tik dempen, focus", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("slaaptrein", "Slaap-trein", "🚂", "Ratel + omroep, zacht", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("drukkoken", "Druk-koken", "🍳", "Pannen + timer hoorbaar", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("avondwandel", "Avond-wandel", "🌙", "Verkeer alert, wind zacht", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true)
    )
}
