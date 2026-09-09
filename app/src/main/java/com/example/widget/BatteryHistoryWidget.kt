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
import com.example.media.BatteryEta

class BatteryHistoryWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { update(context, appWidgetManager, it) }
    }

    companion object {
        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, BatteryHistoryWidget::class.java))
            ids.forEach { update(context, mgr, it) }
        }

        private fun update(context: Context, mgr: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.battery_history_widget)
            val samples = BatteryEta.samples(context)
            val last = samples.lastOrNull()?.second
            val eta = BatteryEta.label(BatteryEta.etaMinutes(context, last))
            val spark = sparkline(samples.map { it.second })
            views.setTextViewText(
                R.id.batt_title,
                buildString {
                    append("Accu")
                    last?.let { append(" $it%") }
                    eta?.let { append(" · $it") }
                }
            )
            views.setTextViewText(
                R.id.batt_spark,
                if (spark.isBlank()) "Nog geen historie" else spark
            )
            val open = PendingIntent.getActivity(
                context, 90,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.batt_root, open)
            mgr.updateAppWidget(id, views)
        }

        private fun sparkline(values: List<Int>): String {
            if (values.isEmpty()) return ""
            val chars = charArrayOf('▁', '▂', '▃', '▄', '▅', '▆', '▇', '█')
            val min = values.min()
            val max = values.max().coerceAtLeast(min + 1)
            return values.takeLast(24).joinToString("") { v ->
                val idx = ((v - min) * (chars.size - 1) / (max - min)).coerceIn(0, chars.lastIndex)
                chars[idx].toString()
            }
        }
    }
}
