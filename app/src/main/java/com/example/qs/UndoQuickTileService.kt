package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.ListeningScenes
import com.example.widget.SoundMaxWidget

class UndoQuickTileService : TileService() {
    override fun onStartListening() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val prev = prefs.getString("prev_scene_id", null)
        val scene = ListeningScenes.byId(prev)
        qsTile?.apply {
            state = if (scene != null) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = scene?.name ?: "Undo"
            subtitle = scene?.let { "${it.emoji} terug" } ?: "Geen vorige"
            updateTile()
        }
    }

    override fun onClick() {
        SoundMaxWidget.undoScene(this)
        onStartListening()
    }
}
