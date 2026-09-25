package com.example.dsp

/** Batch 166: trein / winkel / tv / buiten / tram / overleg. */
object EqShape166 {
    /** Trein: rumble weg, spraak, pieken beperkt. */
    fun train(dsp: AudioDspManager) {
        EqShape.rumble(dsp, 2.4f)
        EqShape.speech(dsp)
        EqShape.tight(dsp)
    }

    /** Winkel: mud eruit, presence, sisklank zacht. */
    fun shop(dsp: AudioDspManager) {
        EqShape.mud(dsp)
        EqShape.presence(dsp)
        EqShape.deess(dsp, 1.2f)
        EqShape.clip(dsp, 5f)
    }

    /** TV/avond: body + vocal, highs zacht. */
    fun tv(dsp: AudioDspManager) {
        EqShape.body(dsp)
        EqShape.vocal(dsp)
        EqShape.night(dsp, 1.0f)
    }

    /** Buiten: punch + air, veilig clip. */
    fun outdoor(dsp: AudioDspManager) {
        EqShape.punch(dsp, 1.3f)
        EqShape.air(dsp, 1.1f)
        EqShape.clip(dsp, 5f)
    }

    /** Tram: sub-cut + spraak + hiss. */
    fun tram(dsp: AudioDspManager) {
        EqShape163.subCut(dsp, 2.4f)
        EqShape.speech(dsp)
        EqShape162.hiss(dsp, 1.2f)
    }

    /** Overleg: telefoonband + de-ess + tight. */
    fun meeting(dsp: AudioDspManager) {
        EqShape163.phone(dsp)
        EqShape.deess(dsp)
        EqShape.tight(dsp)
    }

    fun applyNamed(context: android.content.Context, name: String): Boolean {
        val key = name.lowercase()
        val mine = key in setOf(
            "train", "trein",
            "shop", "winkel",
            "tv", "avondtv",
            "outdoor", "buiten",
            "tram",
            "meeting", "overleg"
        )
        if (!mine) return EqShape165.applyNamed(context, name)
        val dsp = DspHolder.instance ?: return false
        EqUndo.push(context, dsp)
        when (key) {
            "train", "trein" -> train(dsp)
            "shop", "winkel" -> shop(dsp)
            "tv", "avondtv" -> tv(dsp)
            "outdoor", "buiten" -> outdoor(dsp)
            "tram" -> tram(dsp)
            "meeting", "overleg" -> meeting(dsp)
        }
        return true
    }
}
