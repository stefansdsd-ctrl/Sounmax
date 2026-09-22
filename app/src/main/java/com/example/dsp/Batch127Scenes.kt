package com.example.dsp

object Batch127Scenes {
    val ALL = listOf(
        ListeningScene("ovchippoortje", "OV-chip poortje", "🎫", "Piep + drukte, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("huisartstelefoon", "Huisarts-telefoon", "📞", "Wachttoon + fluister", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("winkelfietsenkelder", "Winkel-fietsenkelder", "🚲", "Echo + sloten, alert", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("avondapotheek", "Avond-apotheek", "🌙", "Balie + bel, zacht", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("zwembaddouche", "Zwembad-douche", "🚿", "Sproeier + galm", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("kantoorplotter", "Kantoor-plotter", "🖨️", "Zoem + stem, lichte ANC", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("nachtcafetaria", "Nacht-cafetaria", "🍟", "Frituur + muzak, zacht", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("parkeerwachter", "Parkeerwachter", "🅿️", "Buiten + intercom, alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
