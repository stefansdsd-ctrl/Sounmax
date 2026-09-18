package com.example.dsp

import android.content.Context
import com.example.data.SceneUsage

/** Zoek + tags over alle luister-scenes. */
object SceneSearch {
    fun tagsFor(id: String): List<String> =
        SceneGroups.LABELS
            .filter { it.second.contains(id) }
            .map { it.first }
            .filter { it != "Alles" && it != "Favorieten" }

    fun query(q: String, favorites: Set<String> = emptySet()): List<ListeningScene> {
        val n = q.trim().lowercase()
        if (n.isEmpty()) return SceneLookup.ALL
        val aliases = ALIASES[n].orEmpty()
        return SceneLookup.ALL.filter { s ->
            s.id.contains(n) ||
                s.id in aliases ||
                s.name.lowercase().contains(n) ||
                s.description.lowercase().contains(n) ||
                s.presetName.lowercase().contains(n) ||
                tagsFor(s.id).any { it.lowercase().contains(n) } ||
                (n == "fav" && s.id in favorites)
        }
    }

    private val ALIASES = mapOf(
        "efteling" to setOf("themepark"),
        "walibi" to setOf("themepark"),
        "zwembad" to setOf("poolreverb", "pool"),
        "kermis" to setOf("fairground"),
        "intratuin" to setOf("gardencenter"),
        "gamma" to setOf("diyhall"),
        "praxis" to setOf("diyhall"),
        "karwei" to setOf("diyhall"),
        "kringloop" to setOf("thriftshop"),
        "overstap" to setOf("transferhub", "ovoverstap"),
        "nachtbus" to setOf("nightbusplus", "nightbus")
    )

    fun queryRanked(context: Context, q: String, favorites: Set<String> = emptySet()): List<ListeningScene> {
        val hits = query(q, favorites)
        return hits.sortedByDescending { s ->
            val fav = if (s.id in favorites) 1000 else 0
            fav + SceneUsage.count(context, s.id)
        }
    }
}
