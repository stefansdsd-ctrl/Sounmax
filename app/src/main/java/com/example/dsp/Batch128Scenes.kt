package com.example.dsp

object Batch128Scenes {
    val ALL = listOf(
        ListeningScene("maxancnu", "Max ANC nu", "🔇", "Eén tik: sterkste ANC", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("gespreknu", "Gesprek nu", "🗣️", "Eén tik: volledige transparantie", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("windfietsplus", "Wind+fiets", "🌬️", "Windfilter + alert buiten", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("stilconcert", "Stil concert", "🎻", "Zacht + ruimtelijk, veilig", "Classical & Live Concert 3D", AncMode.ADAPTIVE, safeVolume = true)
    )
}
