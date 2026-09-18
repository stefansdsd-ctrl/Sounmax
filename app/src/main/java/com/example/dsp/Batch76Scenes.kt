package com.example.dsp

object Batch76Scenes {
    val ALL = listOf(
        ListeningScene("hybridmeet", "Hybride-meet", "💻", "Stem helder, toetsenbord weg", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("packedtram", "Volle tram", "🚊", "Drukte weg, omroep hoorbaar", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true),
        ListeningScene("lateah", "Late AH", "🛒", "Koeling + intercom alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kidsbed", "Kids naar bed", "🌙", "Fluister + geen lek", "Night Chill & Lo-Fi Relax", AncMode.STRONG, safeVolume = true),
        ListeningScene("garageklus", "Garageklus", "🔧", "Boor/compressor weg", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("rainoffice", "Regen-kantoor", "☔", "Ramen + toetsen filter", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("sundaymarket", "Zondagmarkt", "🫺", "Kraamdrukte, veilig volume", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("quietcoach", "Stiltecoupé+", "🤫", "Max rust in de trein", "Podcast Voice", AncMode.STRONG, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true)
    )
}
