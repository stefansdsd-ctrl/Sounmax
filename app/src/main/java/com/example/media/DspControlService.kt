package com.example.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.dsp.ListeningScenes
import com.example.wear.WearBridge
import com.example.widget.SoundMaxWidget

class DspControlService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val wellness = getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        when (intent?.action) {
            ACTION_TOGGLE -> {
                val next = !prefs.getBoolean(KEY_DSP, true)
                prefs.edit().putBoolean(KEY_DSP, next).apply()
            }
            ACTION_NEXT_SCENE -> SoundMaxWidget.cycleScene(this, +1)
            ACTION_PREV_SCENE -> SoundMaxWidget.cycleScene(this, -1)
            ACTION_CYCLE_SLEEP -> SoundMaxWidget.cycleSleep(this)
            ACTION_SUGGEST -> SoundMaxWidget.applySuggested(this)
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        ensureChannel()
        val enabled = prefs.getBoolean(KEY_DSP, true)
        val scene = ListeningScenes.byId(wellness.getString("last_scene_id", null))
        val sceneLabel = scene?.let { "${it.emoji} ${it.name}" } ?: "Scene"
        val suggested = ListeningScenes.suggestedNow()
        val battery = wellness.getInt(SoundMaxWidget.KEY_BATTERY, -1)
        val sleepLeft = SoundMaxWidget.remainingSleepMinutes(wellness.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L))
        val extra = buildString {
            if (battery in 0..100) append(" · BT $battery%")
            if (sleepLeft > 0) append(" · slaap $sleepLeft min")
            if (suggested.id != scene?.id) append(" · tip ${suggested.emoji}")
        }
        val openApp = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val toggle = PendingIntent.getService(
            this, 1,
            Intent(this, DspControlService::class.java).setAction(ACTION_TOGGLE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextScene = PendingIntent.getService(
            this, 3,
            Intent(this, DspControlService::class.java).setAction(ACTION_NEXT_SCENE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val prevScene = PendingIntent.getService(
            this, 5,
            Intent(this, DspControlService::class.java).setAction(ACTION_PREV_SCENE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val suggest = PendingIntent.getService(
            this, 6,
            Intent(this, DspControlService::class.java).setAction(ACTION_SUGGEST),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val sleep = PendingIntent.getService(
            this, 4,
            Intent(this, DspControlService::class.java).setAction(ACTION_CYCLE_SLEEP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stop = PendingIntent.getService(
            this, 2,
            Intent(this, DspControlService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Sounmax DSP")
            .setContentText(
                if (enabled) "Aan · $sceneLabel$extra"
                else "Uit · $sceneLabel$extra"
            )
            .setContentIntent(openApp)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(0, if (enabled) "Pauzeer" else "Start", toggle)
            .addAction(0, "◀", prevScene)
            .addAction(0, "Scene", nextScene)
            .addAction(0, "Nu ${suggested.emoji}", suggest)
            .addAction(0, if (sleepLeft > 0) "Timer $sleepLeft" else "Timer", sleep)
            .addAction(0, "Sluit", stop)
            .build()
        startForeground(NOTIF_ID, notification)
        try {
            SoundMaxWidget.refreshAll(this)
        } catch (_: Exception) {
        }
        WearBridge.publishStatus(this)
        return START_STICKY
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "DSP-bediening", NotificationManager.IMPORTANCE_LOW)
        )
    }

    companion object {
        const val CHANNEL_ID = "sounmax_dsp"
        const val NOTIF_ID = 6519
        const val ACTION_TOGGLE = "com.example.DSP_TOGGLE"
        const val ACTION_NEXT_SCENE = "com.example.DSP_NEXT_SCENE"
        const val ACTION_PREV_SCENE = "com.example.DSP_PREV_SCENE"
        const val ACTION_CYCLE_SLEEP = "com.example.DSP_CYCLE_SLEEP"
        const val ACTION_SUGGEST = "com.example.DSP_SUGGEST"
        const val ACTION_STOP = "com.example.DSP_STOP"
        const val PREFS = "soundmax_ui"
        const val KEY_DSP = "notif_dsp_enabled"

        fun start(context: Context) {
            val i = Intent(context, DspControlService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(i)
            } else {
                context.startService(i)
            }
        }
    }
}
