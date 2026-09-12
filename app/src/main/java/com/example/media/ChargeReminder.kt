package com.example.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R
import java.util.Calendar

/** Accu < 25% na 21:00, niet aan lader → één melding per avond. */
object ChargeReminder {
    private const val CHANNEL = "sounmax_charge"
    private const val KEY_LAST_DAY = "charge_remind_day"

    fun tick(context: Context, batteryPercent: Int?, charging: Boolean = false) {
        if (batteryPercent == null || batteryPercent >= 25) return
        if (charging) return
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour < 21) return
        val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        if (prefs.getInt(KEY_LAST_DAY, -1) == day) return
        prefs.edit().putInt(KEY_LAST_DAY, day).apply()
        notify(context, batteryPercent)
    }

    private fun notify(context: Context, pct: Int) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Opladen", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Headset opladen")
            .setContentText("Accu $pct% — sluit vannacht aan de lader.")
            .setAutoCancel(true)
            .build()
        nm.notify(4217, n)
    }
}
