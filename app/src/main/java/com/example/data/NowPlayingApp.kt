package com.example.data

import com.example.dsp.EqPreset

object NowPlayingApp {
    @Volatile var packageName: String? = null
    @Volatile var onBoundPreset: ((EqPreset) -> Unit)? = null
}
