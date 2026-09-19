package com.example.dsp

object Batch87Scenes {
    val ALL = listOf(
        ListeningScene("jumbospits", "Jumbo-spits", "🛒", "Karren + omroep, alert bij kassa", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ikeazondag", "IKEA-zondag", "🛋️", "Hal-echo weg, kind/stem alert", "Outdoor Wind Guard", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("tandartswacht", "Tandarts-wacht", "🦷", "Boor zacht, naam hoorbaar", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("terraswind", "Terras-wind", "🍃", "Wind weg, gesprek voor", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("liftecho", "Lift-echo", "🛗", "Korte echo plat, deurbel alert", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("thuiskidsplus", "Thuis-kids+", "🧸", "Gillen zacht, bel/deur voor", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
