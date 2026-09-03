package com.example.dsp

object ExtraListeningScenes {
    val ALL = listOf(
        ListeningScene("house", "House", "🏠", "Vier-op-de-vloer + warme bas", "Electronic & Festival EDM", AncMode.ADAPTIVE),
        ListeningScene("techno", "Techno", "🔲", "Droge kick + tight hoog", "Electronic & Festival EDM", AncMode.ADAPTIVE),
        ListeningScene("dubstep", "Dubstep", "🌀", "Zware wobble + sub", "Hip-Hop & Urban R&B", AncMode.ADAPTIVE),
        ListeningScene("ambient", "Ambient", "🌫️", "Zacht, ruimtelijk, veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("saver", "Saver", "🔋", "Lage accu: zacht + LDAC 330", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("radio", "Radio", "📻", "Stem + muziek, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("tv", "TV", "📺", "Dialoog voorop + lichte bas", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("djset", "DJ-set", "🎛️", "Lange mix, punch + glitter", "Electronic & Festival EDM", AncMode.ADAPTIVE),
        ListeningScene("liquid", "Liquid DnB", "💧", "Zachte rolling bass + vocaal", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE)
    )

    val GENRE_IDS = setOf("house", "techno", "dubstep", "ambient", "djset", "liquid")
    val MEDIA_IDS = setOf("radio", "tv", "djset")
    val NIGHT_IDS = setOf("ambient", "saver", "liquid")
}
