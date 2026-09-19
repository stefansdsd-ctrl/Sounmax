package com.example.dsp

object Batch94Scenes {
    val ALL = listOf(
        ListeningScene("bakkerij", "Bakkerij", "🥐", "Oven + rij, stem voor de toonbank", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("dierenarts", "Dierenarts", "🐾", "Wachtkamer-blaf, volume veilig", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("padelbaan", "Padel", "🎾", "Harde knal + echo, ANC sterk", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("klimhal", "Klimhal", "🧗", "Hoge hal, muziek + valmat-dreun", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("bowlingbaan", "Bowling", "🎳", "Kegels + muziek, stem alert", "Outdoor Wind Guard", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("zwemles", "Zwemles", "🏊", "Bad-echo + fluitsignaal, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
