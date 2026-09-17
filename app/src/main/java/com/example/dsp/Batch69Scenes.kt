package com.example.dsp

object Batch69Scenes {
    val ALL = listOf(
        ListeningScene("nachtbus", "Nachtbus", "🚌", "Max ANC + omroep, veilig volume", "Podcast Voice", AncMode.STRONG, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("burenfeest", "Burenfeest", "🎉", "Muur-ANC tegen bas, veilig", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("college-echo", "College-echo", "📢", "Stemhelder in galmende zaal", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("lekcheck", "Lekcheck", "🔍", "Flat + laag volume om lekkage te horen", "Flat Studio Monitor (0 dB)", AncMode.OFF, safeVolume = true),
        ListeningScene("storm", "Storm", "🌪", "Max ANC + wind, stabiele stream", "Outdoor Wind Guard", AncMode.STRONG, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("kermis", "Kermis", "🎡", "Crowd + wind, veilig volume", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("partnerslaap", "Partner slaapt", "💑", "Fluister + ambient voor roep", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("langecall", "Lange call", "📞", "Stemhelder, veilig, weinig vermoeidheid", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
