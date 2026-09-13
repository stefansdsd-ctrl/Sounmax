package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dsp.ConversationBoost
import com.example.dsp.SceneLookup
import com.example.widget.SoundMaxWidget

object MeetingSession {
    const val ACTION_END = "com.example.ACTION_MEETING_END"
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ACTIVE = "meeting_active"
    const val KEY_END = "meeting_end_at"
    const val KEY_PREV = "meeting_prev_scene"
    const val KEY_TALK_BEFORE = "meeting_talk_before"
    const val KEY_MINUTES = "meeting_minutes"

    fun lastMinutes(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_MINUTES, 30)
            .coerceIn(10, 120)

    fun isActive(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val end = prefs.getLong(KEY_END, 0L)
        return prefs.getBoolean(KEY_ACTIVE, false) && end > System.currentTimeMillis()
    }

    fun remainingMs(context: Context): Long {
        val end = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_END, 0L)
        return (end - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun start(context: Context, minutes: Int = 30, currentSceneId: String? = null) {
        val scene = SceneLookup.byId("meeting")
            ?: SceneLookup.byId("gesprek")
            ?: SceneLookup.byId("transparency")
            ?: return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val prev = currentSceneId ?: prefs.getString("last_scene_id", null)
        val end = System.currentTimeMillis() + minutes * 60_000L
        prefs.edit()
            .putBoolean(KEY_ACTIVE, true)
            .putLong(KEY_END, end)
            .putInt(KEY_MINUTES, minutes.coerceIn(10, 120))
            .putString(KEY_PREV, prev)
            .putBoolean(KEY_TALK_BEFORE, TalkThrough.enabled(context))
            .putBoolean("scene_locked", true)
            .putLong("manual_scene_until", end)
            .putString("last_scene_id", scene.id)
            .putBoolean("pending_widget_scene", true)
            .apply()
        ConversationBoost.apply(true)
        TalkThrough.setEnabled(context, true)
        TalkThrough.onRms(1f)
        MediaRemote.pause(context)
        scheduleEnd(context, end)
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        Toast.makeText(context, "Vergadering $minutes min", Toast.LENGTH_SHORT).show()
    }

    fun cancel(context: Context) = finish(context, cancelled = true)

    fun finish(context: Context, cancelled: Boolean = false) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val prev = prefs.getString(KEY_PREV, null)
        val talkBefore = prefs.getBoolean(KEY_TALK_BEFORE, false)
        prefs.edit()
            .putBoolean(KEY_ACTIVE, false)
            .putLong(KEY_END, 0L)
            .putBoolean("scene_locked", false)
            .putLong("manual_scene_until", 0L)
            .apply()
        TalkThrough.release()
        ConversationBoost.apply(false)
        if (!talkBefore) TalkThrough.setEnabled(context, false)
        prev?.let { id ->
            SceneLookup.byId(id)?.let { scene ->
                prefs.edit()
                    .putString("last_scene_id", scene.id)
                    .putBoolean("pending_widget_scene", true)
                    .apply()
            }
        }
        context.getSystemService(AlarmManager::class.java)?.cancel(pending(context))
        DspControlService.start(context)
        SoundMaxWidget.refreshAll(context)
        Toast.makeText(
            context,
            if (cancelled) "Vergadering uit" else "Vergadering klaar",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun toggle(context: Context, currentSceneId: String? = null) {
        if (isActive(context)) cancel(context) else start(context, lastMinutes(context), currentSceneId)
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
            31,
            Intent(context, MeetingEndReceiver::class.java).setAction(ACTION_END),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
}

class MeetingEndReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != MeetingSession.ACTION_END) return
        MeetingSession.finish(context)
    }
}
