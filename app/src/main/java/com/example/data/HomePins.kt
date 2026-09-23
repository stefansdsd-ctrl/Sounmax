package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Vier vastgezette scenes voor de home-balk. */
object HomePins {
    private const val PREFS = "sounmax_home_pins"
    private const val KEY = "ids"

    fun ids(context: Context): List<String> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "focus,commute,night,oorpauze") ?: return emptyList()
        return raw.split(',').map { it.trim() }.filter { it.isNotBlank() }.distinct().take(4)
    }

    fun scenes(context: Context): List<ListeningScene> =
        ids(context).mapNotNull { SceneLookup.byId(it) }

    fun isPinned(context: Context, sceneId: String): Boolean = sceneId in ids(context)

    fun toggle(context: Context, sceneId: String): List<String> {
        val cur = ids(context).toMutableList()
        if (sceneId in cur) cur.remove(sceneId) else {
            if (cur.size >= 4) cur.removeAt(0)
            cur.add(sceneId)
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, cur.joinToString(",")).apply()
        return cur
    }

    fun next(context: Context, currentId: String?): ListeningScene? {
        val list = scenes(context)
        if (list.isEmpty()) return null
        val idx = list.indexOfFirst { it.id == currentId }
        return list[(idx + 1).coerceAtLeast(0) % list.size]
    }

    fun move(context: Context, sceneId: String, delta: Int): List<String> {
        val cur = ids(context).toMutableList()
        val i = cur.indexOf(sceneId)
        if (i < 0) return cur
        val j = (i + delta).coerceIn(0, cur.lastIndex)
        if (i == j) return cur
        val item = cur.removeAt(i)
        cur.add(j, item)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, cur.joinToString(",")).apply()
        return cur
    }

    fun label(context: Context): String? {
        val first = scenes(context).firstOrNull() ?: return null
        return "Pin: ${first.emoji} ${first.name}"
    }
}
