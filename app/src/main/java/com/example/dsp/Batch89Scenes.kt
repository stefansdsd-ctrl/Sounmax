package com.example.dsp

object Batch89Scenes {
    val ALL = listOf(
        ListeningScene("actionhal", "Action-hal", "🧴", "Piep-muziek plat, kassa-naam alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kruidvatrij", "Kruidvat-rij", "💊", "Scanner + omroep, stem voor", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ahtogo", "AH to go", "🥗", "Korte stop, barcode + stem", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("prparkeren", "P+R parkeren", "🅿️", "Wind + auto’s weg, busalert", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("wasstraat", "Wasstraat", "🚿", "Sproeiers ANC, radio zacht", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("gemeenteloket", "Gemeente-loket", "🏛️", "Wachtnummers helder, fluister", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
