package com.example.media

import android.content.SharedPreferences

/** Onthoud laatste scene per media-app. */
object AppSceneMemory {
    private const val PREFIX = "app_scene_"

    fun remember(prefs: SharedPreferences, packageName: String?, sceneId: String?) {
        val pkg = packageName?.takeIf { it.isNotBlank() } ?: return
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return
        prefs.edit().putString(PREFIX + pkg, id).apply()
    }

    fun recall(prefs: SharedPreferences, packageName: String?): String? {
        val pkg = packageName?.takeIf { it.isNotBlank() } ?: return null
        return prefs.getString(PREFIX + pkg, null)
    }
}
