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
 * Thuisbezorgd/Uber Eats/Deliveroo op de voorgrond of met geluid: boven 62% → 42%, in stapjes.
 * Bestelgeluid blijft hoorbaar. Geen overlap met Shop-cap.
 */
object FoodSoft {
    private const val PREFS = "sounmax_food_soft"
    private const val CAP_PCT = 42
    private const val TRIGGER_PCT = 62

    private val packages = setOf(
        "com.thuisbezorgd.takeaway",
        "nl.takeaway.android",
        "com.yourdelivery.thuisbezorgd",
        "com.ubercab.eats",
        "com.deliveroo.orderapp",
        "com.justeat.app.nl",
        "com.foodora.android",
        "com.gorillas.android"
    )

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    fun foodActive(context: Context): Boolean {
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
            val now = System.currentTimeMillis()
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 15_000, now)
            val top = stats.maxByOrNull { it.lastTimeUsed } ?: return false
            top.packageName in packages && now - top.lastTimeUsed < 8_000
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
        if (!enabled(context) || !foodActive(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            val step = (cur - 2).coerceAtLeast(cap)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, step, 0)
            prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && foodActive(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Eten-cap uit"
        foodActive(context) -> "Eten-cap (42%)"
        else -> "Eten-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
