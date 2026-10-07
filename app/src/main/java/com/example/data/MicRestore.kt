package com.example.data

import android.content.Context

/**
 * Bouwt het muziekvolume terug nadat een andere app stopt met opnemen.
 * Geen eigen baseline meer: DuckLane bewaart de stand. Geen sprong naar 100%.
 */
object MicRestore {
    private const val PREFS = "sounmax_mic_restore"

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "mic")
        return label(context)
    }

    fun remember(context: Context) = Unit

    fun tick(context: Context): Boolean {
        if (!enabled(context) || MicLive.recording(context)) return false
        if (!DuckLane.heldBy(context, "mic") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "mic")
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || MicLive.recording(context)) return false
        return DuckLane.heldBy(context, "mic") || DuckLane.restoring(context)
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Mic-herstel uit"
        active(context) -> "Volume komt terug"
        else -> "Mic-herstel aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
