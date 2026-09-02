package com.example.dsp

import java.util.Calendar

object ListeningScenes {
    val ALL = listOf(
        ListeningScene("commute", "Pendelen", "🚃", "Maximale ANC + heldere stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("sport", "Sport", "🏃", "Punchy bas, windfilter", "Hip-Hop & Urban R&B", AncMode.WIND_GUARD),
        ListeningScene("gym", "Gym", "💪", "Strakke kick + ANC", "Electronic & Festival EDM", AncMode.STRONG),
        ListeningScene("focus", "Focus", "🎧", "Vlak + zachte loudness", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE),
        ListeningScene("deepwork", "Deep work", "🧠", "Lange focus, vlak + veilig", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("hiit", "HIIT", "🔥", "Max punch + windfilter", "Electronic & Festival EDM", AncMode.WIND_GUARD),
        ListeningScene("livesport", "Live sport", "🏟️", "Commentaar helder + ANC", "Vocal & Acoustic Warmth", AncMode.STRONG),
        ListeningScene("office", "Kantoor", "🏢", "Spraakhelder, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("night", "Nacht", "🌃", "Zacht hoog, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("film", "Film", "🎬", "Breed stereo, diepe bas", "Classical & Live Concert 3D", AncMode.STRONG),
        ListeningScene("game", "Game", "🎮", "Lage latency + spatial", "Rock & Metal Punch", AncMode.ADAPTIVE, preferredCodec = BluetoothCodec.APTX_ADAPTIVE),
        ListeningScene("fps", "FPS", "🎯", "Footsteps + lage latency", "Rock & Metal Punch", AncMode.AMBIENT, preferredCodec = BluetoothCodec.APTX_ADAPTIVE),
        ListeningScene("voicechat", "Voicechat", "🎤", "Stemmen helder, game zachter", "Vocal & Acoustic Warmth", AncMode.AMBIENT, preferredCodec = BluetoothCodec.APTX_ADAPTIVE),
        ListeningScene("latework", "Avondwerk", "💡", "Focus + veilig volume", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("podcast", "Podcast", "🎙️", "Stemhelderheid", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("audiobook", "Luisterboek", "📖", "Warme stem, minder bas", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("news", "Nieuws", "📰", "Spraak-EQ, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("call", "Omgeving", "🗣️", "Transparantie / stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("plane", "Vliegtuig", "✈️", "Max ANC + veilige loudness", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("train", "Trein", "🚃", "ANC tegen rails + stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("bus", "Bus", "🚌", "ANC tegen motor + stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("tram", "Tram", "🚊", "ANC tegen piep + stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("metro", "Metro", "🚇", "Max ANC in tunnel", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("rain", "Regen", "🌧️", "Warme mids, ANC tegen straat", "Night Chill & Lo-Fi Relax", AncMode.STRONG),
        ListeningScene("study", "Studie", "📚", "Stem + detail, geen zware bas", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("school", "School", "🎓", "Focus + veilig volume", "Flat Studio Monitor (0 dB)", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("cook", "Koken", "🍳", "Transparantie in de keuken", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("kids", "Kids", "🧒", "Alert + veilig volume", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("party", "Party", "🎉", "Max punch + loudness", "Hip-Hop & Urban R&B", AncMode.OFF),
        ListeningScene("walk", "Wandelen", "🚶", "Transparantie + windfilter", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD),
        ListeningScene("bike", "Fietsen", "🚲", "Windfilter + alertheid", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD),
        ListeningScene("library", "Bibliotheek", "📖", "Fluister-EQ, veilig volume", "Night Chill & Lo-Fi Relax", AncMode.ADAPTIVE, safeVolume = true),
        ListeningScene("meeting", "Vergadering", "💼", "Max transparantie, stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("wfh", "Thuiswerk", "🏠", "Heldere calls + lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("car", "Auto", "🚗", "Lage latency, wind/wegruis", "Rock & Metal Punch", AncMode.WIND_GUARD, preferredCodec = BluetoothCodec.APTX_ADAPTIVE),
        ListeningScene("sleep", "Inslapen", "💤", "Zacht + timer-vriendelijk", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("rest", "Rust", "♻️", "Pauze: zacht + veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("concert", "Concert", "🎤", "Live-ruimte + punch", "Classical & Live Concert 3D", AncMode.ADAPTIVE),
        ListeningScene("asmr", "ASMR", "🫧", "Zacht, detail, veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("cafe", "Café", "☕", "Warme mids, lichte ANC", "Vocal & Acoustic Warmth", AncMode.ADAPTIVE),
        ListeningScene("shop", "Winkel", "🛍️", "Transparantie in de winkel", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("hike", "Hiken", "🧗", "Windfilter + alert buiten", "Vocal & Acoustic Warmth", AncMode.WIND_GUARD),
        ListeningScene("beach", "Strand", "🏖️", "Windfilter + warme mids", "Night Chill & Lo-Fi Relax", AncMode.WIND_GUARD),
        ListeningScene("hospital", "Ziekenhuis", "🏥", "Fluister + alert + veilig", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("station", "Station", "🚉", "ANC tegen hal, stemmen", "Philips TAH6519 Pro ANC", AncMode.STRONG),
        ListeningScene("church", "Kerk", "⛪", "Fluister, ruimtelijk, veilig", "Classical & Live Concert 3D", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("shower", "Douche", "🚿", "Punchy bas, ANC tegen water", "Hip-Hop & Urban R&B", AncMode.STRONG),
        ListeningScene("vinyl", "Vinyl", "💿", "Warm, analoge mids", "Vocal & Acoustic Warmth", AncMode.OFF),
        ListeningScene("jazz", "Jazzclub", "🍾", "Live-ruimte, zachte ANC", "Classical & Live Concert 3D", AncMode.ADAPTIVE),
        ListeningScene("museum", "Museum", "🏛️", "Fluister + alert", "Night Chill & Lo-Fi Relax", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("oneear", "Eén oor", "👂", "Mono + veilig, één cup", "Vocal & Acoustic Warmth", AncMode.AMBIENT, safeVolume = true),
        ListeningScene("meditate", "Meditatie", "🧘", "Zacht, ruimtelijk, veilig", "Night Chill & Lo-Fi Relax", AncMode.OFF, safeVolume = true),
        ListeningScene("restaurant", "Restaurant", "🍽️", "Transparantie + warme stemmen", "Vocal & Acoustic Warmth", AncMode.AMBIENT),
        ListeningScene("airport", "Luchthaven", "🛫", "Max ANC tegen hallen", "Philips TAH6519 Pro ANC", AncMode.STRONG, safeVolume = true),
        ListeningScene("cinema", "Bioscoop", "🍿", "Breed stereo, diepe bas", "Classical & Live Concert 3D", AncMode.STRONG)
    )

    fun byId(id: String?): ListeningScene? = ALL.firstOrNull { it.id == id }

    fun suggestedNow(weekDoseMinutes: Int = 0): ListeningScene {
        val cal = Calendar.getInstance()
        val weekend = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        val friday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
        return suggestedForHour(cal.get(Calendar.HOUR_OF_DAY), weekend, friday, weekDoseMinutes)
    }

    fun suggestedForHour(
        hour: Int,
        weekend: Boolean = false,
        friday: Boolean = false,
        weekDoseMinutes: Int = 0
    ): ListeningScene {
        if (weekDoseMinutes >= 600 && hour in 18..23) {
            return ALL.first { it.id == "rest" }
        }
        if (weekend) {
            return when (hour) {
                in 0..8 -> ALL.first { it.id == "sleep" }
                in 9..11 -> ALL.first { it.id == "cafe" }
                in 12..16 -> ALL.first { it.id == "beach" }
                in 17..20 -> ALL.first { it.id == "party" }
                in 21..22 -> ALL.first { it.id == "meditate" }
                else -> ALL.first { it.id == "night" }
            }
        }
        if (friday && hour in 19..22) return ALL.first { it.id == "party" }
        return when (hour) {
            in 6..8 -> ALL.first { it.id == "commute" }
            in 9..11 -> ALL.first { it.id == "focus" }
            in 12..13 -> ALL.first { it.id == "office" }
            in 14..16 -> ALL.first { it.id == "school" }
            in 17..19 -> ALL.first { it.id == "commute" }
            in 20..21 -> ALL.first { it.id == "film" }
            in 22..23 -> ALL.first { it.id == "latework" }
            else -> ALL.first { it.id == "night" }
        }
    }

    fun fromNowPlaying(packageName: String, genre: String, title: String): ListeningScene? {
        val p = packageName.lowercase()
        val blob = "$genre $title".lowercase()
        val id = when {
            p.contains("whatsapp") || p.contains("telegram") || p.contains("signal") -> "call"
            p.contains("libby") || p.contains("overdrive") || p.contains("kindle") -> "audiobook"
            p.contains("hbo") || p.contains("wbd") -> "film"
            p.contains("tiktok") || p.contains("instagram") || p.contains("snapchat") -> "party"
            p.contains("twitch") -> "game"
            p.contains("spotify") || p.contains("youtubemusic") || p.contains("youtube.music") ->
                return fromMusicBlob(blob) ?: byId("party")
            p.contains("plex") || p.contains("kodi") -> "film"
            p.contains("calm") || p.contains("headspace") || p.contains("insighttimer") -> "meditate"
            p.contains("discord") || p.contains("teams") || p.contains("zoom") ||
                p.contains("meet") || p.contains("slack") -> "voicechat"
            p.contains("dialer") || p.contains("telecom") || p.contains("incallui") -> "call"
            p.contains("podcast") || p.contains("pocketcasts") || p.contains("overcast") ||
                p.contains("antenna") -> "podcast"
            p.contains("audible") || p.contains("storytel") || p.contains("scribd") -> "audiobook"
            p.contains("tidal") || p.contains("deezer") || p.contains("soundcloud") ||
                p.contains("amazon.mp3") || p.contains("music.amazon") ||
                p.contains("apple.android.music") -> "party"
            p.contains("netflix") || p.contains("disney") || p.contains("primevideo") ||
                p.contains("videolan") || p.contains("mxplayer") || p.contains("jellyfin") ||
                p.contains("npo") || p.contains("vrt") -> "film"
            p.contains("youtube") && !p.contains("music") -> "film"
            p.contains("pubg") || p.contains("codm") || p.contains("genshin") ||
                p.contains("roblox") || p.contains("fortnite") || p.contains("minecraft") -> "fps"
            blob.contains("luisterboek") || blob.contains("audiobook") || blob.contains("hoorspel") -> "audiobook"
            blob.contains("wedstrijd") || blob.contains("eredivisie") || blob.contains("champions league") || blob.contains("live sport") -> "livesport"
            blob.contains("nieuws") || blob.contains("news bulletin") || blob.contains("journaal") -> "news"
            blob.contains("podcast") || blob.contains("speech") || blob.contains("interview") -> "podcast"
            blob.contains("asmr") -> "asmr"
            blob.contains("hip") || blob.contains("rap") || blob.contains("r&b") -> "sport"
            blob.contains("edm") || blob.contains("electro") || blob.contains("techno") || blob.contains("house") -> "gym"
            blob.contains("classic") || blob.contains("orchestra") -> "focus"
            blob.contains("lofi") || blob.contains("chill") || blob.contains("ambient") -> "night"
            blob.contains("rock") || blob.contains("metal") -> "game"
            blob.contains("pop") -> "party"
            blob.contains("jazz") || blob.contains("blues") -> "jazz"
            blob.contains("vinyl") || blob.contains("analog") -> "vinyl"
            blob.contains("meditat") || blob.contains("yoga") || blob.contains("mindful") -> "meditate"
            else -> return null
        }
        return byId(id)
    }

    private fun fromMusicBlob(blob: String): ListeningScene? {
        val id = when {
            blob.contains("podcast") || blob.contains("speech") || blob.contains("interview") -> "podcast"
            blob.contains("luisterboek") || blob.contains("audiobook") -> "audiobook"
            blob.contains("asmr") -> "asmr"
            blob.contains("lofi") || blob.contains("chill") || blob.contains("ambient") -> "night"
            blob.contains("classic") || blob.contains("orchestra") -> "focus"
            blob.contains("jazz") || blob.contains("blues") -> "jazz"
            blob.contains("hip") || blob.contains("rap") || blob.contains("r&b") -> "sport"
            blob.contains("edm") || blob.contains("electro") || blob.contains("techno") || blob.contains("house") -> "gym"
            blob.contains("rock") || blob.contains("metal") -> "game"
            blob.contains("meditat") || blob.contains("yoga") -> "meditate"
            else -> return null
        }
        return byId(id)
    }
}
