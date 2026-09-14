package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.ListeningScene
import com.example.widget.FavoriteScenesGlanceWidget
import com.example.widget.SoundMaxWidget

class SuggestQuickTileService : TileService() {
    override fun onStartListening() {
        paint(FavoriteScenesGlanceWidget.pickSuggested(this))
    }

    override fun onClick() {
        SoundMaxWidget.applySuggested(this)
        SoundMaxWidget.refreshAll(this)
        paint(FavoriteScenesGlanceWidget.pickSuggested(this))
    }

    private fun paint(scene: ListeningScene?) {
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = scene?.name ?: "Suggestie"
            subtitle = if (scene != null) "${scene.emoji} nu" else "Scene"
            updateTile()
        }
    }
}
