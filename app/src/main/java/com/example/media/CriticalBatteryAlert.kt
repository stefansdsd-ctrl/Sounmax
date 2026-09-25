package com.example.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.widget.BatteryHistoryWidget
import com.example.widget.HearingDoseWidget

/** Accu ≤ 12% → melding, max 1x per 4 uur. Onafhankelijk van avond-ChargeReminder. */
object CriticalBatteryAlert {
    private const val CHANNEL = "sounmax_batt_crit"
    private const val KEY_TS = "crit_batt_ts"
    private const val COOLDOWN_MS = 4 * 60 * 60 * 1000L
    const val THRESHOLD = 12

    fun tick(context: Context, batteryPercent: Int?) {
        if (batteryPercent == null || batteryPercent !in 0..THRESHOLD) return
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (now - prefs.getLong(KEY_TS, 0L) < COOLDOWN_MS) return
        prefs.edit().putLong(KEY_TS, now).apply()
        notify(context, batteryPercent)
        runCatching { BatteryHistoryWidget.refreshAll(context) }
        runCatching { HearingDoseWidget.refreshAll(context) }
    }

    private fun notify(context: Context, pct: Int) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Kritieke accu", NotificationManager.IMPORTANCE_HIGH)
            )
        }
        val open = PendingIntent.getActivity(
            context, 4218,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Headset bijna leeg")
            .setContentText("Accu $pct% — spaarmodus / laad op.")
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        nm.notify(4218, n)
    }
}
