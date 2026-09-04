package com.example.dsp

import java.util.Calendar

/** Lichte EQ-laag op basis van het uur: ochtend warmer, avond zachter hoog. */
object CircadianEq {
    fun overlay(): AdaptiveEqHint {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..8 -> AdaptiveEqHint(
                "circ-ochtend",
                listOf(0.6f, 0.4f, 0.2f, 0.1f, 0.2f, 0.3f, 0.2f, 0.1f, -0.1f, -0.2f),
                bassDelta = 15,
                clarityDelta = 0.15f
            )
            in 21..23 -> AdaptiveEqHint(
                "circ-avond",
                listOf(0.3f, 0.2f, 0.1f, 0f, -0.1f, -0.2f, -0.4f, -0.6f, -0.8f, -1.0f),
                bassDelta = 10,
                clarityDelta = -0.2f
            )
            in 0..5 -> AdaptiveEqHint(
                "circ-nacht",
                listOf(0.2f, 0.1f, 0f, -0.1f, -0.2f, -0.3f, -0.6f, -0.9f, -1.2f, -1.4f),
                bassDelta = 0,
                clarityDelta = -0.3f
            )
            else -> AdaptiveTrackEq.NONE
        }
    }

    fun blend(base: AdaptiveEqHint, extra: AdaptiveEqHint): AdaptiveEqHint {
        if (extra === AdaptiveTrackEq.NONE || extra.label == "neutraal") return base
        val n = minOf(base.offsetsDb.size, extra.offsetsDb.size, 10)
        val offs = List(10) { i ->
            if (i < n) (base.offsetsDb[i] + extra.offsetsDb[i]).coerceIn(-6f, 6f) else 0f
        }
        val label = if (base.label == "neutraal") extra.label else "${base.label}+${extra.label}"
        return AdaptiveEqHint(
            label,
            offs,
            bassDelta = base.bassDelta + extra.bassDelta,
            clarityDelta = base.clarityDelta + extra.clarityDelta
        )
    }
}
