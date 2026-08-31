package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.example.widget.SoundMaxWidget

object SceneAutomation {
    const val PREFS = "soundmax_wellness"
    const val ACTION_HOURLY = "com.example.ACTION_SCENE_HOURLY"

    fun consumePending(context: Context, apply: (ListeningScene) -> Unit) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("pending_widget_scene", false)) return
        prefs.edit().putBoolean("pending_widget_scene", false).apply()
        val scene = ListeningScenes.byId(prefs.getString("last_scene_id", null)) ?: return
        apply(scene)
    }

    fun writeSuggested(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean("scene_locked", false)) return
        if (!prefs.getBoolean("auto_scene", true)) return
        val scene = WeatherAdvisor.suggest(context, ListeningScenes.suggestedNow())
        prefs.edit()
            .putString("last_scene_id", scene.id)
            .putBoolean("pending_widget_scene", true)
            .apply()
        QuietHours.enforce(context)
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
    }

    fun scheduleHourly(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            context, 17,
            Intent(context, SceneHourlyReceiver::class.java).setAction(ACTION_HOURLY),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        am.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 60_000L,
            AlarmManager.INTERVAL_HOUR,
            pi
        )
    }
}

class SceneHourlyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        QuietHours.enforce(context)
        SceneAutomation.writeSuggested(context)
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED &&
            intent?.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return
        SceneAutomation.scheduleHourly(context)
        QuietHours.enforce(context)
        try {
            DspControlService.start(context)
        } catch (_: Exception) {
        }
        SoundMaxWidget.refreshAll(context)
    }
}
