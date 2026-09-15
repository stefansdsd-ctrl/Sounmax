package com.example.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Alert als headset verbonden blijft maar RSSI lang zwak is (vergeten / te ver).
 */
object LeftBehind {
    private const val PREFS = "soundmax_wellness"
    const val KEY_ENABLED = "left_behind"
    private const val CHANNEL = "sounmax_left_behind"
    private const val NOTIF_ID = 6519
    private const val WEAK_DBM = -86
    private const val HOLD_MS = 25_000L
    private var weakSince = 0L
    private var fired = false

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun tick(context: Context, rssi: Int, connected: Boolean) {
        if (!enabled(context)) return
        if (!connected || rssi == 0) {
            weakSince = 0L
            fired = false
            return
        }
        val now = System.currentTimeMillis()
        if (rssi <= WEAK_DBM) {
            if (weakSince == 0L) weakSince = now
            if (!fired && now - weakSince >= HOLD_MS) {
                notify(context, rssi)
                fired = true
            }
        } else {
            weakSince = 0L
            fired = false
        }
    }

    fun label(context: Context): String =
        if (enabled(context)) "Vergeten-alert aan" else "Vergeten-alert uit"

    private fun notify(context: Context, rssi: Int) {
        ensureChannel(context)
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Sounmax · headset ver weg")
            .setContentText("Zwak signaal (${rssi} dBm). Check of je hem niet vergeet.")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID, n)
        } catch (_: SecurityException) {
        }
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (mgr.getNotificationChannel(CHANNEL) != null) return
        mgr.createNotificationChannel(
            NotificationChannel(CHANNEL, "Headset vergeten", NotificationManager.IMPORTANCE_DEFAULT)
        )
    }
}
