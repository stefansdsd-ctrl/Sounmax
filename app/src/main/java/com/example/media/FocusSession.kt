package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dsp.SceneLookup
import com.example.widget.SoundMaxWidget

/** 25-min focus: Deep work + scene-lock, daarna oorpauze. */
object FocusSession {
    const val ACTION_END = "com.example.ACTION_FOCUS_END"
    const val PREFS = SceneAutomation.PREFS
    const val KEY_END = "focus_end_at"
    const val KEY_ACTIVE = "focus_active"
    const val DURATION_MS = 25 * 60_000L

    fun isActive(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val end = prefs.getLong(KEY_END, 0L)
        return prefs.getBoolean(KEY_ACTIVE, false) && end > System.currentTimeMillis()
    }

    fun remainingMs(context: Context): Long {
        val end = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_END, 0L)
        return (end - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun start(context: Context, minutes: Int = 25) {
        val end = System.currentTimeMillis() + minutes * 60_000L
        val scene = SceneLookup.byId("deepwork") ?: SceneLookup.byId("focus") ?: return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_ACTIVE, true)
            .putLong(KEY_END, end)
            .putBoolean("scene_locked", true)
            .putString("last_scene_id", scene.id)
            .putBoolean("pending_widget_scene", true)
            .putLong("session_started_at", System.currentTimeMillis())
            .apply()
        scheduleEnd(context, end)
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        Toast.makeText(context, "Focus $minutes min · Deep work vergrendeld", Toast.LENGTH_SHORT).show()
    }

    fun cancel(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_ACTIVE, false)
            .putLong(KEY_END, 0L)
            .putBoolean("scene_locked", false)
            .apply()
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.cancel(pending(context))
        SoundMaxWidget.refreshAll(context)
    }

    fun finish(context: Context) {
        val rest = SceneLookup.byId("rest")
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_ACTIVE, false)
            .putLong(KEY_END, 0L)
            .putBoolean("scene_locked", false)
            .putLong("last_ear_break", System.currentTimeMillis())
            .apply()
        if (rest != null) {
            prefs.edit()
                .putString("last_scene_id", rest.id)
                .putBoolean("pending_widget_scene", true)
                .apply()
        }
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        Toast.makeText(context, "Focus klaar · oorpauze", Toast.LENGTH_LONG).show()
    }

    fun toggle(context: Context) {
        if (isActive(context)) cancel(context) else start(context)
    }

    private fun scheduleEnd(context: Context, endMs: Long) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        val pi = pending(context)
        am.cancel(pi)
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endMs, pi)
    }

    private fun pending(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            25,
            Intent(context, FocusEndReceiver::class.java).setAction(ACTION_END),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
}

class FocusEndReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != FocusSession.ACTION_END) return
        FocusSession.finish(context)
    }
}
