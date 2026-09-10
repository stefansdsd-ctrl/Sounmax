package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Ringbuffer van de laatste 8 scene-id's. Persist via SharedPreferences. */
class SceneHistory(context: Context) {
    private val prefs = context.getSharedPreferences("sounmax_scene_history", Context.MODE_PRIVATE)

    fun record(sceneId: String?) {
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return
        val list = ids().toMutableList()
        list.remove(id)
        list.add(0, id)
        prefs.edit().putString("ids", list.take(8).joinToString(",")).apply()
        prefs.edit().putString("current", id).apply()
    }

    fun ids(): List<String> =
        prefs.getString("ids", "")
            .orEmpty()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    fun current(): String? = prefs.getString("current", null)

    fun previousId(): String? {
        val all = ids()
        val cur = current()
        return all.firstOrNull { it != cur } ?: all.getOrNull(1)
    }

    fun previousScene(): ListeningScene? = SceneLookup.byId(previousId())

    fun undo(): ListeningScene? {
        val prev = previousScene() ?: return null
        record(prev.id)
        return prev
    }
}
