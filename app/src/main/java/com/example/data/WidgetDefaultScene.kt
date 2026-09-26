package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** 1-tap default scene voor widgets (lang indrukken pin = default). */
object WidgetDefaultScene {
    private const val PREFS = "sounmax_widget_default"
    private const val KEY = "scene_id"

    fun id(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
            ?.takeIf { it.isNotBlank() }

    fun scene(context: Context): ListeningScene? = id(context)?.let { SceneLookup.byId(it) }

    fun set(context: Context, sceneId: String?) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        if (sceneId.isNullOrBlank()) prefs.remove(KEY) else prefs.putString(KEY, sceneId)
        prefs.apply()
        FavoriteScenes(context).pin(sceneId)
    }

    fun clear(context: Context) = set(context, null)

    fun toggle(context: Context, sceneId: String?): String? {
        val current = id(context)
        val next = if (current == sceneId) null else sceneId
        set(context, next)
        return next
    }

    fun label(context: Context): String =
        scene(context)?.let { "1-tap: ${it.emoji} ${it.name}" } ?: "1-tap: laatste scene"

    /** Pin de huidige last_scene als 1-tap default. */
    fun pinCurrent(context: Context): String? {
        val id = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .getString("last_scene_id", null)
        if (id.isNullOrBlank()) return null
        set(context, id)
        return id
    }
}
