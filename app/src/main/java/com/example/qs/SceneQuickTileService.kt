package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.ListeningScenes

class SceneQuickTileService : TileService() {
    override fun onStartListening() {
        val id = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
            .getString("last_scene_id", "focus")
        val scene = ListeningScenes.byId(id) ?: ListeningScenes.ALL.first()
        updateTile(scene.name, scene.emoji)
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val current = prefs.getString("last_scene_id", "focus")
        val idx = ListeningScenes.ALL.indexOfFirst { it.id == current }.let { if (it < 0) 0 else it }
        val next = ListeningScenes.ALL[(idx + 1) % ListeningScenes.ALL.size]
        prefs.edit().putString("last_scene_id", next.id).apply()
        sendBroadcast(
            Intent(ACTION_CYCLE_SCENE).setPackage(packageName).putExtra("scene_id", next.id)
        )
        updateTile(next.name, next.emoji)
    }

    private fun updateTile(name: String, emoji: String) {
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = name
            subtitle = "$emoji Scene"
            updateTile()
        }
    }

    companion object {
        const val ACTION_CYCLE_SCENE = "com.example.action.CYCLE_SCENE"
    }
}
