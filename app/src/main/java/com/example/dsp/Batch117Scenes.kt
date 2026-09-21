package com.example.dsp

object Batch117Scenes {
    val ALL = listOf(
        ListeningScene("traminstap", "Traminstap", "🚊", "Druk instappen, bel + deuren", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("huisartshal", "Huisartshal", "🏥", "Wachtkamer-fluister, namen hoorbaar", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("wasplaats", "Wasplaats", "🧺", "Centrifuge-brom, kort klusje", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("koffietent", "Koffietent", "☕", "Espressomachine + praat, alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("parkeerlift", "Parkeerlift", "🅿️", "Beton-echo + piep, kort", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("nachtfiets", "Nachtfiets", "🌙", "Donker fietspad, wind + verkeer", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("burenfeest", "Burenfeest", "🎉", "Muziek door de muur, max ANC", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true)
    )
}
