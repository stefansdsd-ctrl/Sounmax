package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.AudioDspManager
import com.example.widget.SoundMaxWidget
import kotlinx.coroutines.delay

object SleepFade {
    const val ACTION_FADE = "com.example.DSP_SLEEP_FADE"
    const val KEY_AFTER_TRACK = "sleep_after_track"
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
            waitForTrackEndIfNeeded(context)
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
            dsp?.setAncMode(AncMode.OFF)
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

    private suspend fun waitForTrackEndIfNeeded(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_AFTER_TRACK, false)) return
        val startTitle = currentTitle(context)
        val deadline = System.currentTimeMillis() + 8 * 60_000L
        while (System.currentTimeMillis() < deadline) {
            val left = remainingMs(context)
            if (left in 1 until 9_000) break
            val title = currentTitle(context)
            if (startTitle != null && title != null && title != startTitle) break
            if (!MediaRemote.isMusicActive(context)) break
            delay(1_200)
        }
    }

    private fun session(context: Context) = try {
        val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val cn = ComponentName(context, SoundMaxNotificationListener::class.java)
        msm.getActiveSessions(cn).firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: msm.getActiveSessions(cn).firstOrNull()
    } catch (_: Exception) {
        null
    }

    private fun currentTitle(context: Context): String? {
        val md = session(context)?.metadata ?: return null
        return md.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: md.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
    }

    private fun remainingMs(context: Context): Long {
        val c = session(context) ?: return -1L
        val dur = c.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val pos = c.playbackState?.position ?: -1L
        if (dur <= 0L || pos < 0L) return -1L
        return (dur - pos).coerceAtLeast(0L)
    }
}
