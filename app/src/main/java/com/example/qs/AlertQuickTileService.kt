package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.SceneLookup

class AlertQuickTileService : TileService() {
    override fun onStartListening() {
        val id = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
            .getString("last_scene_id", "")
        updateTile(id == "alert")
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val current = prefs.getString("last_scene_id", "")
        val nextId = if (current == "alert") {
            prefs.getString("pre_alert_scene_id", "focus") ?: "focus"
        } else {
            prefs.edit().putString("pre_alert_scene_id", current).apply()
            "alert"
        }
        val scene = SceneLookup.byId(nextId) ?: SceneLookup.ALL.first()
        prefs.edit().putString("last_scene_id", scene.id).apply()
        sendBroadcast(
            Intent(SceneQuickTileService.ACTION_CYCLE_SCENE)
                .setPackage(packageName)
                .putExtra("scene_id", scene.id)
        )
        updateTile(scene.id == "alert")
    }

    private fun updateTile(active: Boolean) {
        qsTile?.apply {
            state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (active) "Alert aan" else "Alert"
            subtitle = "🔔 Omgeving"
            updateTile()
        }
    }
}
