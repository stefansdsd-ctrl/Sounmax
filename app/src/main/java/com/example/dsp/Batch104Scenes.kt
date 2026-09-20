package com.example.dsp

object Batch104Scenes {
    val ALL = listOf(
        ListeningScene("peuterspeelzaal", "Peuterspeelzaal", "🧸", "Kindergeluid + alert, veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("slagerijplus", "Slagerij", "🥩", "Koeling + toonbank, veilig", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("snelfietspad", "Snelfietspad", "🚲", "Buitenwind + bel, windguard", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("bibliobus", "Bibliobus", "🚌", "Kleine ruimte + praten, veilig", "Flat Studio Monitor (0 dB)", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kantoorflex", "Kantoorflex", "🪑", "Hotdesk + calls, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("keukentafelavond", "Keukentafel-avond", "🍽️", "Praat + pannen, veilig volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
