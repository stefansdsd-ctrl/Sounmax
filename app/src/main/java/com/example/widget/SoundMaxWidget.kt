package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.dsp.ListeningScenes
import com.example.media.DspControlService

class SoundMaxWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
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
                cycleScene(context)
                refreshAll(context)
            }
            ACTION_REFRESH -> refreshAll(context)
        }
    }

    companion object {
        const val ACTION_TOGGLE_DSP = "com.example.widget.TOGGLE_DSP"
        const val ACTION_NEXT_SCENE = "com.example.widget.NEXT_SCENE"
        const val ACTION_REFRESH = "com.example.widget.REFRESH"

        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, SoundMaxWidget::class.java))
            ids.forEach { updateWidget(context, mgr, it) }
        }

        fun cycleScene(context: Context) {
            val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            val current = prefs.getString("last_scene_id", ListeningScenes.ALL.first().id)
            val idx = ListeningScenes.ALL.indexOfFirst { it.id == current }.coerceAtLeast(0)
            val next = ListeningScenes.ALL[(idx + 1) % ListeningScenes.ALL.size]
            prefs.edit().putString("last_scene_id", next.id).putBoolean("pending_widget_scene", true).apply()
            DspControlService.start(context)
        }

        private fun updateWidget(context: Context, mgr: AppWidgetManager, id: Int) {
            val ui = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
            val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            val enabled = ui.getBoolean(DspControlService.KEY_DSP, true)
            val scene = ListeningScenes.byId(wellness.getString("last_scene_id", null))
                ?: ListeningScenes.ALL.first()

            val views = RemoteViews(context.packageName, R.layout.soundmax_widget)
            views.setTextViewText(R.id.widget_title, "Sounmax")
            views.setTextViewText(R.id.widget_dsp, if (enabled) "DSP aan" else "DSP uit")
            views.setTextViewText(R.id.widget_scene, "${scene.emoji} ${scene.name}")

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
            views.setOnClickPendingIntent(R.id.widget_root, open)
            views.setOnClickPendingIntent(R.id.widget_dsp_btn, toggle)
            views.setOnClickPendingIntent(R.id.widget_scene_btn, nextScene)
            mgr.updateAppWidget(id, views)
        }
    }
}
