package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class FavoriteScenesWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { update(context, appWidgetManager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == SoundMaxWidget.ACTION_APPLY_SCENE) {
            SoundMaxWidget.applyScene(context, intent.getStringExtra(SoundMaxWidget.EXTRA_SCENE_ID))
            refreshAll(context)
            SoundMaxWidget.refreshAll(context)
        }
    }

    companion object {
        private val SLOT_IDS = intArrayOf(
            R.id.fav_slot_0, R.id.fav_slot_1, R.id.fav_slot_2, R.id.fav_slot_3
        )

        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, FavoriteScenesWidget::class.java))
            ids.forEach { update(context, mgr, it) }
        }

        private fun update(context: Context, mgr: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.favorite_scenes_widget)
            val favs = SoundMaxWidget.favoriteScenes(context).take(4)
            views.setTextViewText(R.id.fav_title, "Favorieten")
            SLOT_IDS.forEachIndexed { i, viewId ->
                if (i < favs.size) {
                    val scene = favs[i]
                    views.setViewVisibility(viewId, View.VISIBLE)
                    views.setTextViewText(viewId, "${scene.emoji} ${scene.name}")
                    val pi = PendingIntent.getBroadcast(
                        context, 40 + i,
                        Intent(context, FavoriteScenesWidget::class.java)
                            .setAction(SoundMaxWidget.ACTION_APPLY_SCENE)
                            .putExtra(SoundMaxWidget.EXTRA_SCENE_ID, scene.id),
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    views.setOnClickPendingIntent(viewId, pi)
                } else {
                    views.setViewVisibility(viewId, View.GONE)
                }
            }
            val open = PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.fav_root, open)
            mgr.updateAppWidget(id, views)
        }
    }
}
