package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.data.PinProfiles
import com.example.wear.WearBridge
import com.example.widget.SoundMaxWidget

class PinSetQuickTileService : TileService() {
    override fun onStartListening() {
        paint()
    }

    override fun onClick() {
        PinProfiles.cycle(this)
        SoundMaxWidget.refreshAll(this)
        WearBridge.publishStatus(this)
        paint()
    }

    private fun paint() {
        val profile = PinProfiles.active(this)
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = profile.name
            subtitle = "Pin-set"
            updateTile()
        }
    }
}
