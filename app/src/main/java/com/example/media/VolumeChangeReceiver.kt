package com.example.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

class VolumeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != "android.media.VOLUME_CHANGED_ACTION") return
        val stream = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
        if (stream != AudioManager.STREAM_MUSIC && stream != -1) return
        val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("adaptive_volume", true)) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
        QuietHours.enforce(context)
    }
}
