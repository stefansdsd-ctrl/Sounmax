package com.example.dsp

object Batch120Scenes {
    val ALL = listOf(
        ListeningScene("herfstwandel", "Herfstwandel", "🍂", "Wind + bladgeritsel, stemmen zacht", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("ovchipstoring", "OV-chip storing", "🎫", "Poort-piep + omroep, kort", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("praktijkles", "Praktijkles", "🛠️", "Machines + instructeur, stem voorop", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("spabeurt", "Spa-beurt", "🫧", "Zacht, vocht + fluister", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("icstilteplus", "IC-stilte+", "🤐", "Max rust, omroep doorlaten", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("wfhkind", "WFH + kind", "👧", "Focus met deurbel + kindstem", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("mallzondag", "Winkelcentrum zo", "🏬", "Echo + muzak, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("collegeavond", "Avondcollege", "🎓", "Docent helder, zaalruis weg", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true)
    )
}
