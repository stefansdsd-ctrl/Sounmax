package com.example.dsp

object Batch119Scenes {
    val ALL = listOf(
        ListeningScene("spoorwerk", "NS-werkzaamheden", "🛠️", "Ploeg + omroep, stemmen helder", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("biebbalie", "Bieb-balie", "📚", "Fluister + piep, naam hoorbaar", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("zelfscanavond", "Zelfscan avond", "🛒", "Beepers + koeling, kort", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("thuiscallplus", "Thuis-call", "🏠", "Stem voorop, buren-vriendelijk", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("groupfit", "Groupfit", "💪", "Beat + instructeur, windfilter uit", "Electronic & Festival EDM", AncMode.ADAPTIVE),
        ListeningScene("avondmarkt", "Avondmarkt", "🍴", "Kramen + stemmen, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("huisvisite", "Huisvisite", "🏥", "Zacht, deurbel + stem", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
