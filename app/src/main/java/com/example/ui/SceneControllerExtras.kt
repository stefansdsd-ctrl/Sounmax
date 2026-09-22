package com.example.ui

import com.example.dsp.AncMode

fun SceneController.applyFavoriteNext() {
    favoriteScenes().let { list ->
        if (list.isEmpty()) return
        val cur = activeSceneId.value
        val idx = list.indexOfFirst { it.id == cur }
        applyListeningScene(list[(idx + 1).coerceAtLeast(0) % list.size])
    }
}

fun SceneController.favoriteNextLabel(): String? {
    val list = favoriteScenes()
    if (list.isEmpty()) return null
    val cur = activeSceneId.value
    val idx = list.indexOfFirst { it.id == cur }
    val next = list[(idx + 1).coerceAtLeast(0) % list.size]
    return "Fav: ${next.name}"
}

fun SceneController.cycleAncMode() {
    val order = listOf(AncMode.STRONG, AncMode.ADAPTIVE, AncMode.WIND_GUARD, AncMode.AMBIENT)
    val prefs = getApplication().getSharedPreferences("scene_auto", android.content.Context.MODE_PRIVATE)
    val cur = prefs.getString("last_anc_cycle", AncMode.ADAPTIVE.name)
    val idx = order.indexOfFirst { it.name == cur }.let { if (it < 0) 0 else (it + 1) % order.size }
    val next = order[idx]
    prefs.edit().putString("last_anc_cycle", next.name).apply()
    setHardwareAnc(next)
}

fun SceneController.ancCycleLabel(): String {
    val prefs = getApplication().getSharedPreferences("scene_auto", android.content.Context.MODE_PRIVATE)
    val cur = prefs.getString("last_anc_cycle", AncMode.ADAPTIVE.name) ?: AncMode.ADAPTIVE.name
    return "ANC: ${cur.lowercase()}"
}

private fun SceneController.getApplication(): android.app.Application =
    throw UnsupportedOperationException()
