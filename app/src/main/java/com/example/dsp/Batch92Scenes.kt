package com.example.dsp

object Batch92Scenes {
    val ALL = listOf(
        ListeningScene("zelfscan", "Zelfscan", "📱", "Kassa-piep plat, stem alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("stationshal", "Stationshal", "🚉", "Omroep + echo, ANC sterk", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("hornbach", "Hornbach", "🪵", "Hoge hal, zaag + echo weg", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("parkeergarage", "Parkeergarage", "🅿️", "Motor-dreun + piep, alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("thuisbezorgd", "Thuisbezorgd", "🛵", "Deurbel + scooter, stem voor", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ijssalon", "IJssalon", "🍦", "Rij + praat, zacht ambient", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
