package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/** Telt scene-gebruik per uur en per dag. */
object SceneUsage {
    private const val PREFS = "sounmax_scene_usage"

    fun record(context: Context, sceneId: String?) {
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val total = prefs.getInt("n_$id", 0) + 1
        val hourCount = prefs.getInt("h_${hour}_$id", 0) + 1
        prefs.edit()
            .putInt("n_$id", total)
            .putInt("h_${hour}_$id", hourCount)
            .putLong("last_$id", System.currentTimeMillis())
            .apply()
    }

    fun count(context: Context, sceneId: String): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("n_$sceneId", 0)

    fun top(context: Context, limit: Int = 5): List<ListeningScene> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.all.entries
            .filter { it.key.startsWith("n_") && it.value is Int }
            .sortedByDescending { it.value as Int }
            .mapNotNull { SceneLookup.byId(it.key.removePrefix("n_")) }
            .take(limit)
    }

    fun recent(context: Context, limit: Int = 8): List<ListeningScene> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.all.entries
            .filter { it.key.startsWith("last_") && it.value is Long }
            .sortedByDescending { it.value as Long }
            .mapNotNull { SceneLookup.byId(it.key.removePrefix("last_")) }
            .take(limit)
    }

    fun recencyScore(context: Context, sceneId: String): Int {
        val last = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong("last_$sceneId", 0L)
        if (last == 0L) return 0
        val ageH = ((System.currentTimeMillis() - last) / 3_600_000L).toInt()
        return when {
            ageH < 2 -> 40
            ageH < 24 -> 18
            ageH < 72 -> 8
            else -> 0
        }
    }

    fun suggestNow(context: Context): ListeningScene? {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val best = prefs.all.entries
            .filter { it.key.startsWith("h_${hour}_") && it.value is Int }
            .maxByOrNull { it.value as Int }
            ?.key
            ?.removePrefix("h_${hour}_")
        return SceneLookup.byId(best) ?: top(context, 1).firstOrNull()
    }
}
