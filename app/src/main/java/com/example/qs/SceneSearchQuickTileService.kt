package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.SceneLookup
import com.example.dsp.SceneSearch

/** QS-tegel: cyclus door laatste zoekresultaat of favorieten. */
class SceneSearchQuickTileService : TileService() {
    override fun onStartListening() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val q = prefs.getString(KEY_QUERY, "") ?: ""
        val id = prefs.getString("last_scene_id", "focus")
        val scene = SceneLookup.byId(id)
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = scene?.name ?: "Zoek scene"
            subtitle = if (q.isBlank()) "fav / zoek" else "zoek: $q"
            updateTile()
        }
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val q = prefs.getString(KEY_QUERY, "") ?: ""
        val favs = (prefs.getString("fav_scenes", "") ?: "")
            .split(',').map { it.trim() }.filter { it.isNotBlank() }.toSet()
        val pool = if (q.isNotBlank()) {
            SceneSearch.query(q, favs)
        } else {
            favs.mapNotNull { SceneLookup.byId(it) }.ifEmpty { SceneLookup.ALL }
        }
        if (pool.isEmpty()) return
        val current = prefs.getString("last_scene_id", null)
        val idx = pool.indexOfFirst { it.id == current }.let { if (it < 0) 0 else it }
        val next = pool[(idx + 1) % pool.size]
        prefs.edit()
            .putString("prev_scene_id", current)
            .putString("last_scene_id", next.id)
            .putBoolean("pending_widget_scene", true)
            .apply()
        sendBroadcast(
            Intent(SceneQuickTileService.ACTION_CYCLE_SCENE)
                .setPackage(packageName)
                .putExtra("scene_id", next.id)
        )
        qsTile?.apply {
            label = next.name
            subtitle = next.emoji
            updateTile()
        }
    }

    companion object {
        const val KEY_QUERY = "qs_scene_search"
    }
}
