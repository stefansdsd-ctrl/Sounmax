package com.example.dsp

object Batch132Scenes {
    val ALL = listOf(
        ListeningScene("apotheekbalie", "Apotheek", "💊", "Fluister + alert + veilig", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("fietsenstallinghal", "Fietsenstalling", "🚲", "Echo + alert in de hal", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("snackbarhal", "Snackbar", "🍟", "Transparantie + warme stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("postnlpunt", "PostNL-punt", "📦", "Alert in de rij", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("waskelder", "Waskelder", "🧺", "Max ANC tegen droger", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("kantoorwc", "Kantoor-wc", "🚻", "Korte ANC + echo", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("overstaphal", "Overstap-hal", "🔁", "Omroep helder + ANC", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("markthalavond", "Markthal avond", "🌙", "Drukte + warme mids", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE)
    )
}
