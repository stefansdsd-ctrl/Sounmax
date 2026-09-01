package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.widget.Toast
import com.example.dsp.AudioDspManager
import com.example.widget.SoundMaxWidget
import kotlinx.coroutines.delay

object SleepFade {
    const val ACTION_FADE = "com.example.DSP_SLEEP_FADE"
    private const val PREFS = "soundmax_wellness"
    private const val KEY_SAVED_VOL = "sleep_saved_stream_vol"

    @Volatile
    private var running = false

    fun schedule(context: Context, endMs: Long) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        val pi = pending(context)
        am.cancel(pi)
        if (endMs <= 0L) return
        val fadeStart = (endMs - 60_000L).coerceAtLeast(System.currentTimeMillis() + 2_000L)
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fadeStart, pi)
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.cancel(pending(context))
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putLong(SoundMaxWidget.KEY_SLEEP_END, 0L)
            .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, 0)
            .apply()
    }

    private fun pending(context: Context): PendingIntent =
        PendingIntent.getService(
            context,
            42,
            Intent(context, DspControlService::class.java).setAction(ACTION_FADE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    suspend fun run(context: Context, dsp: AudioDspManager? = null) {
        if (running) return
        running = true
        try {
            val am = context.getSystemService(AudioManager::class.java) ?: return
            val startVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putInt(KEY_SAVED_VOL, startVol).apply()
            val startLoud = dsp?.loudnessGain?.value ?: 0
            val startBass = dsp?.bassBoostStrength?.value ?: 0
            for (step in 8 downTo 0) {
                val vol = (startVol * step / 8f).toInt()
                try {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0)
                } catch (_: Exception) {
                }
                dsp?.setLoudness((startLoud * step / 8f).toInt())
                dsp?.setBassBoost((startBass * step / 8f).toInt())
                delay(7_500)
            }
            MediaRemote.pause(context)
            dsp?.setDspEnabled(false)
            dsp?.setLoudness(startLoud)
            dsp?.setBassBoost(startBass)
            delay(400)
            try {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, startVol, 0)
            } catch (_: Exception) {
            }
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putLong(SoundMaxWidget.KEY_SLEEP_END, 0L)
                .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, 0)
                .apply()
            Toast.makeText(context, "Slaaptimer: volume uitgefead, media gepauzeerd", Toast.LENGTH_LONG).show()
        } finally {
            running = false
        }
    }
}
