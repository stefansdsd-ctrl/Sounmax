package com.example.dsp

object ExtraListeningScenes {
    val ALL = listOf(
        ListeningScene("house", "House", "\uD83C\uDFE0", "Vier-op-de-vloer + warme bas", "Electronic & Festival EDM", AncMode.ADAPTIVE),
        ListeningScene("techno", "Techno", "\uD83D\uDD32", "Droge kick + tight hoog", "Electronic & Festival EDM", AncMode.ADAPTIVE),
        ListeningScene("dubstep", "Dubstep", "\uD83C\uDF00", "Zware wobble + sub", "Hip-Hop & Urban R&B", AncMode.ADAPTIVE),
        ListeningScene("ambient", "Ambient", "\uD83C\uDF2B\uFE0F", "Zacht, ruimtelijk, veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("saver", "Saver", "\uD83D\uDD0B", "Lage accu: zacht + LDAC 330", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330)
    )

    val GENRE_IDS = setOf("house", "techno", "dubstep", "ambient", "djset", "liquid")
    val NIGHT_IDS = setOf("ambient", "saver")
}
