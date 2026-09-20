package com.example.dsp

object Batch107Scenes {
    val ALL = listOf(
        ListeningScene("strandpaviljoen", "Strandpaviljoen", "⛱️", "Wind + terraspraters, alert", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("bouwkeet", "Bouwkeet", "🏗️", "Radio + machines, lichte ANC", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("spoorwegovergang", "Spoorwegovergang", "🚧", "Bellen + wind, alert blijven", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("studentenkamer", "Studentenkamer", "🛏️", "Huisgenoten + focus, veilig", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("bioscoopfoyer", "Bioscoopfoyer", "🎟️", "Hal-echo + praters, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("gemeentebalie", "Gemeentebalie", "🏛️", "Wachtrij + balie, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
