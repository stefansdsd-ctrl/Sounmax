package com.example.dsp

object Batch78Scenes {
    val ALL = listOf(
        ListeningScene("themepark", "Pretpark", "🎢", "Rit-pieken weg, kids alert", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("poolreverb", "Zwembadgalm", "🏊", "Echo + fluiten filter", "Podcast Voice", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("fairground", "Kermis", "🎡", "Drukte weg, veilig volume", "Outdoor Wind Guard", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("gardencenter", "Tuincentrum", "🪴", "Fontein + intercom alert", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("diyhall", "Bouwmarkt", "🪵", "Zaag + magazijnfilter", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("thriftshop", "Kringloop", "♻️", "Rommeldrukte, stemmen helder", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("nightbusplus", "Nachtbus+", "🚌", "Ratel weg, halt hoorbaar", "Philips TAH6519 Pro ANC", AncMode.ADAPTIVE, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true),
        ListeningScene("transferhub", "Overstapstation", "🔀", "Omroep + hallo-alert", "Outdoor Wind Guard", AncMode.AMBIENT, preferredLdac = LdacQualityMode.CONNECTION_330, safeVolume = true)
    )
}
