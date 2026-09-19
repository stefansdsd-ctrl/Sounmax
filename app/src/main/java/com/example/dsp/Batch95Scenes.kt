package com.example.dsp

object Batch95Scenes {
    val ALL = listOf(
        ListeningScene("rijles", "Rijles", "🚗", "Instructeur hoorbaar, wind/motor weg", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("ziekenhuiswacht", "Ziekenhuiswacht", "🏥", "Omroep + naam, zacht volume", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("practicumlab", "Practicum / lab", "🧪", "Ventilatie weg, stem docent", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("cbrtheorie", "CBR-theorie", "📋", "Max stilte, geen lek", "Flat Studio Monitor (0 dB)", AncMode.STRONG, safeVolume = true),
        ListeningScene("sportdag", "Sportdag", "🏅", "Fluit + veld, volume veilig", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("douchepodcast", "Douche-podcast", "🚿", "Waterdreun weg, stem helder", "Podcast Voice", AncMode.STRONG, safeVolume = true)
    )
}
