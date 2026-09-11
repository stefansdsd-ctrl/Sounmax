package com.example.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/** Nachts 03:00 lokale snapshot + optioneel Drive/SAF-map. */
class NightlyBackupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != NightlyBackup.ACTION) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                NightlyBackup.runNow(context)
            } finally {
                pending.finish()
                NightlyBackup.schedule(context)
            }
        }
    }
}

object NightlyBackup {
    const val ACTION = "com.example.action.NIGHTLY_BACKUP"
    private const val PREFS = "sounmax_backup"
    private const val KEY_ON = "nightly_on"
    private const val KEY_LAST = "nightly_last_ms"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ON, on).apply()
        if (on) schedule(context) else cancel(context)
    }

    fun lastRunMs(context: Context): Long =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_LAST, 0L)

    suspend fun runNow(context: Context) {
        if (!isEnabled(context)) return
        PresetBackup.writeLocalOnly(context)
        if (PresetBackup.treeUri(context) != null) {
            runCatching { PresetBackup.exportToTreeSilent(context) }
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong(KEY_LAST, System.currentTimeMillis()).apply()
    }

    fun schedule(context: Context) {
        if (!isEnabled(context)) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 3)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        val trigger = cal.timeInMillis - System.currentTimeMillis()
        am.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + trigger.coerceAtLeast(60_000L),
            pending(context)
        )
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending(context))
    }

    private fun pending(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context, 31,
            Intent(context, NightlyBackupReceiver::class.java).setAction(ACTION),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
}
