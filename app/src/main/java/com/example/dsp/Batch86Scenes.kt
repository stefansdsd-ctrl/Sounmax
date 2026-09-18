package com.example.dsp

object Batch86Scenes {
    val ALL = listOf(
        ListeningScene("nsoverstap", "NS-overstap", "🔁", "Omroep + perronwind, alert", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("fietstunnel", "Fietstunnel", "🚇", "Echo weg, bel hoorbaar", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("huisartswacht", "Huisarts-wacht", "🩺", "Fluister, naam hoorbaar", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("biebavond", "Bieb-avond", "📖", "Stil, stemmen zacht", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("klusweekend", "Klusweekend", "🛠️", "Boor weg, bel/deur alert", "Outdoor Wind Guard", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("regenbalcon", "Regen-balkon", "🌧️", "Druppelweg, podcast helder", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true)
    )
}
