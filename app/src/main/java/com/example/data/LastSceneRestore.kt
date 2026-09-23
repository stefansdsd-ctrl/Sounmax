package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Herstel de laatst gekozen scene na app-start. */
object LastSceneRestore {
    fun scene(context: Context): ListeningScene? {
        val id = SceneHistory(context).current() ?: return null
        return SceneLookup.byId(id)
    }

    fun label(context: Context): String? =
        scene(context)?.let { "Laatst: ${it.emoji} ${it.name}" }
}
