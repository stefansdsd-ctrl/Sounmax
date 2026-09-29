package com.example.data

import java.util.Calendar

/** Volgende tijdslot + scene-hint, zonder extra hold. */
object NextHint {
    data class Slot(val hour: Int, val name: String, val hint: String)

    private val slots = listOf(
        Slot(6, "ochtend-pendel", "trein/wandel"),
        Slot(9, "focus-blok", "focus + cap"),
        Slot(12, "pauze", "lichte scene"),
        Slot(14, "middag", "werk/rit"),
        Slot(18, "avond", "cinema/game"),
        Slot(22, "nacht / rust", "slaap + fade"),
    )

    fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    fun next(): Slot {
        val h = currentHour()
        return slots.firstOrNull { it.hour > h } ?: slots.first()
    }

    fun short(): String {
        val n = next()
        val h = currentHour()
        val wait = if (n.hour > h) n.hour - h else (24 - h + n.hour)
        return "volgende ${n.name} over ${wait}u (${n.hint})"
    }
}
