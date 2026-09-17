package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc
import com.example.widget.SoundMaxWidget

/** 25 min focus + 5 min oorpauze, gekoppeld aan de Pomodoro-scene. */
object PomodoroSession {
    const val ACTION_END = "com.example.ACTION_POMO_END"
    const val ACTION_BREAK_END = "com.example.ACTION_POMO_BREAK_END"
    const val PREFS = SceneAutomation.PREFS
    const val KEY_END = "pomo_end_at"
    const val KEY_ACTIVE = "pomo_active"
    const val KEY_BREAK = "pomo_break"
    const val WORK_MIN = 25
    const val BREAK_MIN = 5

    fun isActive(context: Context): Boolean {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val end = p.getLong(KEY_END, 0L)
        return p.getBoolean(KEY_ACTIVE, false) && end > System.currentTimeMillis()
    }

    fun isBreak(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_BREAK, false)

    fun remainingMs(context: Context): Long {
        val end = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_END, 0L)
        return (end - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun remainingLabel(context: Context): String {
        val ms = remainingMs(context)
        val m = (ms / 60_000L).toInt()
        val s = ((ms % 60_000L) / 1000L).toInt()
        val prefix = if (isBreak(context)) "Pauze" else "Focus"
        return "$prefix %d:%02d".format(m, s)
    }

    fun toggle(context: Context) {
        if (isActive(context)) cancel(context) else startWork(context)
    }

    fun startWork(context: Context) {
        arm(context, WORK_MIN, breakPhase = false, action = ACTION_END)
        SceneLookup.byId("pomodoro")?.let { scene ->
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString("last_scene_id", scene.id)
                .putBoolean("pending_widget_scene", true)
                .putBoolean("scene_locked", true)
                .apply()
        }
        SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
        OneTapProfiles.apply(context, "pomodoro")
        DndFocusFilter.enter(context)
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        Toast.makeText(context, "Pomodoro $WORK_MIN min", Toast.LENGTH_SHORT).show()
    }

    fun startBreak(context: Context) {
        arm(context, BREAK_MIN, breakPhase = true, action = ACTION_BREAK_END)
        SceneLookup.byId("microbreak")?.let { scene ->
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString("last_scene_id", scene.id)
                .putBoolean("pending_widget_scene", true)
                .putBoolean("scene_locked", false)
                .apply()
        }
        SoftwareAnc.applyWithHardware(context, AncMode.OFF)
        DndFocusFilter.exit(context)
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        Toast.makeText(context, "Oorpauze $BREAK_MIN min", Toast.LENGTH_SHORT).show()
    }

    fun cancel(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit().putBoolean(KEY_ACTIVE, false).putBoolean(KEY_BREAK, false).putLong(KEY_END, 0L)
            .putBoolean("scene_locked", false).apply()
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.cancel(pending(context, ACTION_END, 71))
        am.cancel(pending(context, ACTION_BREAK_END, 72))
        DndFocusFilter.exit(context)
        SoundMaxWidget.refreshAll(context)
        Toast.makeText(context, "Pomodoro uit", Toast.LENGTH_SHORT).show()
    }

    private fun arm(context: Context, minutes: Int, breakPhase: Boolean, action: String) {
        val end = System.currentTimeMillis() + minutes * 60_000L
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ACTIVE, true)
            .putBoolean(KEY_BREAK, breakPhase)
            .putLong(KEY_END, end)
            .apply()
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        val req = if (breakPhase) 72 else 71
        val pi = pending(context, action, req)
        am.cancel(pi)
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, end, pi)
    }

    private fun pending(context: Context, action: String, req: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            req,
            Intent(context, PomodoroEndReceiver::class.java).setAction(action),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
}

class PomodoroEndReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            PomodoroSession.ACTION_END -> PomodoroSession.startBreak(context)
            PomodoroSession.ACTION_BREAK_END -> PomodoroSession.startWork(context)
        }
    }
}
