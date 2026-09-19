package com.example.dsp

object Batch91Scenes {
    val ALL = listOf(
        ListeningScene("ovchippoort", "OV-poortjes", "🚏", "Piep + omroep, kort alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("snackbar", "Snackbar", "🍟", "Afzuig + radio plat, stem voor", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("woonboulevard", "Woonboulevard", "🛒", "Hoge hal, echo dempen", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("mcdrive", "McDrive", "🍔", "Ruit open, motor + intercom", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("icdirect", "Intercity Direct", "🚄", "Lange rit, rail-dreun weg", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("picnicbezorg", "Picnic-bezorg", "🥝", "Deurbel + koelbox, stem alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
