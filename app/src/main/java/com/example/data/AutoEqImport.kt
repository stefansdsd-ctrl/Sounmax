package com.example.data

import com.example.dsp.EqPreset

/**
 * Import AutoEQ (autoeq.app) style corrections into Sounmax 10-band EQ.
 * Supports:
 * - GraphicEQ: 20 0.5; 25 0.4; ... lines
 * - Simple "freq gain" pairs (one per line or comma/space separated)
 * - Optional Preamp: -X.X dB line
 */
object AutoEqImport {

    data class ImportResult(
        val preset: EqPreset,
        val points: List<AutoEqPoint>,
        val preampDb: Float,
        val sourceHint: String
    )

    /**
     * Parse text (pasted AutoEQ result or .txt) into scaled 10-band preset.
     * @param strength 0f..1f (1 = full correction)
     */
    fun parse(
        text: String,
        name: String = "AutoEQ Import",
        strength: Float = 1f,
        headphoneHint: String = "TAH6519"
    ): ImportResult? {
        val points = extractPoints(text)
        if (points.size < 3) return null

        val preamp = extractPreamp(text)
        val scaled = AutoEqStrength.scale(points, strength.coerceIn(0f, 1f))
        val bands = AutoEqStrength.toTenBands(scaled)
        val safePre = AutoEqStrength.safePreamp(scaled)
        val effectivePreamp = if (preamp != 0f) preamp else safePre

        // Map preamp into loudness-ish offset (heuristic, keeps levels safe)
        val loudness = (400 + (effectivePreamp * 40f).toInt()).coerceIn(0, 800)

        val preset = EqPreset(
            name = name.ifBlank { "AutoEQ $headphoneHint" },
            bandGains = bands.map { it.coerceIn(-12f, 12f) },
            bassBoost = 300,
            virtualizer = 200,
            loudness = loudness,
            clarity = 8.0f,
            isCustom = true,
            category = "AutoEQ",
            description = "Geïmporteerd uit AutoEQ ($headphoneHint), strength ${(strength * 100).toInt()}%, preamp ${"%.1f".format(effectivePreamp)} dB"
        )

        return ImportResult(
            preset = preset,
            points = scaled,
            preampDb = effectivePreamp,
            sourceHint = if (text.contains("GraphicEQ", ignoreCase = true)) "GraphicEQ" else "points"
        )
    }

    /** Convenience: import and save to EqPersist slot. */
    fun importAndSave(
        context: android.content.Context,
        text: String,
        slot: Int = 0,
        name: String = "AutoEQ",
        strength: Float = 1f
    ): EqPreset? {
        val result = parse(text, name, strength) ?: return null
        EqPersist.save(context, result.preset, slot)
        return result.preset
    }

    private fun extractPreamp(text: String): Float {
        val re = Regex("""(?i)Preamp:\s*(-?[\d.]+)\s*dB""")
        return re.find(text)?.groupValues?.getOrNull(1)?.toFloatOrNull() ?: 0f
    }

    private fun extractPoints(text: String): List<AutoEqPoint> {
        val points = mutableListOf<AutoEqPoint>()

        // GraphicEQ: 20 0.5; 25 0.4; ...
        val graphic = Regex("(?i)GraphicEQ:\s*(.+)")
        graphic.findAll(text).forEach { m ->
            val body = m.groupValues[1]
            Regex("""([\d.]+)\s+(-?[\d.]+)""").findAll(body).forEach { p ->
                val f = p.groupValues[1].toFloatOrNull()
                val g = p.groupValues[2].toFloatOrNull()
                if (f != null && g != null && f > 0f) points.add(AutoEqPoint(f, g))
            }
        }

        if (points.isNotEmpty()) return points.sortedBy { it.freq }

        // Fallback: lines or tokens "freq gain"
        val pairRe = Regex("""([\d.]+)\s*[,;\t ]+\s*(-?[\d.]+)""")
        for (line in text.lines()) {
            val t = line.trim()
            if (t.isEmpty() || t.startsWith("#") || t.startsWith("//")) continue
            if (t.contains("Preamp", ignoreCase = true)) continue
            if (t.contains("Filter:", ignoreCase = true)) continue
            pairRe.findAll(t).forEach { p ->
                val f = p.groupValues[1].toFloatOrNull()
                val g = p.groupValues[2].toFloatOrNull()
                if (f != null && g != null && f in 10f..24000f && g in -24f..24f) {
                    points.add(AutoEqPoint(f, g))
                }
            }
        }

        return points.distinctBy { it.freq }.sortedBy { it.freq }
    }

    /** Minimal sample curve for TAH6519-style closed ANC (demo / offline). */
    fun sampleTah6519Correction(): String = """
        Preamp: -5.2 dB
        GraphicEQ: 20 4.2; 25 3.8; 40 3.0; 63 2.2; 100 1.2; 160 0.4; 250 -0.6; 400 -1.2; 630 -0.8; 1000 0.5; 1600 1.8; 2500 2.8; 4000 3.2; 6300 2.4; 10000 1.6; 16000 0.8
    """.trimIndent()
}
