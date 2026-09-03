package com.example.dsp

/** Bronlijst; dezelfde scenes staan ook in ListeningScenes.ALL + GROUPS. */
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
        ListeningScene("liquid", "Liquid DnB", "💧", "Zachte rolling bass + vocaal", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE),
        ListeningScene("lecture", "College", "🧑‍🏫", "Spraakhelder + veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("commute_rain", "Pendelen regen", "☔", "Max ANC + warme mids", "Philips TAH6519 Pro ANC", AncMode.STRONG, preferredLdac = LdacQualityMode.BALANCED_660),
        ListeningScene("morning", "Ochtend", "🌅", "Warme mids, zachte start", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("basscheck", "Bass-check", "🔊", "Sub + kick om laag te testen", "Hip-Hop & Urban R&B", AncMode.OFF),
        ListeningScene("stereotest", "Stereo-test", "↔️", "Links/rechts + breedte", "Classical & Live Concert 3D", AncMode.OFF),
        ListeningScene("reference", "Referentie", "🎚️", "Vlak A/B zonder extra DSP", "Flat Studio Monitor (0 dB)", AncMode.OFF),
        ListeningScene("speaker", "Speaker", "🔈", "Speaker-achtig: minder stereo-split", "Vocal & Acoustic Warmth", AncMode.OFF)
    )

    val GENRE_IDS = setOf("house", "techno", "dubstep", "ambient", "djset", "liquid")
    val MEDIA_IDS = setOf("radio", "tv", "djset", "speaker", "reference")
    val NIGHT_IDS = setOf("ambient", "saver", "liquid")
    val WORK_IDS = setOf("lecture")
    val COMMUTE_IDS = setOf("commute_rain")
    val TOOL_IDS = setOf("basscheck", "stereotest", "reference", "speaker")
    val DAY_IDS = setOf("morning")
}
