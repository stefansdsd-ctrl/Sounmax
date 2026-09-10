package com.example.dsp

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
        return SceneLookup.ALL.filter { s ->
            s.id.contains(n) ||
                s.name.lowercase().contains(n) ||
                s.description.lowercase().contains(n) ||
                s.presetName.lowercase().contains(n) ||
                tagsFor(s.id).any { it.lowercase().contains(n) } ||
                (n == "fav" && s.id in favorites)
        }
    }
}
