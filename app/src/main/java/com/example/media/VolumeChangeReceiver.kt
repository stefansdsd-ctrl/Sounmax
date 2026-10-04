package com.example.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.example.data.AirplaneSoft
import com.example.data.BookSoft
import com.example.data.CamSoft
import com.example.data.FoodSoft
import com.example.data.HealthSoft
import com.example.data.WeatherSoft
import com.example.data.RideSoft
import com.example.data.SleepSoft
import com.example.data.ShopSoft
import com.example.data.NewsSoft
import com.example.data.CarCap
import com.example.data.IdleCap
import com.example.data.LowHsCap
import com.example.data.MicBusy
import com.example.data.NavSoft
import com.example.data.NetSoft
import com.example.data.OfficeSoft
import com.example.data.PhoneLowCap
import com.example.data.PodcastSoft
import com.example.data.SessionCap
import com.example.data.StudySoft
import com.example.data.SocialSoft
import com.example.data.VoiceNoteSoft
import com.example.data.VideoSoft
import com.example.data.SportSoft
import com.example.data.GameSoft
import com.example.data.TransitSoft
import com.example.data.PaySoft
import com.example.data.KidSoft
import com.example.data.MeetSoft
import com.example.data.MailSoft
import com.example.data.ChargeNightCap
import com.example.data.DndSoft
import com.example.data.EveningSoft
import com.example.data.HearingGuard
import com.example.data.HotspotSoft
import com.example.data.JumpGuard
import com.example.data.LockSoft
import com.example.data.LunchSoft
import com.example.data.MeteredSoft
import com.example.data.MorningSoft
import com.example.data.NightSoft
import com.example.data.PeakCap
import com.example.data.SaverSoft
import com.example.data.ScreenOffSoft
import com.example.data.SilentSoft
import com.example.data.VpnSoft
import com.example.data.WeekendSoft
import com.example.data.WiredSoft

class VolumeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != "android.media.VOLUME_CHANGED_ACTION") return
        val stream = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
        if (stream != AudioManager.STREAM_MUSIC && stream != -1) return
        val app = context.applicationContext
        HearingGuard.applyCap(app)
        JumpGuard.apply(app)
        PeakCap.apply(app)
        MorningSoft.apply(app)
        LunchSoft.apply(app)
        EveningSoft.apply(app)
        NightSoft.apply(app)
        WeekendSoft.apply(app)
        MeteredSoft.apply(app)
        DndSoft.apply(app)
        AirplaneSoft.apply(app)
        SaverSoft.apply(app)
        ScreenOffSoft.apply(app)
        HotspotSoft.apply(app)
        LockSoft.apply(app)
        SilentSoft.apply(app)
        VpnSoft.apply(app)
        WiredSoft.apply(app)
        CarCap.apply(app)
        SessionCap.apply(app)
        IdleCap.apply(app)
        MicBusy.apply(app)
        PhoneLowCap.apply(app)
        LowHsCap.apply(app)
        OfficeSoft.apply(app)
        PodcastSoft.apply(app)
        VideoSoft.apply(app)
        StudySoft.apply(app)
        SocialSoft.apply(app)
        VoiceNoteSoft.apply(app)
        NavSoft.apply(app)
        NetSoft.apply(app)
        SportSoft.apply(app)
        GameSoft.apply(app)
        TransitSoft.apply(app)
        PaySoft.apply(app)
        KidSoft.apply(app)
        MeetSoft.apply(app)
        MailSoft.apply(app)
        ShopSoft.apply(app)
        NewsSoft.apply(app)
        BookSoft.apply(app)
        CamSoft.apply(app)
        SleepSoft.apply(app)
        FoodSoft.apply(app)
        WeatherSoft.apply(app)
        RideSoft.apply(app)
        HealthSoft.apply(app)
        ChargeNightCap.apply(app)
        val prefs = app.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("adaptive_volume", true)) return
        val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val ratio = cur.toFloat() / max
        val target = when {
            ratio < 0.25f -> 520
            ratio < 0.45f -> 360
            ratio < 0.70f -> 220
            else -> 60
        }
        prefs.edit()
            .putInt("adaptive_loudness_target", target)
            .putBoolean("pending_adaptive_volume", true)
            .apply()
        QuietHours.enforce(app)
        SafeVolume.enforce(app)
        OutdoorSafetyAdvisor.enforceVolume(app)
    }
}
