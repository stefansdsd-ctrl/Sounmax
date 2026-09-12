package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.media.OneEarMode

class OneEarQuickTileService : TileService() {
    override fun onStartListening() = refresh()

    override fun onClick() {
        OneEarMode.toggle(this)
        refresh()
    }

    private fun refresh() {
        val on = OneEarMode.enabled(this)
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (on) "Eén oor" else "Stereo"
            subtitle = "SoundMax"
            updateTile()
        }
    }
}
