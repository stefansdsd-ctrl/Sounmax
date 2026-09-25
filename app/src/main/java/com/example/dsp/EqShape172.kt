package com.example.dsp

/** Micro-aanpassingen: 1 dB bass/mids/air zonder de hele curve te vervangen. */
object EqShape172 {
    private const val MIN = -12f
    private const val MAX = 12f

    fun bumpBass(dsp: AudioDspManager, db: Float) = bump(dsp, 0..2, db)
    fun bumpMid(dsp: AudioDspManager, db: Float) = bump(dsp, 3..6, db)
    fun bumpAir(dsp: AudioDspManager, db: Float) = bump(dsp, 7..9, db)

    fun nudgeBand(dsp: AudioDspManager, index: Int, db: Float) {
        val src = dsp.bandGains.value
        if (index !in src.indices) return
        dsp.updateBandGain(index, (src[index] + db).coerceIn(MIN, MAX))
    }

    private fun bump(dsp: AudioDspManager, range: IntRange, db: Float) {
        val src = dsp.bandGains.value
        src.forEachIndexed { i, v ->
            if (i in range) dsp.updateBandGain(i, (v + db).coerceIn(MIN, MAX))
        }
    }
}
