package com.example.dsp

object Batch122Scenes {
    val ALL = listOf(
        ListeningScene("tuincentrumkas", "Tuincentrumkas", "🌿", "Vocht + ventilatoren, stemmen zacht", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("nsservicebalie", "NS-servicebalie", "🎫", "Omroep + balie, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kantoorpantry", "Kantoorpantry", "☕", "Magnetron + praat, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("fietsenkeldernacht", "Fietsenkelder nacht", "🚲", "Echo + piep, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ziekenhuisnachthal", "Ziekenhuis nachthal", "🏥", "Fluister + omroep, veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("studentenbar", "Studentenvereniging", "🍻", "Druk + muziek, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("pretparkwacht", "Pretparkwacht", "🎢", "Rij + wind, punch zacht", "Electronic & Festival EDM", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("marktavondsluit", "Markt sluiting", "🛝", "Kramen inpakken, alert buiten", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true)
    )
}
