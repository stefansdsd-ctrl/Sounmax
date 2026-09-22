package com.example.dsp

import android.content.Context
import com.example.data.FavoriteScenes
import com.example.data.SceneUsage
import com.example.media.HourSceneSuggest
import com.example.media.ListenDose
import java.util.Calendar

/** Korte NL-reden waarom BestNow deze scene kiest. */
object WhyBestNow {
    fun explain(context: Context, scene: ListeningScene? = BestNow.top(context)): String? {
        val s = scene ?: return null
        val reasons = mutableListOf<String>()
        val hourHint = HourSceneSuggest.suggest(context)?.id
        if (s.id == hourHint) reasons += "past bij dit uur"
        val favs = runCatching { FavoriteScenes(context).ids() }.getOrDefault(emptyList())
        if (s.id in favs) reasons += "favoriet"
        if (SceneUsage.count(context, s.id) >= 3) reasons += "vaak gebruikt"
        val last = context.getSharedPreferences("sounmax_scene_usage", Context.MODE_PRIVATE)
            .getLong("last_${s.id}", 0L)
        if (last > 0 && System.currentTimeMillis() - last < 2 * 3_600_000L) reasons += "net gebruikt"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour in 22..23 || hour < 6) reasons += "nacht"
        if (ListenDose.shouldPause(context) && s.id in setOf("oorpauze", "earfatigue", "sleep", "hearrest")) {
            reasons += "gehoorpauze"
        }
        val batt = runCatching { PhoneBattery.percent(context) }.getOrDefault(100)
        if (batt <= 20 && s.id in setOf("saver", "batterysaveplus", "oorpauze", "sleep")) {
            reasons += "lage accu"
        }
        if (reasons.isEmpty()) reasons += "beste match nu"
        return "${s.emoji} ${s.name} · ${reasons.take(2).joinToString(", ")}"
    }
}
