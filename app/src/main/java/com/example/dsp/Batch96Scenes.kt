package com.example.dsp

object Batch96Scenes {
    val ALL = listOf(
        ListeningScene("fysio", "Fysiotherapie", "🩺", "Therapeut hoorbaar, gym-echo weg", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("bioscoop", "Bioscoop", "🎬", "Zaal-ruis weg, dialoog helder", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("sauna", "Sauna / wellness", "🧖", "Zacht, geen lek, herstel", "Flat Studio Monitor (0 dB)", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("schaatsbaan", "Schaatsbaan", "⛸️", "IJs-machine + wind, volume veilig", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("werkcollege", "Werkcollege", "👩‍🏫", "Docent + groep, lokaal-echo weg", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("speeltuin", "Speeltuin", "🧟", "Kinderen alert, volume veilig", "Podcast Voice", AncMode.AMBIENT, safeVolume = true)
    )
}
