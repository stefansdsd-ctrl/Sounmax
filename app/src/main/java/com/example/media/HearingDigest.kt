package com.example.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.data.WeeklyListenReport
import java.util.Calendar

/** Zondagse weeksamenvatting: uren, piekdag, advies. */
object HearingDigest {
    private const val PREFS = "soundmax_wellness"
    private const val KEY_LAST = "hearing_digest_week"
    private const val CHANNEL = "sounmax_hearing_digest"
    private const val NOTIF_ID = 6520

    fun maybeNotify(context: Context) {
        val cal = Calendar.getInstance()
        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) return
        if (cal.get(Calendar.HOUR_OF_DAY) < 18) return
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        val year = cal.get(Calendar.YEAR)
        val token = "${year}_$week"
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (p.getString(KEY_LAST, null) == token) return
        p.edit().putString(KEY_LAST, token).apply()
        notify(context, WeeklyListenReport.digest(context))
    }

    private fun notify(context: Context, text: String) {
        ensureChannel(context)
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Sounmax · week gehoor")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
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
            NotificationChannel(CHANNEL, "Gehoor weekrapport", NotificationManager.IMPORTANCE_LOW)
        )
    }
}
