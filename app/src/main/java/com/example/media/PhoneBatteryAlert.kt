package com.example.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.R

/**
 * Telefoonaccu ≤15% en niet aan de lader: één melding per 3 uur.
 * Naast LowBatteryAncSaver (headset).
 */
object PhoneBatteryAlert {
    private const val CHANNEL = "sounmax_phone_battery"
    private const val PREFS = "sounmax_phone_batt"
    private const val KEY_TS = "last_alert_ts"
    private const val COOLDOWN_MS = 3 * 60 * 60 * 1000L
    const val OFF_THRESHOLD = 15

    fun tick(context: Context) {
        if (!PhoneBatteryAdvisor.enabled(context)) return
        val pct = PhoneBatteryAdvisor.level(context) ?: return
        if (pct > OFF_THRESHOLD) return
        if (PhoneBatteryAdvisor.charging(context)) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (now - prefs.getLong(KEY_TS, 0L) < COOLDOWN_MS) return
        prefs.edit().putLong(KEY_TS, now).apply()
        notify(context, pct)
        Toast.makeText(context, "Telefoon $pct% — LDAC 330 / saver", Toast.LENGTH_SHORT).show()
    }

    private fun notify(context: Context, pct: Int) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Telefoonaccu", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Telefoonaccu laag")
            .setContentText("Telefoon $pct% — codec naar 330 kbps, saver-scene.")
            .setAutoCancel(true)
            .build()
        nm.notify(4218, n)
    }
}
