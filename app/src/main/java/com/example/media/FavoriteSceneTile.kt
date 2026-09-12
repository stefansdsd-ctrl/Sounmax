package com.example.media

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.data.FavoriteScenes
import com.example.dsp.SceneLookup

/** Quick Settings: tik = volgende favoriete scene. */
class FavoriteSceneTile : TileService() {
    override fun onStartListening() {
        refresh()
    }

    override fun onClick() {
        val fav = FavoriteScenes(applicationContext)
        val prefs = getSharedPreferences("sounmax_scene_history", MODE_PRIVATE)
        val current = prefs.getString("current", null)
        val next = fav.nextAfter(current)
        if (next != null) {
            prefs.edit().putString("current", next.id).apply()
            getSharedPreferences("sounmax_qs_scene", MODE_PRIVATE)
                .edit().putString("apply_id", next.id).apply()
            qsTile?.label = next.title
            qsTile?.state = Tile.STATE_ACTIVE
            qsTile?.updateTile()
        } else {
            qsTile?.label = "Geen pins"
            qsTile?.state = Tile.STATE_INACTIVE
            qsTile?.updateTile()
        }
    }

    private fun refresh() {
        val fav = FavoriteScenes(applicationContext)
        val current = getSharedPreferences("sounmax_scene_history", MODE_PRIVATE)
            .getString("current", null)
        val scene = fav.scenes().firstOrNull { it.id == current } ?: fav.scenes().firstOrNull()
            ?: SceneLookup.byId(current)
        qsTile?.apply {
            label = scene?.title ?: "Favoriet"
            subtitle = if (fav.ids().isEmpty()) "Pin een scene" else "${fav.ids().size}/4 pins"
            state = if (fav.ids().isEmpty()) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
            updateTile()
        }
    }
}
