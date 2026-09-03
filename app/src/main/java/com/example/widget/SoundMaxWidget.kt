package com.example.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.dsp.ListeningScenes
import com.example.media.DspControlService
import com.example.media.EarBreakWatch
import com.example.media.SleepFade
import com.example.media.WeatherAdvisor

class SoundMaxWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        scheduleTick(context)
    }

    override fun onDisabled(context: Context) {
        cancelTick(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        scheduleTick(context)
        appWidgetIds.forEach { updateWidget(context, appWidgetManager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_TOGGLE_DSP -> {
                val prefs = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
                val next = !prefs.getBoolean(DspControlService.KEY_DSP, true)
                prefs.edit().putBoolean(DspControlService.KEY_DSP, next).apply()
                DspControlService.start(context)
                refreshAll(context)
            }
            ACTION_NEXT_SCENE -> {
                cycleScene(context, +1)
                refreshAll(context)
            }
            ACTION_PREV_SCENE -> {
                cycleScene(context, -1)
                refreshAll(context)
            }
            ACTION_SUGGEST -> {
                applySuggested(context)
                refreshAll(context)
            }
            ACTION_CYCLE_SLEEP -> {
                cycleSleep(context)
                refreshAll(context)
            }
            ACTION_TICK, ACTION_REFRESH -> {
                EarBreakWatch.tick(context)
                refreshAll(context)
            }
        }
    }

    companion object {
        const val ACTION_TOGGLE_DSP = "com.example.widget.TOGGLE_DSP"
        const val ACTION_NEXT_SCENE = "com.example.widget.NEXT_SCENE"
        const val ACTION_PREV_SCENE = "com.example.widget.PREV_SCENE"
        const val ACTION_SUGGEST = "com.example.widget.SUGGEST"
        const val ACTION_CYCLE_SLEEP = "com.example.widget.CYCLE_SLEEP"
        const val ACTION_REFRESH = "com.example.widget.REFRESH"
        const val ACTION_TICK = "com.example.widget.TICK"
        private val SLEEP_STEPS = intArrayOf(0, 15, 30, 60, 90, 120)

        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, SoundMaxWidget::class.java))
            ids.forEach { updateWidget(context, mgr, it) }
        }

        fun cycleScene(context: Context, step: Int = 1) {
            val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            val favs = prefs.getString("fav_scenes", "")
                ?.split(',')
                ?.filter { it.isNotBlank() }
                .orEmpty()
            val pool = if (favs.size >= 2) {
                favs.mapNotNull { ListeningScenes.byId(it) }
            } else {
                ListeningScenes.ALL
            }
            val current = prefs.getString("last_scene_id", pool.first().id)
            val idx = pool.indexOfFirst { it.id == current }.let { if (it < 0) 0 else it }
            val size = pool.size.coerceAtLeast(1)
            val next = pool[((idx + step) % size + size) % size]
            prefs.edit().putString("last_scene_id", next.id).putBoolean("pending_widget_scene", true).apply()
            DspControlService.start(context)
        }

        fun applySuggested(context: Context) {
            val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            if (prefs.getBoolean("scene_locked", false)) return
            val scene = WeatherAdvisor.suggest(context, ListeningScenes.suggestedNow())
            prefs.edit()
                .putString("last_scene_id", scene.id)
                .putBoolean("pending_widget_scene", true)
                .apply()
            DspControlService.start(context)
        }

        fun cycleSleep(context: Context) {
            val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            val remaining = remainingSleepMinutes(prefs.getLong(KEY_SLEEP_END, 0L))
            val idx = SLEEP_STEPS.indexOfFirst { it >= remaining }.let { if (it < 0) 0 else it }
            val next = SLEEP_STEPS[(idx + 1) % SLEEP_STEPS.size]
            val end = if (next <= 0) 0L else System.currentTimeMillis() + next * 60_000L
            prefs.edit()
                .putLong(KEY_SLEEP_END, end)
                .putInt(KEY_SLEEP_MINUTES, next)
                .putBoolean("pending_widget_sleep", true)
                .apply()
            if (next <= 0) SleepFade.cancel(context) else SleepFade.schedule(context, end)
            DspControlService.start(context)
        }

        fun remainingSleepMinutes(endMs: Long): Int {
            if (endMs <= 0L) return 0
            val left = ((endMs - System.currentTimeMillis()) / 60_000L).toInt()
            return left.coerceAtLeast(0)
        }

        const val KEY_SLEEP_END = "sleep_end_ms"
        const val KEY_SLEEP_MINUTES = "sleep_minutes"
        const val KEY_BATTERY = "headset_battery"
        const val KEY_HEADSET_NAME = "headset_name"

        private fun tickIntent(context: Context): PendingIntent {
            return PendingIntent.getBroadcast(
                context, 9,
                Intent(context, SoundMaxWidget::class.java).setAction(ACTION_TICK),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun scheduleTick(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60_000L,
                60_000L,
                tickIntent(context)
            )
        }

        fun cancelTick(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(tickIntent(context))
        }

        private fun updateWidget(context: Context, mgr: AppWidgetManager, id: Int) {
            val ui = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
            val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            val enabled = ui.getBoolean(DspControlService.KEY_DSP, true)
            val scene = ListeningScenes.byId(wellness.getString("last_scene_id", null))
                ?: ListeningScenes.ALL.first()
            val suggested = WeatherAdvisor.suggest(context, ListeningScenes.suggestedNow())
            val battery = wellness.getInt(KEY_BATTERY, -1)
            val sleepLeft = remainingSleepMinutes(wellness.getLong(KEY_SLEEP_END, 0L))
            val name = wellness.getString(KEY_HEADSET_NAME, null)

            val views = RemoteViews(context.packageName, R.layout.soundmax_widget)
            views.setTextViewText(R.id.widget_title, name?.take(18) ?: "Sounmax")
            views.setTextViewText(
                R.id.widget_battery,
                if (battery in 0..100) "BT $battery%" else "BT --%"
            )
            views.setTextViewText(R.id.widget_dsp, if (enabled) "DSP aan" else "DSP uit")
            views.setTextViewText(R.id.widget_scene, "${scene.emoji} ${scene.name}")
            views.setTextViewText(
                R.id.widget_sleep,
                if (sleepLeft > 0) "Slaap ${sleepLeft} min" else "Tip ${suggested.emoji} ${suggested.name}"
            )

            val open = PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val toggle = PendingIntent.getBroadcast(
                context, 1,
                Intent(context, SoundMaxWidget::class.java).setAction(ACTION_TOGGLE_DSP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val nextScene = PendingIntent.getBroadcast(
                context, 2,
                Intent(context, SoundMaxWidget::class.java).setAction(ACTION_NEXT_SCENE),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val prevScene = PendingIntent.getBroadcast(
                context, 5,
                Intent(context, SoundMaxWidget::class.java).setAction(ACTION_PREV_SCENE),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val suggest = PendingIntent.getBroadcast(
                context, 7,
                Intent(context, SoundMaxWidget::class.java).setAction(ACTION_SUGGEST),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val sleep = PendingIntent.getBroadcast(
                context, 4,
                Intent(context, SoundMaxWidget::class.java).setAction(ACTION_CYCLE_SLEEP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_root, open)
            views.setOnClickPendingIntent(R.id.widget_dsp_btn, toggle)
            views.setOnClickPendingIntent(R.id.widget_scene_btn, nextScene)
            views.setOnClickPendingIntent(R.id.widget_prev_btn, prevScene)
            views.setOnClickPendingIntent(R.id.widget_sleep_btn, sleep)
            views.setOnClickPendingIntent(R.id.widget_suggest_btn, suggest)
            mgr.updateAppWidget(id, views)
        }
    }
}
