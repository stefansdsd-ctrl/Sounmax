package com.example.dsp

object Batch131Scenes {
    val ALL = listOf(
        ListeningScene("wachtkamerhuisarts", "Wachtkamer huisarts", "🏥", "Fluister + alert + veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("zwembadhal", "Zwembadhal", "🏊", "ANC tegen echo + splash", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("terrasavond", "Terrasavond", "🍷", "Windfilter + warme stemmen", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD),
        ListeningScene("ikeahal", "IKEA-hal", "🛋️", "Alert in de drukte", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("schoolplein", "Schoolplein", "🎒", "Alert + veilig volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("liftschacht", "Lift", "🛗", "Korte ANC tegen brom", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("parkeergarage", "Parkeergarage", "🅿️", "Max ANC + echo", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("kapperstoel", "Kapper", "✂️", "Transparantie + veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true)
    )
}
