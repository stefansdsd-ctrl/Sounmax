package com.example.media

import android.content.Context
import android.widget.Toast

/**
 * Als L/R-accu-delta > 25% of één oordop offline: snackbar + soft-mono + crossfeed 60%.
 */
object OneEarFallback {
    const val DELTA_PCT = 25
    const val CROSSFEED = 0.60f

    data class Decision(
        val active: Boolean,
        val reason: String?,
        val crossfeed: Float,
        val softMono: Boolean,
    )

    fun evaluate(leftPct: Int?, rightPct: Int?, leftOnline: Boolean, rightOnline: Boolean): Decision {
        val offline = !leftOnline || !rightOnline
        val delta = if (leftPct != null && rightPct != null) kotlin.math.abs(leftPct - rightPct) else 0
        val unbalanced = delta > DELTA_PCT
        val active = offline || unbalanced
        val reason = when {
            !leftOnline && rightOnline -> "Links offline — soft-mono + crossfeed 60%"
            !rightOnline && leftOnline -> "Rechts offline — soft-mono + crossfeed 60%"
            !leftOnline && !rightOnline -> "Beide oordoppen offline"
            unbalanced -> "Accu L/R Δ${delta}% — soft-mono + crossfeed 60%"
            else -> null
        }
        return Decision(active, reason, if (active) CROSSFEED else 0f, active && (offline || unbalanced))
    }

    fun notify(context: Context, decision: Decision) {
        val msg = decision.reason ?: return
        if (!decision.active) return
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
