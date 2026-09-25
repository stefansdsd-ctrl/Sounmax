package com.example.dsp

/** Batch 164: context-EQ voor dagelijks gebruik. */
object EqShape164 {
    /** Podcast: lows omlaag, spraak omhoog, sibilance licht omlaag. */
    fun podcast(dsp: AudioDspManager) {
        val src = dsp.bandGains.value
        if (src.size < 6) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v ->
            when {
                i <= 1 -> v - 2.4f
                i in 4..6 -> v + 1.8f
                i >= src.lastIndex - 1 -> v - 1.2f
                else -> v
            }
        })
    }

    /** Auto: weg-rumble eruit, presence erin. */
    fun car(dsp: AudioDspManager) {
        EqShape163.subCut(dsp, 3.2f)
        EqShape.presence(dsp)
        EqShape.clip(dsp, 5f)
    }

    /** Sport: punch + tight. */
    fun gym(dsp: AudioDspManager) {
        EqShape.punch(dsp)
        EqShape.tight(dsp)
    }

    /** Film: sub-cut + vocal, highs zacht. */
    fun film(dsp: AudioDspManager) {
        EqShape163.subCut(dsp, 2.5f)
        EqShape.vocal(dsp)
        EqShape.night(dsp)
    }

    /** Wandelen: minder bass, meer mids (verkeer hoorbaar). */
    fun walk(dsp: AudioDspManager) {
        val src = dsp.bandGains.value
        if (src.size < 5) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v ->
            when {
                i <= 1 -> v - 2.0f
                i in 3..5 -> v + 1.1f
                else -> v
            }
        })
    }

    /** Radio: mid-scoop, lucht eraf. */
    fun radio(dsp: AudioDspManager) {
        EqShape.scoop(dsp)
        EqShape162.hiss(dsp, 1.4f)
    }

    fun applyNamed(context: android.content.Context, name: String): Boolean {
        val key = name.lowercase()
        val mine = key in setOf(
            "podcast", "pod", "car", "auto", "gym", "sport",
            "film", "movie", "walk", "wandel", "radio"
        )
        if (!mine) return EqShape163.applyNamed(context, name)
        val dsp = DspHolder.instance ?: return false
        EqUndo.push(context, dsp)
        when (key) {
            "podcast", "pod" -> podcast(dsp)
            "car", "auto" -> car(dsp)
            "gym", "sport" -> gym(dsp)
            "film", "movie" -> film(dsp)
            "walk", "wandel" -> walk(dsp)
            "radio" -> radio(dsp)
        }
        return true
    }
}
