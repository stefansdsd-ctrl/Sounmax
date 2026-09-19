package com.example.dsp

object Batch98Scenes {
    val ALL = listOf(
        ListeningScene("tandartsstoel", "Tandarts (behandeling)", "🦷", "Boor-ruis weg, stem arts hoorbaar", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("pakketpunt", "Pakketpunt", "📦", "Hal-echo + wachtrij, alert blijven", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("barbecue", "BBQ / tuinfeest", "🔥", "Stemmen helder, wind zacht", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kantoortuin", "Kantoortuin", "🪴", "Toetsenborden weg, focus", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("uitvaart", "Uitvaart / herdenking", "🕯️", "Zacht, ruimtelijk, veilig volume", "Classical & Live Concert 3D", AncMode.OFF, safeVolume = true),
        ListeningScene("apkkeuring", "APK / garage", "🔧", "Compressor + hal, podcast helder", "Podcast Voice", AncMode.STRONG, safeVolume = true)
    )
}
