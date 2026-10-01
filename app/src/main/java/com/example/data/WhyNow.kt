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
        CallTransparency.apply(context)
        NotifyDuck.apply(context)
        ShakeAware.apply(context)
        ShakeAware.ensure(context)
        FlipQuiet.ensure(context)
        FlipQuiet.apply(context)
        ChargeNightCap.apply(context)
        PocketGuard.ensure(context)
        PocketGuard.clampUp(context)
        SpeakerGuard.ensure(context)
        AlarmSoon.apply(context)
        HiddenScenes.maybeAutoSlim(context)
        val net = OfflineGuard.label(context)
        val codec = CodecProbe.last(context)
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
        val hear = if (CallTransparency.isOn(context)) CallTransparency.label(context) else null
        val duck = if (NotifyDuck.active(context)) NotifyDuck.label(context) else null
        val pocket = if (PocketGuard.enabled(context) && PocketGuard.isPocketed()) PocketGuard.label(context) else null
        val shake = if (ShakeAware.active(context)) ShakeAware.label(context) else null
        val flip = if (FlipQuiet.active(context)) FlipQuiet.label(context) else null
        val charge = if (ChargeNightCap.active(context)) ChargeNightCap.label(context) else null
        val speaker = if (SpeakerGuard.active(context)) SpeakerGuard.label(context) else null
        val alarm = if (AlarmSoon.active(context)) AlarmSoon.label(context) else null
        val bits = listOfNotNull(
            "$slot · $holdBit · $sceneBit · $dose · $next · $net · $codec",
            ear, talk, door, street, find, hear, duck, pocket, shake, flip, charge, speaker, alarm
        )
        return bits.joinToString(" · ")
    }
}
