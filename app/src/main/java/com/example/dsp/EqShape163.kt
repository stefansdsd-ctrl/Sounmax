package com.example.dsp

import kotlin.math.max

/** Batch 163: praktische luister-fixes. */
object EqShape163 {
    /** Telefoonband: uiteinden omlaag, spraakband omhoog. */
    fun phone(dsp: AudioDspManager) {
        val src = dsp.bandGains.value
        if (src.size < 6) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v ->
            when {
                i <= 1 -> v - 3.0f
                i in 4..6 -> v + 1.6f
                i >= src.lastIndex - 1 -> v - 2.2f
                else -> v
            }
        })
    }

    /** Laatste twee bands +1,2 dB. */
    fun sparkle(dsp: AudioDspManager, boost: Float = 1.2f) {
        val src = dsp.bandGains.value
        val start = max(0, src.size - 2)
        EqShape.applyBands(dsp, src.mapIndexed { i, v -> if (i >= start) v + boost else v })
    }

    /** Eerste band −4 dB (sub-rumble). */
    fun subCut(dsp: AudioDspManager, cut: Float = 4.0f) {
        EqShape.applyBands(dsp, dsp.bandGains.value.mapIndexed { i, v -> if (i == 0) v - cut else v })
    }

    /** Honky mid (band 3–4) −1,8 dB. */
    fun honk(dsp: AudioDspManager, cut: Float = 1.8f) {
        EqShape.applyBands(dsp, dsp.bandGains.value.mapIndexed { i, v -> if (i in 3..4) v - cut else v })
    }

    /** Nacht + hiss-cut. */
    fun sleep(dsp: AudioDspManager) {
        EqShape.night(dsp)
        EqShape162.hiss(dsp, 1.6f)
        EqShape.scale(dsp, 0.85f)
    }

    /** Piek ±4 dB. */
    fun soft4(dsp: AudioDspManager) {
        EqShape.clip(dsp, 4f)
    }

    fun applyNamed(context: android.content.Context, name: String): Boolean {
        val key = name.lowercase()
        val mine = key in setOf("phone", "tel", "sparkle", "subcut", "sub", "honk", "sleep", "soft4")
        if (!mine) return EqShape162.applyNamed(context, name)
        val dsp = DspHolder.instance ?: return false
        EqUndo.push(context, dsp)
        when (key) {
            "phone", "tel" -> phone(dsp)
            "sparkle" -> sparkle(dsp)
            "subcut", "sub" -> subCut(dsp)
            "honk" -> honk(dsp)
            "sleep" -> sleep(dsp)
            "soft4" -> soft4(dsp)
        }
        return true
    }
}
