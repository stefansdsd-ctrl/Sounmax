package com.example.dsp

object Batch93Scenes {
    val ALL = listOf(
        ListeningScene("apotheek", "Apotheek", "💊", "Wachtnummer + fluister, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kinderopvang", "Kinderopvang", "🧸", "Kids + deurbel, volume veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("fitnesslocker", "Fitness-locker", "🔐", "Tegel-echo + föhn plat", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("stormfiets", "Stormfiets", "🌬️", "Harde wind + regen, alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("slagerij", "Slagerij", "🥩", "Zaag + toonbank, stem voor", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("concertfoyer", "Concertfoyer", "🎫", "Praatdruk, zaal-omroep helder", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
