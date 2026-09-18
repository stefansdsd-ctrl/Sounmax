package com.example.dsp

object Batch77Scenes {
    val ALL = listOf(
        ListeningScene("deliverydoor", "Thuisbezorgd", "📦", "Deurbel + bezorger alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("schoolyard", "Schoolplein", "🎒", "Kids hoorbaar, drukte weg", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("hospwait", "Ziekenhuiswacht", "🏥", "Fluister, naam-oproep", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("garageecho", "Parkeergarage+", "P", "Echo + banden weg", "Philips TAH6519 Pro ANC", AncMode.STRONG, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true),
        ListeningScene("morningbike", "Fietsochtend", "🚲", "Wind + verkeer alert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("nighttram", "Avondtram", "🌙", "Ratel weg, halt hoorbaar", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true),
        ListeningScene("libsilence", "Bieb-stilte+", "📚", "Max focus, min lek", "Podcast Voice", AncMode.STRONG, safeVolume = true),
        ListeningScene("trackwork", "Spoorwerk", "🛠", "Boor/slijper weg", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true)
    )
}
