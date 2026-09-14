package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.ListeningScenes

class SceneQuickTileService : TileService() {
    override fun onStartListening() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val id = prefs.getString("last_scene_id", "focus")
        val scene = ListeningScenes.byId(id) ?: ListeningScenes.ALL.first()
        val bat = prefs.getInt(com.example.widget.SoundMaxWidget.KEY_BATTERY, -1)
        updateTile(scene.name, scene.emoji, bat)
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val current = prefs.getString("last_scene_id", "focus")
        val favCsv = prefs.getString("fav_scenes", "") ?: ""
        val favs = favCsv.split(',').map { it.trim() }.filter { it.isNotBlank() }
        val pool = if (favs.size >= 2) {
            favs.mapNotNull { ListeningScenes.byId(it) ?: com.example.dsp.SceneLookup.byId(it) }
        } else {
            com.example.dsp.SceneLookup.ALL
        }
        val idx = pool.indexOfFirst { it.id == current }.let { if (it < 0) 0 else it }
        val next = pool[(idx + 1) % pool.size]
        prefs.edit().putString("last_scene_id", next.id).apply()
        sendBroadcast(
            Intent(ACTION_CYCLE_SCENE).setPackage(packageName).putExtra("scene_id", next.id)
        )
        val bat = prefs.getInt(com.example.widget.SoundMaxWidget.KEY_BATTERY, -1)
        updateTile(next.name, next.emoji, bat)
    }

    private fun updateTile(name: String, emoji: String, battery: Int) {
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = name
            subtitle = if (battery in 0..100) "$emoji · BT $battery%" else "$emoji Scene"
            updateTile()
        }
    }

    companion object {
        const val ACTION_CYCLE_SCENE = "com.example.action.CYCLE_SCENE"
    }
}
