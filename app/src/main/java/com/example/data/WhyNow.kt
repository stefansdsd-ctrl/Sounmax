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
        OfficeSoft.apply(context)
        PodcastSoft.apply(context)
        VideoSoft.apply(context)
        StudySoft.apply(context)
        SocialSoft.apply(context)
        VoiceNoteSoft.apply(context)
        NavSoft.apply(context)
        NetSoft.apply(context)
        SportSoft.apply(context)
        GameSoft.apply(context)
        TransitSoft.apply(context)
        PaySoft.apply(context)
        KidSoft.apply(context)
        MeetSoft.apply(context)
        MailSoft.apply(context)
        ShopSoft.apply(context)
        NewsSoft.apply(context)
        BookSoft.apply(context)
        CamSoft.apply(context)
        SleepSoft.apply(context)
        FoodSoft.apply(context)
        WeatherSoft.apply(context)
        RideSoft.apply(context)
        HealthSoft.apply(context)
        ParcelSoft.apply(context)
        ClassSoft.apply(context)
        LanguageSoft.apply(context)
        RadioSoft.apply(context)
        TranslateSoft.apply(context)
        RecorderSoft.apply(context)
        HearSoft.apply(context)
        RunWind.apply(context)
        BikeWind.apply(context)
        WalkSafe.apply(context)
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
        val office = if (OfficeSoft.active(context)) OfficeSoft.label(context) else null
        val podcast = if (PodcastSoft.active(context)) PodcastSoft.label(context) else null
        val video = if (VideoSoft.active(context)) VideoSoft.label(context) else null
        val study = if (StudySoft.active(context)) StudySoft.label(context) else null
        val social = if (SocialSoft.active(context)) SocialSoft.label(context) else null
        val voice = if (VoiceNoteSoft.active(context)) VoiceNoteSoft.label(context) else null
        val nav = if (NavSoft.active(context)) NavSoft.label(context) else null
        val offl = if (NetSoft.active(context)) NetSoft.label(context) else null
        val sport = if (SportSoft.active(context)) SportSoft.label(context) else null
        val gamecap = if (GameSoft.active(context)) GameSoft.label(context) else null
        val ov = if (TransitSoft.active(context)) TransitSoft.label(context) else null
        val pay = if (PaySoft.active(context)) PaySoft.label(context) else null
        val kid = if (KidSoft.active(context)) KidSoft.label(context) else null
        val meet = if (MeetSoft.active(context)) MeetSoft.label(context) else null
        val mail = if (MailSoft.active(context)) MailSoft.label(context) else null
        val shop = if (ShopSoft.active(context)) ShopSoft.label(context) else null
        val news = if (NewsSoft.active(context)) NewsSoft.label(context) else null
        val book = if (BookSoft.active(context)) BookSoft.label(context) else null
        val cam = if (CamSoft.active(context)) CamSoft.label(context) else null
        val sleep = if (SleepSoft.active(context)) SleepSoft.label(context) else null
        val food = if (FoodSoft.active(context)) FoodSoft.label(context) else null
        val weather = if (WeatherSoft.active(context)) WeatherSoft.label(context) else null
        val ride = if (RideSoft.active(context)) RideSoft.label(context) else null
        val health = if (HealthSoft.active(context)) HealthSoft.label(context) else null
        val parcel = if (ParcelSoft.active(context)) ParcelSoft.label(context) else null
        val klass = if (ClassSoft.active(context)) ClassSoft.label(context) else null
        val lang = if (LanguageSoft.active(context)) LanguageSoft.label(context) else null
        val radio = if (RadioSoft.active(context)) RadioSoft.label(context) else null
        val tr = if (TranslateSoft.active(context)) TranslateSoft.label(context) else null
        val rec = if (RecorderSoft.active(context)) RecorderSoft.label(context) else null
        val hearCap = if (HearSoft.active(context)) HearSoft.label(context) else null
        val runCap = if (RunWind.active(context)) RunWind.label(context) else null
        val bikeCap = if (BikeWind.active(context)) BikeWind.label(context) else null
        val walkCap = if (WalkSafe.active(context)) WalkSafe.label(context) else null
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
            ear, talk, door, street, find, hear, duck, pocket, shake, flip, charge, speaker, unplug, batt, hscap, replug, acl, phonecap, mic, idle, session, car, office, podcast, video, study, social, voice, nav, offl, sport, gamecap, ov, pay, kid, meet, mail, shop, news, book, cam, sleep, food, weather, ride, health, parcel, klass, lang, radio, tr, rec, hearCap, runCap, bikeCap, walkCap, alarm, route, peak, ring, morning, comm, evening, night, restored, jump, lunch, weekend, metered, dnd, airplane, saver, screen, hotspot, lock, silent, vpn, wired
        )
        return bits.joinToString(" · ")
    }
}
