package com.example.dsp

object Batch88Scenes {
    val ALL = listOf(
        ListeningScene("lidlspits", "Lidl-spits", "🛒", "Karren + scanner, naam bij kassa", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("hemarij", "HEMA-rij", "🥨", "Omroep zacht, stemmen voor", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("mediamarktgang", "MediaMarkt-gang", "📺", "Demo-TV’s weg, stem verkoper", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("fietskelder", "Fietskelder", "🚲", "Echo + sloten, deurbel alert", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("bushaltekou", "Bushalte-kou", "🚌", "Wind weg, busnummer hoorbaar", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("coolbluepickup", "Coolblue-ophalen", "📦", "Hal-omroep + wachtmuziek plat", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
