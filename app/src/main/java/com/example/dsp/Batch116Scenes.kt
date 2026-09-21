package com.example.dsp

object Batch116Scenes {
    val ALL = listOf(
        ListeningScene("pakketkluis", "Pakketkluis", "📦", "Hal-echo + piep, alert op code", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("kantinehal", "Kantinehal", "🍽️", "Bestek + praat, lunch-alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("fietspomp", "Fietspomp", "🚲", "Kort buiten, wind + verkeer", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("liftlobby", "Liftlobby", "🛗", "Marmerecho + bel, kort wachten", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("brievenbushal", "Brievenbushal", "📬", "Tegel-echo, kort en alert", "Podcast Voice", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("spitsfiets", "Spitsfiets", "🚴", "Druk fietspad, wind + bel", "Outdoor Wind Guard", AncMode.WIND_GUARD, safeVolume = true),
        ListeningScene("avondreceptie", "Avondreceptie", "🏨", "Stil + bel hoorbaar", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true)
    )
}
