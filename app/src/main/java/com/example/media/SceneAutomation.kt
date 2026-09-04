package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.dsp.HearingCorrection
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
        var scene = ListeningScenes.byId(prefs.getString("last_scene_id", null)) ?: return
        if (scene.id == "recap") {
            scene = RecentScenes.lastReal(prefs) ?: scene
        }
        apply(scene)
    }

    fun writeSuggested(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean("scene_locked", false)) return
        if (!prefs.getBoolean("auto_scene", true)) return
        ContentSceneAdvisor.restore(prefs)
        val weekDose = weekDoseMinutes(prefs)
        var scene = WeatherAdvisor.suggest(context, ListeningScenes.suggestedNow(weekDose))
        scene = ContentSceneAdvisor.adjust(scene)
        val battery = prefs.getInt("last_battery", -1).takeIf { it in 0..100 }
        scene = BatteryPowerAdvisor.adjust(context, scene, battery)
        ContentSceneAdvisor.remember(prefs)
        WeeklyDose.remember(prefs, weekDose)
        RecentScenes.push(prefs, scene.id)
        prefs.edit()
            .putString("last_scene_id", scene.id)
            .putBoolean("pending_widget_scene", true)
            .putString("dose_label", WeeklyDose.label(weekDose))
            .apply()
        QuietHours.enforce(context)
        tickListeningDose(prefs)
        maybeAutoSafeVolume(prefs)
        maybeSuggestEarBreak(context, prefs)
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
    }

    private fun tickListeningDose(prefs: android.content.SharedPreferences) {
        val cal = java.util.Calendar.getInstance()
        val key = "dose_${cal.get(java.util.Calendar.YEAR)}_${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
        prefs.edit().putInt(key, prefs.getInt(key, 0) + 60).apply()
    }

    private fun maybeSuggestEarBreak(context: Context, prefs: android.content.SharedPreferences) {
        if (prefs.getBoolean("scene_locked", false)) return
        val started = prefs.getLong("session_started_at", 0L)
        val lastBreak = prefs.getLong("last_ear_break", 0L)
        val now = System.currentTimeMillis()
        if (started == 0L || now - started < 50 * 60_000L) return
        if (now - lastBreak < 50 * 60_000L) return
        val rest = ListeningScenes.byId("rest") ?: return
        prefs.edit()
            .putString("last_scene_id", rest.id)
            .putBoolean("pending_widget_scene", true)
            .putLong("last_ear_break", now)
            .apply()
    }

    private fun maybeAutoSafeVolume(prefs: android.content.SharedPreferences) {
        val cal = java.util.Calendar.getInstance()
        val key = "dose_${cal.get(java.util.Calendar.YEAR)}_${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
        val today = prefs.getInt(key, 0)
        if (today >= 180 && !prefs.getBoolean("safe_volume", false)) {
            prefs.edit().putBoolean("safe_volume", true).apply()
        }
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

    private fun weekDoseMinutes(prefs: android.content.SharedPreferences): Int {
        val cal = java.util.Calendar.getInstance()
        var total = 0
        repeat(7) {
            total += prefs.getInt("dose_${cal.get(java.util.Calendar.YEAR)}_${cal.get(java.util.Calendar.DAY_OF_YEAR)}", 0)
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        return total
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
        HearingCorrection.markPending(context)
        try {
            DspControlService.start(context)
        } catch (_: Exception) {
        }
        SoundMaxWidget.refreshAll(context)
    }
}
