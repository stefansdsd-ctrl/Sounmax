package com.example.data

import android.content.Context
import java.util.Calendar

/** Compacte ochtend-samenvatting: dosis, holds, laatste scene. */
object MorningBriefing {
    fun text(context: Context, batteryPercent: Int?): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greet = when (hour) {
            in 5..11 -> "Goedemorgen"
            in 12..17 -> "Middag"
            in 18..22 -> "Avond"
            else -> "Nacht"
        }
        val today = WeeklyListenReport.last7Days(context).lastOrNull()?.minutes ?: 0
        val holds = HoldPanic.activeCount(context)
        val scene = LastSceneRestore.scene(context)?.name ?: "geen scene"
        val bat = batteryPercent?.let { "$it%" } ?: "—"
        val holdTxt = if (holds == 0) "geen holds" else "$holds hold(s)"
        return "$greet · $today min · $holdTxt · $scene · accu $bat"
    }
}
