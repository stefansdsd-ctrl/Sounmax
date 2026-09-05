package com.example.dsp

/** Scene-specifieke adaptive EQ, vóór genre-hints. */
object AdaptiveSceneEq {
    fun hint(blob: String): AdaptiveEqHint? = when {
        has(blob, "festival", "tomorrowland", "lowlands", "pinkpop") ->
            hint("festival", 2.2f, 1.8f, 0.4f, -0.4f, 0.0f, 0.4f, 1.0f, 1.6f, 1.8f, 1.2f, 80, 0.4f)
        has(blob, "veerboot", "ferry", "westerschelde") ->
            hint("veerboot", 1.0f, 0.8f, 0.2f, 0.0f, 0.2f, 0.4f, 0.2f, -0.2f, -0.4f, -0.6f, 20, 0.1f)
        has(blob, "klus", "workshop", "klussen") ->
            hint("klus", -1.0f, -0.6f, 0.2f, 0.8f, 1.4f, 1.6f, 1.2f, 0.6f, 0.0f, -0.6f, -30, 1.0f)
        has(blob, "cowork", "wework", "kantoortuin") ->
            hint("cowork", -0.6f, -0.4f, 0.0f, 0.2f, 0.4f, 0.6f, 0.5f, 0.2f, -0.2f, -0.5f, -10, 0.3f)
        has(blob, "tandarts", "dentist", "dental") ->
            hint("tandarts", -1.6f, -1.2f, -0.4f, 0.2f, 0.6f, 0.8f, 0.4f, 0.0f, -0.4f, -0.8f, -40, 0.2f)
        has(blob, "sauna", "wellness") ->
            hint("sauna", -0.8f, -0.4f, 0.2f, 0.4f, 0.4f, 0.3f, 0.1f, -0.2f, -0.6f, -1.0f, -20, 0.0f)
        has(blob, "stadion", "stadium", "arena") ->
            hint("stadion", -0.4f, 0.0f, 0.3f, 0.6f, 1.0f, 1.4f, 1.6f, 1.2f, 0.4f, -0.2f, -10, 0.9f)
        has(blob, "audiotour", "audio guide", "museumtour", "smartify") ->
            hint("audiotour", -1.8f, -1.4f, -0.6f, 0.4f, 1.2f, 2.0f, 2.2f, 1.4f, 0.4f, -0.6f, -50, 1.2f)
        has(blob, "camping", "kamperen") ->
            hint("camping", 0.3f, 0.2f, 0.2f, 0.3f, 0.4f, 0.5f, 0.4f, 0.1f, -0.3f, -0.6f, 0, 0.2f)
        has(blob, "tentamen", "examen", "toets") ->
            hint("tentamen", -0.4f, -0.2f, 0.0f, 0.2f, 0.3f, 0.4f, 0.3f, 0.1f, -0.2f, -0.5f, -10, 0.2f)
        has(blob, "karaoke", "sing-along", "singalong") ->
            hint("karaoke", -1.2f, -0.8f, 0.0f, 0.6f, 1.4f, 2.0f, 2.2f, 1.6f, 0.8f, 0.2f, -20, 1.1f)
        has(blob, "slaaplied", "white noise baby", "babyfoon") ->
            hint("baby", -2.0f, -1.6f, -0.8f, -0.2f, 0.2f, 0.3f, 0.1f, -0.4f, -1.0f, -1.6f, -80, 0.1f)
        has(blob, "deurbel", "doorbell", "omgeving luisteren") ->
            hint("alert", -1.0f, -0.6f, 0.2f, 0.8f, 1.4f, 1.8f, 1.6f, 1.0f, 0.3f, -0.4f, -40, 1.0f)
        has(blob, "zwembad", "swimming pool", "baan zwem") ->
            hint("zwembad", 0.2f, 0.4f, 0.3f, 0.4f, 0.6f, 0.8f, 0.6f, 0.2f, -0.4f, -0.8f, -10, 0.4f)
        has(blob, "schoolplein", "speeltuin", "playground") ->
            hint("schoolplein", -1.2f, -0.8f, 0.0f, 0.6f, 1.2f, 1.6f, 1.4f, 0.8f, 0.1f, -0.6f, -30, 1.0f)
        has(blob, "fotoshoot", "photoshoot", "lightroom") ->
            hint("fotoshoot", -0.8f, -0.4f, 0.1f, 0.4f, 0.8f, 1.0f, 0.8f, 0.3f, -0.2f, -0.6f, -20, 0.6f)
        has(blob, "callcenter", "klantenservice", "zendesk") ->
            hint("callcenter", -1.4f, -1.0f, -0.2f, 0.6f, 1.4f, 2.0f, 1.8f, 1.0f, 0.2f, -0.4f, -40, 1.2f)
        has(blob, "livestream", "live stream", "kick.com") ->
            hint("livestream", -0.4f, 0.0f, 0.2f, 0.6f, 1.0f, 1.4f, 1.2f, 0.8f, 0.3f, -0.2f, -10, 0.8f)
        has(blob, "e-reader", "ereader", "kindle") ->
            hint("ereader", -1.0f, -0.6f, 0.0f, 0.3f, 0.5f, 0.6f, 0.4f, 0.0f, -0.4f, -0.8f, -20, 0.3f)
        has(blob, "stiltecoupé", "stiltecoupe", "quiet coach") ->
            hint("stiltecoupe", -1.2f, -0.8f, -0.2f, 0.2f, 0.4f, 0.5f, 0.3f, 0.0f, -0.4f, -0.8f, -30, 0.2f)
        has(blob, "park wandel", "stadspark", "vondelpark") ->
            hint("park", 0.2f, 0.3f, 0.3f, 0.4f, 0.5f, 0.6f, 0.4f, 0.1f, -0.3f, -0.6f, 0, 0.3f)
        has(blob, "videobellen", "video call", "facetime", "google meet") ->
            hint("videocall", -1.6f, -1.2f, -0.4f, 0.6f, 1.6f, 2.2f, 2.0f, 1.2f, 0.2f, -0.6f, -40, 1.3f)
        has(blob, "esports", "e-sports", "toernooi game") ->
            hint("esports", -0.6f, -0.2f, 0.2f, 0.6f, 1.0f, 1.4f, 1.8f, 1.6f, 1.0f, 0.2f, -10, 1.0f)
        has(blob, "filmavond", "movie night") ->
            hint("filmavond", 0.6f, 0.8f, 0.4f, 0.2f, 0.4f, 0.8f, 1.0f, 0.8f, 0.2f, -0.2f, 20, 0.4f)
        has(blob, "ski", "snowboard", "wintersport") ->
            hint("ski", 0.4f, 0.6f, 0.3f, 0.4f, 0.6f, 0.8f, 0.6f, 0.2f, -0.2f, -0.6f, 10, 0.4f)
        has(blob, "bioscoop", "cinema", "pathe") ->
            hint("bioscoop", 0.4f, 0.6f, 0.3f, 0.2f, 0.4f, 0.8f, 1.0f, 0.8f, 0.2f, -0.2f, 10, 0.4f)
        has(blob, "terras", "buiten zitten") ->
            hint("terras", -0.4f, -0.2f, 0.2f, 0.6f, 1.0f, 1.2f, 0.8f, 0.3f, -0.2f, -0.6f, -20, 0.8f)
        has(blob, "pretpark", "efteling", "walibi") ->
            hint("pretpark", 0.8f, 1.0f, 0.4f, 0.3f, 0.5f, 0.8f, 1.0f, 0.8f, 0.2f, -0.2f, 20, 0.5f)
        has(blob, "hackathon", "code sprint") ->
            hint("hackathon", -0.4f, -0.2f, 0.0f, 0.2f, 0.3f, 0.4f, 0.3f, 0.1f, -0.2f, -0.5f, -10, 0.2f)
        has(blob, "winkelcentrum", "mall") ->
            hint("mall", -0.8f, -0.4f, 0.2f, 0.6f, 1.0f, 1.2f, 0.8f, 0.3f, -0.2f, -0.6f, -20, 0.8f)
        has(blob, "gvb", "ret ", "htm ") ->
            hint("gvb", 0.4f, 0.3f, 0.1f, 0.2f, 0.6f, 0.8f, 0.4f, 0.0f, -0.4f, -0.6f, 0, 0.3f)
        has(blob, "lo-fi", "lofi", "chillhop") ->
            hint("lofi", 0.4f, 0.6f, 0.4f, 0.2f, 0.2f, 0.1f, 0.0f, -0.2f, -0.4f, -0.6f, 10, 0.1f)
        has(blob, "bibliotheek", "library", "oba ") ->
            hint("bibliotheek", -1.4f, -1.0f, -0.2f, 0.2f, 0.4f, 0.5f, 0.3f, 0.0f, -0.4f, -0.8f, -30, 0.2f)
        has(blob, "concertzaal", "concertgebouw", "de doelen") ->
            hint("concertzaal", 0.2f, 0.4f, 0.3f, 0.2f, 0.4f, 0.8f, 1.2f, 1.0f, 0.4f, -0.2f, 0, 0.5f)
        has(blob, "horeca", "restaurant", "thefork") ->
            hint("horeca", -0.6f, -0.3f, 0.2f, 0.6f, 1.0f, 1.2f, 0.8f, 0.3f, -0.2f, -0.6f, -20, 0.9f)
        has(blob, "ikea") ->
            hint("ikea", -0.6f, -0.3f, 0.2f, 0.5f, 0.9f, 1.1f, 0.7f, 0.2f, -0.2f, -0.5f, -10, 0.8f)
        has(blob, "thuiswerken", "wfh", "home office") ->
            hint("wfh", -0.4f, -0.2f, 0.0f, 0.2f, 0.3f, 0.4f, 0.3f, 0.1f, -0.2f, -0.5f, -10, 0.2f)
        has(blob, "treinwerk", "werken in de trein") ->
            hint("treinwerk", -0.4f, -0.2f, 0.0f, 0.2f, 0.4f, 0.5f, 0.3f, 0.0f, -0.3f, -0.6f, -10, 0.3f)
        has(blob, "drukke ns", "volle trein", "nsdruk") ->
            hint("nsdruk", 0.4f, 0.3f, 0.1f, 0.3f, 0.7f, 0.9f, 0.5f, 0.0f, -0.4f, -0.6f, 0, 0.4f)
        has(blob, "ov-chip", "ovchip", "inchecken") ->
            hint("ovchip", -1.0f, -0.6f, 0.2f, 0.8f, 1.4f, 1.6f, 1.2f, 0.6f, 0.0f, -0.4f, -30, 1.0f)
        has(blob, "swapfiets", "deelfiets") ->
            hint("swapfiets", 0.2f, 0.3f, 0.2f, 0.4f, 0.6f, 0.8f, 0.5f, 0.1f, -0.3f, -0.6f, 0, 0.5f)
        has(blob, "collegezaal", "hoorcollege") ->
            hint("collegezaal", -1.4f, -1.0f, -0.2f, 0.6f, 1.4f, 2.0f, 1.8f, 1.0f, 0.2f, -0.4f, -40, 1.2f)
        has(blob, "gesprek", "praatje") ->
            hint("praat", -1.2f, -0.8f, 0.0f, 0.8f, 1.6f, 2.0f, 1.6f, 0.8f, 0.0f, -0.6f, -40, 1.2f)
        has(blob, "marktplein", "weekmarkt") ->
            hint("marktplein", -0.4f, -0.2f, 0.2f, 0.6f, 1.0f, 1.2f, 0.8f, 0.3f, -0.2f, -0.6f, -20, 0.8f)
        has(blob, "basic-fit", "basicfit", "trainmore") ->
            hint("basicfit", 1.6f, 1.8f, 0.6f, 0.2f, 0.4f, 0.8f, 1.0f, 0.8f, 0.2f, -0.2f, 40, 0.4f)
        has(blob, "regen", "buienradar", "regenfiets") ->
            hint("regen", 0.6f, 0.8f, 0.3f, 0.2f, 0.4f, 0.6f, 0.4f, 0.0f, -0.3f, -0.6f, 20, 0.3f)
        has(blob, "spits", "volle ov") ->
            hint("spits", 0.3f, 0.2f, 0.1f, 0.4f, 0.8f, 1.0f, 0.6f, 0.1f, -0.3f, -0.5f, 0, 0.5f)
        has(blob, "thuisavond", "avond thuis") ->
            hint("thuisavond", 0.5f, 0.6f, 0.4f, 0.2f, 0.2f, 0.1f, 0.0f, -0.2f, -0.5f, -0.8f, 10, 0.1f)
        has(blob, "nachttrein", "nachtnet") ->
            hint("nachttrein", -0.6f, -0.4f, 0.0f, 0.2f, 0.3f, 0.4f, 0.2f, -0.1f, -0.5f, -0.8f, -20, 0.2f)
        has(blob, "intercity", "ic direct") ->
            hint("intercity", 0.2f, 0.2f, 0.1f, 0.3f, 0.5f, 0.6f, 0.4f, 0.0f, -0.3f, -0.5f, 0, 0.3f)
        has(blob, "koffietent", "coffee", "starbucks") ->
            hint("koffietent", -0.6f, -0.3f, 0.2f, 0.6f, 1.0f, 1.2f, 0.8f, 0.3f, -0.2f, -0.6f, -20, 0.8f)
        has(blob, "huisarts", "wachtkamer") ->
            hint("huisarts", -1.4f, -1.0f, -0.2f, 0.6f, 1.4f, 1.8f, 1.4f, 0.6f, -0.2f, -0.6f, -40, 1.1f)
        has(blob, "ziekenhuis", "umc", "poli ") ->
            hint("ziekenhuis", -1.6f, -1.2f, -0.4f, 0.4f, 1.0f, 1.4f, 1.0f, 0.4f, -0.3f, -0.8f, -50, 1.0f)
        has(blob, "thuiskids", "kinderen thuis") ->
            hint("thuiskids", -1.0f, -0.6f, 0.0f, 0.4f, 0.8f, 1.0f, 0.6f, 0.1f, -0.4f, -0.8f, -30, 0.7f)
        else -> null
    }

    private fun hint(label: String, vararg v: Float): AdaptiveEqHint {
        val bands = v.take(10)
        val bass = if (v.size > 10) v[10].toInt() else 0
        val clarity = if (v.size > 11) v[11] else 0f
        return AdaptiveEqHint(label, bands, bass, clarity)
    }

    private fun has(blob: String, vararg keys: String) = keys.any { blob.contains(it) }
}
