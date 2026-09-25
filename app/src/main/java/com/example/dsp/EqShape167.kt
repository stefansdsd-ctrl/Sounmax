package com.example.dsp

/** Batch 167: slaap / fiets / vlucht / focus / restaurant / concert + dosis. */
object EqShape167 {
    /** Slaap: highs + bass zacht, curve veilig. */
    fun sleep(dsp: AudioDspManager) {
        EqShape.night(dsp, 1.4f)
        EqShape.rumble(dsp, 1.6f)
        EqShape.safe(dsp)
    }

    /** Fiets: wind/rumble weg, spraak, verkeer hoorbaar. */
    fun bike(dsp: AudioDspManager) {
        EqShape.rumble(dsp, 2.2f)
        EqShape.speech(dsp)
        EqShape162.hiss(dsp, 1.1f)
        EqShape.clip(dsp, 5f)
    }

    /** Vliegtuig: sub-cut, nacht, tight. */
    fun plane(dsp: AudioDspManager) {
        EqShape163.subCut(dsp, 2.6f)
        EqShape.night(dsp, 1.1f)
        EqShape.tight(dsp)
    }

    /** Focus: mud eruit, presence, piek beperkt. */
    fun focus(dsp: AudioDspManager) {
        EqShape.mud(dsp, 2.0f)
        EqShape.clarity(dsp)
        EqShape.tight(dsp)
    }

    /** Restaurant: spraak + de-ess, bass omlaag. */
    fun restaurant(dsp: AudioDspManager) {
        EqShape.pocket(dsp, 1.6f)
        EqShape.speech(dsp)
        EqShape.deess(dsp, 1.3f)
        EqShape.clip(dsp, 5f)
    }

    /** Concert/live: punch + body, veilig clip. */
    fun concert(dsp: AudioDspManager) {
        EqShape.punch(dsp, 1.2f)
        EqShape.body(dsp)
        EqShape.clip(dsp, 5f)
    }

    /** Gehoorbescherming: flatten + night + hard clip. */
    fun dose(dsp: AudioDspManager) {
        EqShape.fade(dsp, 0.35f)
        EqShape.night(dsp, 1.6f)
        EqShape.clip(dsp, 3.5f)
    }

    fun applyNamed(context: android.content.Context, name: String): Boolean {
        val key = name.lowercase()
        val mine = key in setOf(
            "sleep", "slaap",
            "bike", "fiets",
            "plane", "vlucht", "vliegtuig",
            "focus",
            "restaurant", "eten", "eat",
            "concert", "live",
            "dose", "oor", "gehoor"
        )
        if (!mine) return EqShape166.applyNamed(context, name)
        val dsp = DspHolder.instance ?: return false
        EqUndo.push(context, dsp)
        when (key) {
            "sleep", "slaap" -> sleep(dsp)
            "bike", "fiets" -> bike(dsp)
            "plane", "vlucht", "vliegtuig" -> plane(dsp)
            "focus" -> focus(dsp)
            "restaurant", "eten", "eat" -> restaurant(dsp)
            "concert", "live" -> concert(dsp)
            "dose", "oor", "gehoor" -> dose(dsp)
        }
        return true
    }
}
