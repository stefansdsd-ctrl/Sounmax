package com.example.dsp

import com.example.data.NowPlayingApp

object GenreEqApply {
    fun presetFrom(
        baselineGains: List<Float>,
        bass: Int,
        virt: Int,
        loud: Int,
        clarity: Float
    ): EqPreset {
        val hint = AdaptiveTrackEq.hint(
            NowPlayingApp.genre.orEmpty(),
            NowPlayingApp.title.orEmpty(),
            NowPlayingApp.artist.orEmpty()
        )
        val blended = baselineGains.mapIndexed { i, g ->
            (g + hint.offsetsDb.getOrElse(i) { 0f }).coerceIn(-12f, 12f)
        }
        return EqPreset(
            name = "Genre · ${hint.label}",
            bandGains = blended,
            bassBoost = (bass + hint.bassDelta).coerceIn(0, 1000),
            virtualizer = virt,
            loudness = loud,
            clarity = (clarity + hint.clarityDelta).coerceIn(0f, 10f),
            isCustom = true,
            category = "Genre-EQ",
            description = listOfNotNull(NowPlayingApp.title, NowPlayingApp.artist).joinToString(" · ")
        )
    }

    fun presetFromNowPlaying(current: List<Float>, bass: Int, virt: Int, loud: Int, clarity: Float): EqPreset =
        presetFrom(current, bass, virt, loud, clarity)

    fun trackKey(): String =
        listOf(
            NowPlayingApp.title.orEmpty(),
            NowPlayingApp.artist.orEmpty(),
            NowPlayingApp.genre.orEmpty()
        ).joinToString("|")
}
