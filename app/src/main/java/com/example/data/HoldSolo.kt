package com.example.data

import android.content.Context

/**
 * Eén leefstijl-hold tegelijk. Safety-holds (ziekenhuis/warmte/accu/bedtime)
 * blijven staan. Voorkomt dat wandel+rit+trein tegelijk volume-caps stapelen.
 */
object HoldSolo {
    private const val PREFS = "sounmax_hold_solo"
    private const val KEY_ON = "enabled"

    private val lifestyle = setOf(
        "walk", "bike", "drive", "rain", "wind", "train",
        "game", "cinema", "nap", "concert", "flight", "meeting", "call"
    )

    fun enabled(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ON, on).apply()
    }

    fun toggle(context: Context): String {
        setEnabled(context, !enabled(context))
        return label(context)
    }

    fun label(context: Context) =
        if (enabled(context)) "Solo-hold aan" else "Solo-hold uit"

    /** Stop andere lifestyle-holds voordat [keepId] start. */
    fun prepareStart(context: Context, keepId: String) {
        if (!enabled(context)) return
        if (keepId !in lifestyle) return
        HoldPanic.activeList(context)
            .filter { it.id in lifestyle && it.id != keepId }
            .forEach { HoldPanic.stopOne(context, it.id) }
    }
}
