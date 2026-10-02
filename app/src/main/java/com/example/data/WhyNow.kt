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
        UnplugPause.ensure(context)
        HeadsetBatt.ensure(context)
        LowHsCap.apply(context)
        ReplugPlay.ensure(context)
        AclPause.ensure(context)
        PhoneLowCap.ensure(context)
        PhoneLowCap.apply(context)
        MicBusy.ensure(context)
        MicBusy.apply(context)
        IdleCap.apply(context)
        SessionCap.apply(context)
        CarCap.apply(context)
        AlarmSoon.apply(context)
        NoisyRoute.ensure(context)
        PeakCap.apply(context)
        RingDuck.apply(context)
        MorningSoft.apply(context)
        CommDuck.apply(context)
        EveningSoft.apply(context)
        NightSoft.apply(context)
        LunchSoft.apply(context)
        WeekendSoft.apply(context)
        MeteredSoft.apply(context)
        DndSoft.apply(context)
        AirplaneSoft.apply(context)
        SaverSoft.apply(context)
        ScreenOffSoft.apply(context)
        HotspotSoft.apply(context)
        LockSoft.apply(context)
        SilentSoft.apply(context)
        JumpGuard.apply(context)
        DuckRestore.restore(context)
        HiddenScenes.maybeAutoSlim(context)
        VpnSoft.apply(context)
        WiredSoft.apply(context)
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
        val unplug = if (UnplugPause.active(context)) UnplugPause.label(context) else null
        val batt = if (HeadsetBatt.active(context)) HeadsetBatt.label(context) else null
        val hscap = if (LowHsCap.active(context)) LowHsCap.label(context) else null
        val replug = if (ReplugPlay.active(context)) ReplugPlay.label(context) else null
        val acl = if (AclPause.active(context)) AclPause.label(context) else null
        val phonecap = if (PhoneLowCap.active(context)) PhoneLowCap.label(context) else null
        val mic = if (MicBusy.active(context)) MicBusy.label(context) else null
        val idle = if (IdleCap.active(context)) IdleCap.label(context) else null
        val session = if (SessionCap.active(context)) SessionCap.label(context) else null
        val car = if (CarCap.active(context)) CarCap.label(context) else null
        val alarm = if (AlarmSoon.active(context)) AlarmSoon.label(context) else null
        val route = if (NoisyRoute.active(context)) NoisyRoute.label(context) else null
        val peak = if (PeakCap.active(context)) PeakCap.label(context) else null
        val ring = if (RingDuck.active(context)) RingDuck.label(context) else null
        val morning = if (MorningSoft.active(context)) MorningSoft.label(context) else null
        val comm = if (CommDuck.active(context)) CommDuck.label(context) else null
        val evening = if (EveningSoft.active(context)) EveningSoft.label(context) else null
        val night = if (NightSoft.active(context)) NightSoft.label(context) else null
        val restored = if (DuckRestore.active(context)) DuckRestore.label(context) else null
        val jump = if (JumpGuard.active(context)) JumpGuard.label(context) else null
        val lunch = if (LunchSoft.active(context)) LunchSoft.label(context) else null
        val weekend = if (WeekendSoft.active(context)) WeekendSoft.label(context) else null
        val metered = if (MeteredSoft.active(context)) MeteredSoft.label(context) else null
        val dnd = if (DndSoft.active(context)) DndSoft.label(context) else null
        val airplane = if (AirplaneSoft.active(context)) AirplaneSoft.label(context) else null
        val saver = if (SaverSoft.active(context)) SaverSoft.label(context) else null
        val screen = if (ScreenOffSoft.active(context)) ScreenOffSoft.label(context) else null
        val hotspot = if (HotspotSoft.active(context)) HotspotSoft.label(context) else null
        val lock = if (LockSoft.active(context)) LockSoft.label(context) else null
        val silent = if (SilentSoft.active(context)) SilentSoft.label(context) else null
        val vpn = if (VpnSoft.active(context)) VpnSoft.label(context) else null
        val wired = if (WiredSoft.active(context)) WiredSoft.label(context) else null
        val bits = listOfNotNull(
            "$slot · $holdBit · $sceneBit · $dose · $next · $net · $codec",
            ear, talk, door, street, find, hear, duck, pocket, shake, flip, charge, speaker, unplug, batt, hscap, replug, acl, phonecap, mic, idle, session, car, alarm, route, peak, ring, morning, comm, evening, night, restored, jump, lunch, weekend, metered, dnd, airplane, saver, screen, hotspot, lock, silent, vpn, wired
        )
        return bits.joinToString(" · ")
    }
}
