package com.example.dsp

data class AdaptiveEqHint(
    val label: String,
    val offsetsDb: List<Float>,
    val bassDelta: Int = 0,
    val clarityDelta: Float = 0f
)

object AdaptiveTrackEq {
    val NONE = AdaptiveEqHint("neutraal", List(10) { 0f })

    fun hint(genre: String, title: String, artist: String): AdaptiveEqHint {
        val blob = "$genre $title $artist".lowercase()
        return when {
            containsAny(blob, "podcast", "speech", "interview", "nieuws", "news", "talk") ->
                AdaptiveEqHint("spraak", offs(-2.5f, -2f, -1.5f, -0.5f, 0.5f, 1.5f, 2.5f, 2f, 0.5f, -1f), -80, 1.2f)
            containsAny(blob, "luisterboek", "audiobook", "hoorspel") ->
                AdaptiveEqHint("verhaal", offs(-2f, -1.5f, -1f, 0f, 1f, 2f, 2f, 1f, 0f, -1f), -60, 1.0f)
            containsAny(blob, "asmr") ->
                AdaptiveEqHint("asmr", offs(-3f, -2.5f, -1.5f, -0.5f, 0.5f, 1f, 1.5f, 2f, 1.5f, 0.5f), -120, 0.8f)
            containsAny(blob, "hip hop", "hip-hop", "rap", "r&b", "rnb", "trap") ->
                AdaptiveEqHint("urban", offs(2.5f, 2f, 1f, 0f, -0.5f, -0.5f, 0f, 0.5f, 1f, 0.5f), 80, 0.2f)
            containsAny(blob, "edm", "electro", "techno", "house", "trance", "dnb") ->
                AdaptiveEqHint("edm", offs(2f, 1.5f, 0.5f, -0.5f, -1f, 0f, 0.5f, 1f, 1.5f, 2f), 60, 0.4f)
            containsAny(blob, "metal", "rock", "punk", "hardcore") ->
                AdaptiveEqHint("rock", offs(0.5f, 0.5f, 0f, 0.5f, 1f, 1.5f, 1.5f, 1f, 0.5f, 0f), 20, 0.6f)
            containsAny(blob, "classic", "orchestra", "symphony", "piano", "opera") ->
                AdaptiveEqHint("klassiek", offs(-1.5f, -1f, -0.5f, 0f, 0.5f, 1f, 1.5f, 1.5f, 2f, 1.5f), -40, 0.8f)
            containsAny(blob, "jazz", "blues", "soul") ->
                AdaptiveEqHint("jazz", offs(0.5f, 0.5f, 0.5f, 1f, 1.5f, 1f, 0.5f, 0.5f, 0f, -0.5f), 10, 0.3f)
            containsAny(blob, "lofi", "lo-fi", "chill", "ambient", "sleep") ->
                AdaptiveEqHint("chill", offs(1f, 0.5f, 0.5f, 0.5f, 0f, 0f, -0.5f, -1f, -1.5f, -2f), 20, -0.2f)
            containsAny(blob, "k-pop", "kpop", "j-pop", "jpop") ->
                AdaptiveEqHint("kpop", offs(1f, 0.6f, 0.2f, -0.2f, 0.4f, 0.8f, 1.2f, 1.4f, 1.2f, 0.6f), 20, 0.5f)
            containsAny(blob, "afrobeat", "afrobeats", "amapiano", "reggaeton") ->
                AdaptiveEqHint("afro", offs(2.2f, 1.8f, 0.8f, 0f, -0.3f, 0.2f, 0.6f, 0.8f, 1f, 0.4f), 70, 0.3f)
            containsAny(blob, "nederpop", "nederlandstalig", "smartlap") ->
                AdaptiveEqHint("nl-pop", offs(0.8f, 0.6f, 0.3f, 0.2f, 0.4f, 0.8f, 1.2f, 1f, 0.6f, 0.2f), 10, 0.6f)
            containsAny(blob, "pop", "dance", "disco") ->
                AdaptiveEqHint("pop", offs(1.2f, 0.8f, 0.2f, -0.3f, 0f, 0.4f, 0.8f, 1f, 1.2f, 0.8f), 30, 0.3f)
            containsAny(blob, "film", "score", "soundtrack", "ost") ->
                AdaptiveEqHint("film", offs(1f, 0.8f, 0.3f, 0f, 0.3f, 0.6f, 0.8f, 1.2f, 1.5f, 1.2f), 20, 0.4f)
            else -> NONE
        }
    }

    private fun offs(vararg v: Float): List<Float> = v.toList().let {
        if (it.size == 10) it else List(10) { i -> it.getOrElse(i) { 0f } }
    }

    private fun containsAny(blob: String, vararg keys: String): Boolean =
        keys.any { blob.contains(it) }
}
