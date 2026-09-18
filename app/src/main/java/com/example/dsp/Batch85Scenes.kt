package com.example.dsp

object Batch85Scenes {
    val ALL = listOf(
        ListeningScene("bakfiets", "Bakfiets", "🚲", "Kind achter, verkeer alert", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("bouwstraat", "Bouwstraat", "🚧", "Boor weg, omroep hoorbaar", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("collegezaal", "Collegezaal", "🎓", "Stem voor, fluister zacht", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("wasdroger", "Was-droger", "🌀", "Brom weg, podcast helder", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true),
        ListeningScene("avondmarkt", "Avondmarkt", "🛍️", "Kraamdruk, stemmen voor", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("stiltecoupé", "Stiltecoupé", "🤫", "Ratel weg, geen omroepboost", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true)
    )
}
