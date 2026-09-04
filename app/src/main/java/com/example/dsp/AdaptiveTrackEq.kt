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
            containsAny(blob, "stereo test", "left right", "links rechts") ->
                AdaptiveEqHint("stereo", offs(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f, 1.2f, 1.4f, 1.2f, 0.8f), 0, 0.6f)
            containsAny(blob, "bass test", "bass-check", "sub sweep") ->
                AdaptiveEqHint("basscheck", offs(4f, 3.2f, 1.2f, -0.6f, -1.2f, -0.4f, 0.2f, 0.4f, 0.2f, 0.2f, 0f), 140, 0.1f)
            containsAny(blob, "ochtend", "morning mix", "good morning") ->
                AdaptiveEqHint("ochtend", offs(0.8f, 0.6f, 0.4f, 0.2f, 0.4f, 0.8f, 1.0f, 0.8f, 0.4f, 0.1f), 20, 0.4f)
            containsAny(blob, "podcast", "speech", "interview", "nieuws", "news", "talk") ->
                AdaptiveEqHint("spraak", offs(-2.5f, -2f, -1.5f, -0.5f, 0.5f, 1.5f, 2.5f, 2f, 0.5f, -1f), -80, 1.2f)
            containsAny(blob, "luisterboek", "audiobook", "hoorspel") ->
                AdaptiveEqHint("verhaal", offs(-2f, -1.5f, -1f, 0f, 1f, 2f, 2f, 1f, 0f, -1f), -60, 1.0f)
            containsAny(blob, "asmr") ->
                AdaptiveEqHint("asmr", offs(-3f, -2.5f, -1.5f, -0.5f, 0.5f, 1f, 1.5f, 2f, 1.5f, 0.5f), -120, 0.8f)
            containsAny(blob, "hip hop", "hip-hop", "rap", "r&b", "rnb", "trap") ->
                AdaptiveEqHint("urban", offs(2.5f, 2f, 1f, 0f, -0.5f, -0.5f, 0f, 0.5f, 1f, 0.5f), 80, 0.2f)
            containsAny(blob, "drum and bass", "liquid funk", "jungle") ->
                AdaptiveEqHint("dnb", offs(2.4f, 2.0f, 0.4f, -0.8f, -1.0f, 0.2f, 0.8f, 1.6f, 2.0f, 1.8f), 80, 0.5f)
            containsAny(blob, "uk garage", "2-step", "ukg", "speed garage") ->
                AdaptiveEqHint("ukg", offs(1.8f, 1.4f, 0.4f, -0.4f, 0.2f, 0.6f, 1.0f, 1.2f, 1.0f, 0.4f), 50, 0.5f)
            containsAny(blob, "trance", "progressive house", "uplifting") ->
                AdaptiveEqHint("trance", offs(1.2f, 0.8f, 0.2f, -0.4f, 0.2f, 0.8f, 1.4f, 1.8f, 2.0f, 1.6f), 30, 0.6f)
            containsAny(blob, "dubstep", "brostep", "riddim") ->
                AdaptiveEqHint("dubstep", offs(3.0f, 2.4f, 0.6f, -1.0f, -1.2f, 0.2f, 0.6f, 1.2f, 1.6f, 1.2f), 110, 0.2f)
            containsAny(blob, "techno") ->
                AdaptiveEqHint("techno", offs(2.2f, 1.8f, 0.2f, -0.8f, -0.6f, 0.2f, 0.8f, 1.2f, 1.6f, 1.4f), 70, 0.4f)
            containsAny(blob, "house", "deep house", "tech house") ->
                AdaptiveEqHint("house", offs(1.8f, 1.4f, 0.4f, -0.4f, 0.0f, 0.4f, 0.8f, 1.0f, 1.2f, 0.8f), 50, 0.4f)
            containsAny(blob, "edm", "electro", "dnb") ->
                AdaptiveEqHint("edm", offs(2f, 1.5f, 0.5f, -0.5f, -1f, 0f, 0.5f, 1f, 1.5f, 2f), 60, 0.4f)
            containsAny(blob, "gabber", "happy hardcore", "uptempo") ->
                AdaptiveEqHint("gabber", offs(2.6f, 2.2f, 0.4f, -0.8f, -1.2f, 0.4f, 1.0f, 1.6f, 2.2f, 2.4f), 90, 0.6f)
            containsAny(blob, "hardstyle", "rawstyle", "frenchcore", "hardcore") ->
                AdaptiveEqHint("hardstyle", offs(2.4f, 2.0f, 0.6f, -0.6f, -1.0f, 0.2f, 0.8f, 1.4f, 2.0f, 2.2f), 80, 0.5f)
            containsAny(blob, "phonk", "drift phonk") ->
                AdaptiveEqHint("phonk", offs(2.8f, 2.2f, 0.8f, -0.4f, -0.8f, 0.2f, 0.6f, 1.2f, 1.6f, 1.0f), 100, 0.2f)
            containsAny(blob, "country", "folk", "americana") ->
                AdaptiveEqHint("country", offs(0.4f, 0.4f, 0.6f, 0.8f, 1.0f, 0.8f, 0.6f, 0.4f, 0.2f, -0.2f), 10, 0.4f)
            containsAny(blob, "gospel", "koor", "choir") ->
                AdaptiveEqHint("gospel", offs(-0.5f, -0.2f, 0.2f, 0.8f, 1.4f, 1.6f, 1.4f, 1.0f, 0.6f, 0.2f), -20, 0.7f)
            containsAny(blob, "voetbal", "eredivisie", "commentaar", "commentary", "football", "soccer") ->
                AdaptiveEqHint("commentaar", offs(-2.0f, -1.6f, -1.0f, 0.2f, 1.4f, 2.2f, 2.4f, 1.6f, 0.4f, -0.8f), -70, 1.3f)
            containsAny(blob, "metalcore", "deathcore", "post-hardcore") ->
                AdaptiveEqHint("metalcore", offs(0.8f, 0.6f, 0.2f, 0.8f, 1.4f, 1.8f, 1.6f, 1.2f, 0.6f, 0.2f), 30, 0.7f)
            containsAny(blob, "metal", "rock", "punk") ->
                AdaptiveEqHint("rock", offs(0.5f, 0.5f, 0f, 0.5f, 1f, 1.5f, 1.5f, 1f, 0.5f, 0f), 20, 0.6f)
            containsAny(blob, "acoustic", "unplugged", "akoestisch") ->
                AdaptiveEqHint("akoestisch", offs(-0.6f, -0.2f, 0.4f, 0.8f, 1.2f, 1.4f, 1.2f, 0.8f, 0.3f, -0.2f), -10, 0.7f)
            containsAny(blob, "classic", "orchestra", "symphony", "piano", "opera") ->
                AdaptiveEqHint("klassiek", offs(-1.5f, -1f, -0.5f, 0f, 0.5f, 1f, 1.5f, 1.5f, 2f, 1.5f), -40, 0.8f)
            containsAny(blob, "cardio", "running", "workout mix") ->
                AdaptiveEqHint("cardio", offs(2.0f, 1.6f, 0.4f, -0.4f, 0.0f, 0.4f, 0.8f, 1.2f, 1.4f, 1.0f), 70, 0.4f)
            containsAny(blob, "jazz", "blues", "soul") ->
                AdaptiveEqHint("jazz", offs(0.5f, 0.5f, 0.5f, 1f, 1.5f, 1f, 0.5f, 0.5f, 0f, -0.5f), 10, 0.3f)
            containsAny(blob, "lofi", "lo-fi", "chill", "ambient", "sleep", "nature") ->
                AdaptiveEqHint("chill", offs(1f, 0.5f, 0.5f, 0.5f, 0f, 0f, -0.5f, -1f, -1.5f, -2f), 20, -0.2f)
            containsAny(blob, "k-pop", "kpop", "j-pop", "jpop") ->
                AdaptiveEqHint("kpop", offs(1f, 0.6f, 0.2f, -0.2f, 0.4f, 0.8f, 1.2f, 1.4f, 1.2f, 0.6f), 20, 0.5f)
            containsAny(blob, "afrobeat", "afrobeats", "amapiano", "reggaeton") ->
                AdaptiveEqHint("afro", offs(2.2f, 1.8f, 0.8f, 0f, -0.3f, 0.2f, 0.6f, 0.8f, 1f, 0.4f), 70, 0.3f)
            containsAny(blob, "nederhop", "nederlandstalig rap", "nl rap") ->
                AdaptiveEqHint("nederhop", offs(2.2f, 1.8f, 0.6f, -0.2f, 0.2f, 0.8f, 1.4f, 1.2f, 0.6f, 0.2f), 70, 0.5f)
            containsAny(blob, "nederpop", "nederlandstalig", "smartlap") ->
                AdaptiveEqHint("nl-pop", offs(0.8f, 0.6f, 0.3f, 0.2f, 0.4f, 0.8f, 1.2f, 1f, 0.6f, 0.2f), 10, 0.6f)
            containsAny(blob, "drill", "grime", "uk rap") ->
                AdaptiveEqHint("drill", offs(2.0f, 1.6f, 0.6f, -0.2f, -0.4f, 0.2f, 0.8f, 1.0f, 0.6f, 0.2f), 70, 0.3f)
            containsAny(blob, "reggae", "dancehall", "ska") ->
                AdaptiveEqHint("reggae", offs(1.8f, 1.4f, 0.6f, 0.2f, 0.4f, 0.6f, 0.4f, 0.2f, 0.2f, -0.2f), 50, 0.2f)
            containsAny(blob, "latin", "salsa", "bachata", "cumbia") ->
                AdaptiveEqHint("latin", offs(1.2f, 1.0f, 0.4f, 0.2f, 0.6f, 0.8f, 1.0f, 0.8f, 0.4f, 0.2f), 30, 0.4f)
            containsAny(blob, "anime", "ost", "j-rock") ->
                AdaptiveEqHint("anime", offs(0.8f, 0.6f, 0.2f, 0.0f, 0.4f, 0.8f, 1.2f, 1.4f, 1.2f, 0.6f), 20, 0.5f)
            containsAny(blob, "synthwave", "retrowave", "outrun") ->
                AdaptiveEqHint("synthwave", offs(1.6f, 1.2f, 0.4f, -0.2f, 0.2f, 0.6f, 1.0f, 1.4f, 1.6f, 1.0f), 40, 0.4f)
            containsAny(blob, "hyperpop", "glitchcore") ->
                AdaptiveEqHint("hyperpop", offs(0.4f, 0.2f, -0.4f, -0.2f, 0.6f, 1.2f, 1.8f, 2.2f, 2.0f, 1.4f), 20, 0.8f)
            containsAny(blob, "boom bap", "boombap") ->
                AdaptiveEqHint("boom-bap", offs(1.6f, 1.4f, 0.8f, 0.2f, 0.4f, 0.8f, 1.0f, 0.6f, 0.2f, -0.2f), 50, 0.4f)
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
