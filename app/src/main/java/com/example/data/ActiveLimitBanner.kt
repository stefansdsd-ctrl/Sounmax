package com.example.data

import android.content.Context
import android.media.AudioManager
import com.example.media.ListenCap
import com.example.media.WeeklyDose

/** Eén zin waarom volume nu begrensd is. */
object ActiveLimitBanner {
    fun text(context: Context): String {
        SceneHoldPriority.winner(context)?.let { return SceneHoldPriority.label(context) }
        if (PostCallRamp.active(context)) return "Na-bel: volume bouwt op"
        if (EmergencyDuck.active(context)) return "Nood-duck actief (22%, NL-Alert hoorbaar)"
        if (AlarmDuck.active(context)) return "Alarm-duck actief (28%, wekker hoorbaar)"
        if (AccessDuck.active(context)) return "Toegang-duck actief (33%, TalkBack hoorbaar)"
        if (RingDuck.active(context)) return "Bel-duck actief (32%, beltoon hoorbaar)"
        if (TtsDuck.active(context)) return "TTS-duck actief (34%, voorlezen hoorbaar)"
        if (MeetingDuck.active(context)) return "Agenda-demp actief (35%, afspraak nadert)"
        if (TimerDuck.active(context)) return "Timer-duck actief (36%, piep hoorbaar)"
        if (WaitDuck.active(context)) return "Wacht-duck actief (38%, wachttoon hoorbaar)"
        if (NavDuck.active(context)) return "Nav-duck actief (42%, aanwijzing hoorbaar)"
        if (AssistDuck.active(context)) return "Assistent-duck actief (40%, stem hoorbaar)"
        if (SpeechDuck.active(context)) return "Spraak-duck actief (43%, audioboek hoorbaar)"
        if (ChatDuck.active(context)) return "Chat-duck actief (44%, bericht hoorbaar)"
        if (PingDuck.active(context)) return "Ping-duck actief (45%, melding hoorbaar)"
        if (MicLive.active(context)) return "Mic-cap actief (40%, andere app neemt op)"
        if (CamDuck.active(context)) return "Cam-duck actief (48%, sluiter hoorbaar)"
        if (GameDuck.active(context)) return "Game-duck actief (50%, spel hoorbaar)"
        if (UiDuck.active(context)) return "UI-duck actief (55%, tik hoorbaar)"
        if (HandoffDuck.active(context)) return "Wissel-duck actief (62%, nieuwe speler)"
        if (HuntDuck.active(context)) return "Zoek-cap actief (56%, snel skippen)"
        if (SkipDuck.active(context)) return "Skip-duck actief (64%, nummerwissel)"
        if (ResumeDuck.active(context)) return "Pauze-hervat actief (68%, na stilte)"
        if (MorningDuck.active(context)) return "Ochtend-cap actief (48%, eerste play van de dag)"
        if (MicRestore.active(context)) return "Mic-herstel: volume komt terug na opname"
        if (SpikeGuard.active(context)) return "Sprong gedempt (max +1 stap)"
        if (BootQuiet.active(context)) return "Start-cap actief (50%, vergeten luid volume)"
        if (RouteCap.active(context) && RouteCap.onSpeaker(context)) {
            return "Route-cap actief (45%, muziek op speaker)"
        }
        if (ThermalCap.active(context)) return "Warmte-cap actief (55%, telefoon warm)"
        if (PowerSaveCap.active(context)) return "Spaar-cap actief (60%, spaarstand)"
        if (LowBatteryCap.active(context)) return "Accu-cap actief (50%, accu ≤ 20%)"
        if (FocusQuietCap.active(context)) return "Focus-cap actief (55%, Niet storen)"
        if (SilentRingerCap.active(context)) return "Stil-cap actief (52%, beltoon tril/stil)"
        if (WifiVolumeMemory.active(context)) {
            val id = WifiVolumeMemory.ssid(context) ?: "Wi-Fi"
            return "Wi-Fi-volume actief ($id)"
        }
        if (NightQuietCap.active(context)) return "Nacht-cap actief (50%, 22:30–07:00)"
        if (MeteredCap.active(context) && MeteredCap.enabled(context)) {
            return "Data-cap actief (58% op mobiel)"
        }
        if (OfflineGuard.active(context)) return "Offline: AI lokaal, cloud uit"
        if (ListenCap.enabled(context)) {
            val days = WeeklyListenReport.last7Days(context)
            val weekMin = days.sumOf { it.minutes }
            val db = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                .getInt(WeeklyDose.KEY_DB, 80)
            if (WeeklyDose.exposureRatio(weekMin, db) >= 1.0) {
                return "Gehoorcap actief (weekdosis vol)"
            }
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val vol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        return "Geen limiet · volume ${(100 * vol) / max}%"
    }
}
