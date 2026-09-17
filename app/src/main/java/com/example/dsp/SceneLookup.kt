package com.example.dsp

object SceneLookup {
    val ALL: List<ListeningScene> =
        (ListeningScenes.ALL + ExtraListeningScenes.ALL + NewNlScenes.ALL +
            Batch69Scenes.ALL + Batch70Scenes.ALL + Batch71Scenes.ALL + Batch72Scenes.ALL)
            .distinctBy { it.id }

    fun byId(id: String?): ListeningScene? =
        if (id.isNullOrBlank()) null else ALL.firstOrNull { it.id == id }
}
