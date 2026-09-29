package com.example.data

import android.content.Context

/**
 * Eén tik: leefstijl-holds uit, gehoorwacht hard-cap 70%, volume toepassen.
 * Safety-holds blijven via AudioReset/HoldPanic-gedrag.
 */
object QuickSafe {
    fun run(context: Context): String {
        val reset = AudioReset.run(context)
        HearingGuard.setEnabled(context, true)
        HearingGuard.setHardCap(context, true)
        if (HearingGuard.maxPercent(context) > 70) {
            HearingGuard.setMaxPercent(context, 70)
        }
        val capped = HearingGuard.applyCap(context)
        val cap = HearingGuard.maxPercent(context)
        val vol = HearingGuard.currentVolumePercent(context)
        val capBit = if (capped) "cap $vol%/$cap%" else "cap $cap% (nu $vol%)"
        return "Veilig · $reset · $capBit"
    }

    fun label(context: Context): String {
        val on = HearingGuard.enabled(context) && HearingGuard.hardCap(context)
        return if (on) "Veilig aan (${HearingGuard.maxPercent(context)}%)" else "Veilig uit"
    }
}
