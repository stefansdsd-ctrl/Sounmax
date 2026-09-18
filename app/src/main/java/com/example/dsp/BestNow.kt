package com.example.dsp

import android.content.Context
import com.example.data.FavoriteScenes
import com.example.data.SceneUsage
import com.example.media.HourSceneSuggest
import java.util.Calendar

/** Combineert usage, uur, favorieten, weekend en telefoon-accu tot “beste nu”. */
object BestNow {
    fun ranked(context: Context, limit: Int = 5): List<ListeningScene> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val hourHint = HourSceneSuggest.suggest(context)?.id
        val favs = runCatching { FavoriteScenes(context).ids().toSet() }.getOrDefault(emptySet())
        val batt = runCatching { PhoneBattery.percent(context) }.getOrDefault(100)
        return SceneLookup.ALL
            .map { scene ->
                val uses = SceneUsage.count(context, scene.id)
                val last = context.getSharedPreferences("sounmax_scene_usage", Context.MODE_PRIVATE)
                    .getLong("last_${scene.id}", 0L)
                val recency = if (last == 0L) 0 else ((System.currentTimeMillis() - last) / 3_600_000L).toInt().let { h ->
                    when {
                        h < 2 -> 40
                        h < 24 -> 20
                        h < 72 -> 8
                        else -> 0
                    }
                }
                val dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val weekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
                val score = uses * 3 +
                    recency +
                    (if (scene.id in favs) 25 else 0) +
                    (if (scene.id == hourHint) 30 else 0) +
                    hourBias(hour, scene.id) +
                    weekendBias(weekend, scene.id) +
                    batteryBias(batt, scene.id)
                scene to score
            }
            .sortedByDescending { it.second }
            .map { it.first }
            .distinctBy { it.id }
            .take(limit)
    }

    fun top(context: Context): ListeningScene? = ranked(context, 1).firstOrNull()

    fun label(context: Context): String? =
        top(context)?.let { "Beste nu: ${it.emoji} ${it.name}" }

    private fun hourBias(hour: Int, id: String): Int = when {
        hour in 6..8 && id in setOf("commute", "train", "metro", "windfietsplus", "platformrush", "rainbikeplus") -> 12
        hour in 9..17 && id in setOf("openplanplus", "focus", "office", "examhall", "libraryplus", "zoomclass") -> 10
        hour in 17..20 && id in setOf("gympeak", "cafechat", "kitchensteam", "traffichold") -> 10
        hour in 22..23 || hour < 6 && id in setOf("hearrest", "earfatigue", "night", "sleep", "latefocus", "sleepwind") -> 14
        else -> 0
    }

    private fun weekendBias(weekend: Boolean, id: String): Int {
        if (!weekend) return 0
        return if (id in setOf("themepark", "fairground", "cafechat", "rainwalkplus", "concertpit", "sundayreset")) 8 else 0
    }

    private fun batteryBias(percent: Int, id: String): Int = when {
        percent <= 10 && id in setOf("saver", "batterysaveplus", "sleep", "rest") -> 40
        percent <= 20 && id in setOf("saver", "batterysaveplus") -> 28
        percent <= 20 && id in setOf("gympeak", "party", "festival", "concertpit") -> -12
        else -> 0
    }
}
