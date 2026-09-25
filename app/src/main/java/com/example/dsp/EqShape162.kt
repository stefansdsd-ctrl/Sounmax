package com.example.dsp

import kotlin.math.max

/** Batch 162 curve-tools. */
object EqShape162 {
    fun boxy(dsp: AudioDspManager, cut: Float = 1.7f) {
        EqShape.applyBands(dsp, dsp.bandGains.value.mapIndexed { i, v -> if (i == 3) v - cut else v })
    }

    fun nasal(dsp: AudioDspManager, cut: Float = 1.5f) {
        val src = dsp.bandGains.value
        if (src.size < 6) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v -> if (i in 4..5) v - cut else v })
    }

    fun bloom(dsp: AudioDspManager, boost: Float = 1.4f) {
        EqShape.applyBands(dsp, dsp.bandGains.value.mapIndexed { i, v -> if (i in 0..1) v + boost else v })
    }

    fun hiss(dsp: AudioDspManager, cut: Float = 2.0f) {
        val src = dsp.bandGains.value
        if (src.isEmpty()) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v -> if (i == src.lastIndex) v - cut else v })
    }

    fun widen(dsp: AudioDspManager, boost: Float = 1.0f) {
        val src = dsp.bandGains.value
        if (src.size < 3) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v ->
            if (i == 0 || i == src.lastIndex) v + boost else v
        })
    }

    fun narrow(dsp: AudioDspManager, cut: Float = 1.0f) {
        val src = dsp.bandGains.value
        if (src.size < 3) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v ->
            if (i == 0 || i == src.lastIndex) v - cut else v
        })
    }

    fun zeroBass(dsp: AudioDspManager) {
        EqShape.applyBands(dsp, dsp.bandGains.value.mapIndexed { i, v -> if (i < 3) 0f else v })
    }

    fun zeroTreble(dsp: AudioDspManager) {
        val src = dsp.bandGains.value
        val start = max(0, src.size - 3)
        EqShape.applyBands(dsp, src.mapIndexed { i, v -> if (i >= start) 0f else v })
    }

    fun applyNamed(context: android.content.Context, name: String): Boolean {
        val dsp = DspHolder.instance ?: return false
        EqUndo.push(context, dsp)
        when (name.lowercase()) {
            "flat" -> EqShape.flat(dsp)
            "safe" -> EqShape.safe(dsp)
            "speech" -> EqShape.speech(dsp)
            "night" -> EqShape.night(dsp)
            "vocal" -> EqShape.vocal(dsp)
            "undo" -> {
                EqUndo.popApply(context, dsp)
                return true
            }
            "boxy" -> boxy(dsp)
            "nasal" -> nasal(dsp)
            "bloom" -> bloom(dsp)
            "hiss" -> hiss(dsp)
            "widen" -> widen(dsp)
            "narrow" -> narrow(dsp)
            "0bass", "zerobass" -> zeroBass(dsp)
            "0treble", "zerotreble" -> zeroTreble(dsp)
            else -> return false
        }
        return true
    }
}
