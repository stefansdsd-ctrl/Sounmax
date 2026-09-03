package com.example.data

import com.example.dsp.EqPreset

object NowPlayingApp {
    @Volatile var packageName: String? = null
    @Volatile var title: String? = null
    @Volatile var artist: String? = null
    @Volatile var genre: String? = null
    @Volatile var onBoundPreset: ((EqPreset) -> Unit)? = null

    fun blob(): String = listOfNotNull(genre, title, artist).joinToString(" ")
}
