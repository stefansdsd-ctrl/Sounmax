package com.example.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.dsp.ListeningScenes
import com.example.widget.SoundMaxWidget
import java.util.Calendar

/**
 * Na 50 min ononderbroken luisteren: oorpauze-suggestie + melding.
 */
object EarBreakWatch {
    private const val PREFS = "soundmax_wellness"
    private const val KEY_SESSION = "session_started_at"
    private const val KEY_LAST_NAG = "last_ear_break_nag"
    private const val KEY_AUTO = "auto_ear_break"
    private const val BREAK_AFTER_MS = 50 * 60_000L
    private const val CHANNEL_ID = "sounmax_wellness"
    private const val NOTIF_ID = 6520

    fun tick(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_AUTO, true)) return
        val started = prefs.getLong(KEY_SESSION, 0L)
        if (started <= 0L) return
        val now = System.currentTimeMillis()
        if (now - started < BREAK_AFTER_MS) return
        val lastNag = prefs.getLong(KEY_LAST_NAG, 0L)
        if (now - lastNag < BREAK_AFTER_MS) return
        prefs.edit().putLong(KEY_LAST_NAG, now).apply()
        val rest = ListeningScenes.byId("rest") ?: return
        if (prefs.getBoolean("scene_locked", false)) return
        prefs.edit()
            .putString("last_scene_id", rest.id)
            .putBoolean("pending_widget_scene", true)
            .putBoolean("safe_volume", true)
            .putLong("last_ear_break", now)
            .putLong(KEY_SESSION, now)
            .apply()
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        notify(context)
        try {
            Toast.makeText(context, "Oorpauze — 50 min luisteren", Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
        }
        bumpDose(prefs)
    }

    private fun notify(context: Context) {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Gehoor", NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
            val open = PendingIntent.getActivity(
                context, 20,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val n = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Oorpauze")
                .setContentText("50 min luisteren — scene Rust + veilig volume")
                .setContentIntent(open)
                .setAutoCancel(true)
                .build()
            nm.notify(NOTIF_ID, n)
        } catch (_: Exception) {
        }
    }

    private fun bumpDose(prefs: android.content.SharedPreferences) {
        val cal = Calendar.getInstance()
        val key = "dose_${cal.get(Calendar.YEAR)}_${cal.get(Calendar.DAY_OF_YEAR)}"
        val today = prefs.getInt(key, 0)
        prefs.edit().putInt(key, today + 50).apply()
    }
}
