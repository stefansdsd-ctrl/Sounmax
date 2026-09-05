package com.example.dsp

object SceneLookup {
    val ALL: List<ListeningScene> =
        (ListeningScenes.ALL + ExtraListeningScenes.ALL + NewNlScenes.ALL)
            .distinctBy { it.id }

    fun byId(id: String?): ListeningScene? =
        if (id.isNullOrBlank()) null else ALL.firstOrNull { it.id == id }
}
