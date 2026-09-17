package com.example.dsp

object Batch71Scenes {
    val ALL = listOf(
        ListeningScene("microbreak", "Oorpauze", "🧘", "2 min rust, volume zacht", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("longhaul", "Lange rit", "🛤", "Comfort-EQ, accu-vriendelijk LDAC", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true, preferredLdac = LdacQualityMode.CONNECTION_330),
        ListeningScene("voiceisolate", "Stem-isolatie", "🎙", "Spraak vooruit, ruis weg", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("duskride", "Avondrit", "🌆", "Verkeer hoorbaar, geen vermoeidheid", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("latefps", "Late game", "🎮", "Stappen + voicechat, buren-vriendelijk", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("softcall", "Zachte call", "📞", "Heldere stem, laag lek", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
