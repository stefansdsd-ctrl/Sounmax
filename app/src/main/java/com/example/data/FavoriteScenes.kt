package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Max 4 pinned scene-id's. Long-press pin/unpin. */
class FavoriteScenes(context: Context) {
    private val prefs = context.getSharedPreferences("sounmax_fav_scenes", Context.MODE_PRIVATE)

    fun ids(): List<String> =
        prefs.getString("ids", "")
            .orEmpty()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .take(MAX)

    fun scenes(): List<ListeningScene> = ids().mapNotNull { SceneLookup.byId(it) }

    fun isPinned(id: String?): Boolean {
        val s = id?.takeIf { it.isNotBlank() } ?: return false
        return s in ids()
    }

    fun toggle(id: String?): List<String> {
        val s = id?.takeIf { it.isNotBlank() } ?: return ids()
        val list = ids().toMutableList()
        if (s in list) list.remove(s) else if (list.size < MAX) list.add(s)
        persist(list)
        return list
    }

    fun pin(id: String?) {
        val s = id?.takeIf { it.isNotBlank() } ?: return
        val list = ids().toMutableList()
        if (s !in list && list.size < MAX) {
            list.add(s)
            persist(list)
        }
    }

    fun unpin(id: String?) {
        val s = id ?: return
        persist(ids().filter { it != s })
    }

    fun nextAfter(currentId: String?): ListeningScene? {
        val all = scenes()
        if (all.isEmpty()) return null
        val idx = all.indexOfFirst { it.id == currentId }
        return all[(idx + 1).coerceAtLeast(0) % all.size]
    }

    private fun persist(list: List<String>) {
        prefs.edit().putString("ids", list.take(MAX).joinToString(",")).apply()
    }

    companion object {
        const val MAX = 4
    }
}
