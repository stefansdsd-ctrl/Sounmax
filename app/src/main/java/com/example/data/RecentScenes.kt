package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Cycle door recente scenes (zonder huidige). */
object RecentScenes {
    fun list(context: Context, limit: Int = 4): List<ListeningScene> {
        val hist = SceneHistory(context)
        val cur = hist.current()
        return hist.ids()
            .filter { it != cur }
            .mapNotNull { SceneLookup.byId(it) }
            .distinctBy { it.id }
            .take(limit)
    }

    fun next(context: Context, activeId: String?): ListeningScene? {
        val scenes = list(context, 6)
        if (scenes.isEmpty()) return SceneHistory(context).previousScene()
        val idx = scenes.indexOfFirst { it.id == activeId }
        return scenes[(idx + 1).coerceAtLeast(0) % scenes.size]
    }

    fun label(context: Context): String? {
        val first = list(context, 1).firstOrNull() ?: return null
        return "Recent: ${first.name}"
    }
}
