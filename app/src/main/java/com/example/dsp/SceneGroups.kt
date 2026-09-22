package com.example.dsp

/** UI-groepen inclusief extra scenes. */
object SceneGroups {
    val LABELS: List<Pair<String, Set<String>>> = listOf(
        "Alles" to emptySet(),
        "Favorieten" to emptySet(),
        "Nieuw" to setOf(
            "maxancnu", "gespreknu", "windfietsplus", "stilconcert",
            "regenspitsfiets", "nsstoringperron", "videobellenbuiten", "babyinslaap",
            "kookwekker", "burenboor", "picknickwind", "autowegfile"
        )
    )

    fun ids(group: String): Set<String>? =
        LABELS.firstOrNull { it.first == group }?.second

    fun idsFor(group: String): Set<String> = ids(group) ?: emptySet()
}
