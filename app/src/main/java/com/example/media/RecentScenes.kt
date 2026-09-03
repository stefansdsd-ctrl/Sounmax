package com.example.media

import android.content.SharedPreferences
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes

object RecentScenes {
    private const val KEY = "recent_scene_ids"
    private const val MAX = 8

    fun push(prefs: SharedPreferences, id: String?) {
        if (id.isNullOrBlank() || id == "recap") return
        val next = listOf(id) + list(prefs).filter { it != id }
        prefs.edit().putString(KEY, next.take(MAX).joinToString(",")).apply()
    }

    fun list(prefs: SharedPreferences? = null): List<String> {
        val raw = prefs?.getString(KEY, null) ?: return emptyList()
        return raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun scenes(prefs: SharedPreferences): List<ListeningScene> =
        list(prefs).mapNotNull { ListeningScenes.byId(it) }

    fun lastReal(prefs: SharedPreferences, except: String? = "recap"): ListeningScene? =
        list(prefs).firstOrNull { it != except }?.let { ListeningScenes.byId(it) }
}
