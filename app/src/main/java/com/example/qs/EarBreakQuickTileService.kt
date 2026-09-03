package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.ListeningScenes
import com.example.media.DspControlService
import com.example.widget.SoundMaxWidget

class EarBreakQuickTileService : TileService() {
    override fun onStartListening() {
        val id = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
            .getString("last_scene_id", "")
        val on = id == "rest"
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (on) "Oorpauze aan" else "Oorpauze"
            subtitle = "Rust-scene"
            updateTile()
        }
    }

    override fun onClick() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val current = prefs.getString("last_scene_id", "focus")
        val nextId = if (current == "rest") {
            prefs.getString("pre_break_scene", "focus") ?: "focus"
        } else {
            prefs.edit().putString("pre_break_scene", current).apply()
            "rest"
        }
        val scene = ListeningScenes.byId(nextId) ?: ListeningScenes.ALL.first()
        prefs.edit()
            .putString("last_scene_id", scene.id)
            .putBoolean("pending_widget_scene", true)
            .apply()
        sendBroadcast(
            Intent(SceneQuickTileService.ACTION_CYCLE_SCENE)
                .setPackage(packageName)
                .putExtra("scene_id", scene.id)
        )
        runCatching { DspControlService.start(this) }
        SoundMaxWidget.refreshAll(this)
        qsTile?.apply {
            state = if (scene.id == "rest") Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (scene.id == "rest") "Oorpauze aan" else "Oorpauze"
            updateTile()
        }
    }
}
