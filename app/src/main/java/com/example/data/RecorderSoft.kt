package com.example.data

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.session.MediaSessionManager
import android.os.Process
import com.example.media.SoundMaxNotificationListener

/**
 * Voice Recorder / Google Recorder / Easy Voice Recorder actief: boven 64% → 46%, stapsgewijs.
 * Terugluisteren blijft verstaanbaar. Geen overlap met Spraakberichten of Meet.
 */
object RecorderSoft {
    private const val PREFS = "sounmax_recorder_soft"
    private const val CAP_PCT = 46
    private const val TRIGGER_PCT = 64

    private val packages = setOf(
        "com.google.android.apps.recorder",
        "com.sec.android.app.voicenote",
        "com.coffeebeanventures.easyvoicerecorder",
        "com.coffeebeanventures.easyvoicerecorderpro",
        "com.splendapps.voicerec",
        "com.media.bestrecorder.audiorecorder",
        "com.andrwq.recorder",
        "com.sonymobile.androidapp.voicerecorder",
        "com.htc.soundrecorder",
        "com.android.soundrecorder"
    )

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun recorderActive(context: Context): Boolean {
        if (viaPlayback(context)) return true
        if (viaSession(context)) return true
        return foreground(context)
    }

    private fun viaSession(context: Context): Boolean {
        return try {
            val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val cn = ComponentName(context, SoundMaxNotificationListener::class.java)
            msm.getActiveSessions(cn).any { it.packageName in packages }
        } catch (_: Exception) {
            false
        }
    }

    private fun viaPlayback(context: Context): Boolean {
        return try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val pm = context.packageManager
            am.activePlaybackConfigurations.any { cfg ->
                cfg.playerState == AudioPlaybackConfiguration.PLAYER_STATE_STARTED &&
                    (pm.getPackagesForUid(cfg.clientUid) ?: emptyArray()).any { it in packages }
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun foreground(context: Context): Boolean {
        if (!usageGranted(context)) return false
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val end = System.currentTimeMillis()
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, end - 60_000, end)
            val top = stats.maxByOrNull { it.lastTimeUsed }?.packageName
            top in packages
        } catch (_: Exception) {
            false
        }
    }

    private fun usageGranted(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= 29) {
            ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            @Suppress("DEPRECATION")
            ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !recorderActive(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - 2).coerceAtLeast(cap), 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && recorderActive(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Opname-cap uit"
        recorderActive(context) -> "Opname-cap (46%)"
        else -> "Opname-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
