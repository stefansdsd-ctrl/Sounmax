package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.media.FocusSession

class FocusQuickTileService : TileService() {
    override fun onStartListening() {
        val on = FocusSession.isActive(this)
        val min = (FocusSession.remainingMs(this) / 60_000L).toInt()
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (on) "Focus $min m" else "Focus 25"
            subtitle = if (on) "Deep work aan" else "25 min + lock"
            updateTile()
        }
    }

    override fun onClick() {
        FocusSession.toggle(this)
        onStartListening()
    }
}
