package com.example.data

import android.content.Context

/** Eén tik: leefstijl-holds uit, safety blijft, duidelijke status. */
object AudioReset {
    fun run(context: Context): String {
        val msg = HoldPanic.stopAll(context)
        return if (msg.startsWith("Geen")) "Reset: geen hold actief"
        else "Reset · $msg"
    }

    fun label(context: Context) = "Reset (${HoldPanic.activeCount(context)})"
}
