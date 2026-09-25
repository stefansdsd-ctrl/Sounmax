package com.example.dsp

/** Batch 165: café / werk / regen / keuken / game / bel. */
object EqShape165 {
    /** Café: bass omlaag, spraak omhoog, hiss weg. */
    fun cafe(dsp: AudioDspManager) {
        val src = dsp.bandGains.value
        if (src.size < 6) return
        EqShape.applyBands(dsp, src.mapIndexed { i, v ->
            when {
                i <= 1 -> v - 2.2f
                i in 4..6 -> v + 1.6f
                i >= src.lastIndex -> v - 1.4f
                else -> v
            }
        })
        EqShape.clip(dsp, 5f)
    }

    /** Werk/focus: mud eruit, presence, piek beperkt. */
    fun work(dsp: AudioDspManager) {
        EqShape.mud(dsp)
        EqShape.presence(dsp)
        EqShape.tight(dsp)
    }

    /** Regen: sub-cut + air, highs zacht. */
    fun rain(dsp: AudioDspManager) {
        EqShape163.subCut(dsp, 2.8f)
        EqShape.air(dsp)
        EqShape.night(dsp)
    }

    /** Keuken: mid-honk weg, spraak, hiss-cut. */
    fun kitchen(dsp: AudioDspManager) {
        EqShape163.honk(dsp, 1.6f)
        EqShape.speech(dsp)
        EqShape162.hiss(dsp, 1.3f)
    }

    /** Game: punch + presence, highs beperkt. */
    fun game(dsp: AudioDspManager) {
        EqShape.punch(dsp)
        EqShape.presence(dsp)
        EqShape.deess(dsp)
        EqShape.clip(dsp, 5f)
    }

    /** Bel: telefoonband + de-ess. */
    fun call(dsp: AudioDspManager) {
        EqShape163.phone(dsp)
        EqShape.deess(dsp)
    }

    fun applyNamed(context: android.content.Context, name: String): Boolean {
        val key = name.lowercase()
        val mine = key in setOf(
            "cafe", "café", "koffie",
            "work", "werk", "kantoor",
            "rain", "regen",
            "kitchen", "keuken",
            "game", "games",
            "call", "bel"
        )
        if (!mine) return EqShape164.applyNamed(context, name)
        val dsp = DspHolder.instance ?: return false
        EqUndo.push(context, dsp)
        when (key) {
            "cafe", "café", "koffie" -> cafe(dsp)
            "work", "werk", "kantoor" -> work(dsp)
            "rain", "regen" -> rain(dsp)
            "kitchen", "keuken" -> kitchen(dsp)
            "game", "games" -> game(dsp)
            "call", "bel" -> call(dsp)
        }
        return true
    }
}
