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
import com.example.data.HearingGuard
import com.example.media.DailyHearingBudget

class HearingDoseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { update(context, appWidgetManager, it) }
    }

    companion object {
        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, HearingDoseWidget::class.java))
            ids.forEach { update(context, mgr, it) }
        }

        private fun update(context: Context, mgr: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.hearing_dose_widget)
            val min = DailyHearingBudget.todayMinutes(context)
            val pct = DailyHearingBudget.percent(context).coerceAtMost(100)
            val over = DailyHearingBudget.overCap(context) || HearingGuard.overDailyLimit(context)
            views.setTextViewText(
                R.id.dose_title,
                if (over) "LIMIET" else "Gehoordosis"
            )
            views.setTextViewText(
                R.id.dose_value,
                "$min / ${DailyHearingBudget.CAP_MIN} min · $pct%"
            )
            views.setTextViewText(
                R.id.dose_hint,
                when {
                    HearingGuard.overDailyLimit(context) -> "Daglimiet ${HearingGuard.minutesToday(context)}m / ${HearingGuard.dailyLimitMin(context)}m"
                    over -> "Rust tot middernacht · tap voor app"
                    else -> DailyHearingBudget.chipLabel(context)
                }
            )
            val open = PendingIntent.getActivity(
                context, 91,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.dose_root, open)
            mgr.updateAppWidget(id, views)
        }
    }
}
