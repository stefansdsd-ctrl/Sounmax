package com.example.data

import android.content.Context
import java.util.Calendar

/** Uitleg waarom de app nu deze scene/hold voorstelt. */
object WhyNow {
    fun text(context: Context): String {
        EarRest.apply(context)
        TalkSoft.apply(context)
        DoorListen.apply(context)
        StreetListen.apply(context)
        FindBeep.apply(context)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val winner = SceneHoldPriority.label(context)
        val holds = HoldPanic.activeList(context).joinToString("+") { it.name }
        val scene = LastSceneRestore.scene(context)?.name
        val today = WeeklyListenReport.last7Days(context).lastOrNull()?.minutes ?: 0
        val dose = WeeklyListenReport.hint(today)
        val slot = when (hour) {
            in 6..8 -> "ochtend-pendel"
            in 9..11 -> "focus-blok"
            in 12..13 -> "pauze"
            in 14..17 -> "middag"
            in 18..21 -> "avond"
            else -> "nacht / rust"
        }
        val sceneBit = scene?.let { "laatst $it" } ?: "geen scene"
        val holdBit = if (holds.isBlank()) winner else holds
        val next = NextHint.short()
        val ear = if (EarRest.enabled(context)) EarRest.label(context) else null
        val talk = if (TalkSoft.active(context)) TalkSoft.label(context) else null
        val door = if (DoorListen.active(context)) DoorListen.label(context) else null
        val street = if (StreetListen.active(context)) StreetListen.label(context) else null
        val find = if (FindBeep.active(context)) FindBeep.label(context) else null
        val bits = listOfNotNull("$slot · $holdBit · $sceneBit · $dose · $next", ear, talk, door, street, find)
        return bits.joinToString(" · ")
    }
}
