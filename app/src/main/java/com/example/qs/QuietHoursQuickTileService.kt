package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.media.QuietHours

class QuietHoursQuickTileService : TileService() {
    override fun onStartListening() = refresh()

    override fun onClick() {
        QuietHours.cycle(this)
        QuietHours.enforce(this)
        refresh()
    }

    private fun refresh() {
        val on = QuietHours.enabled(this)
        val now = QuietHours.isQuietNow(this)
        qsTile?.apply {
            state = if (on && now) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = QuietHours.label(this@QuietHoursQuickTileService)
            subtitle = "SoundMax"
            updateTile()
        }
    }
}
