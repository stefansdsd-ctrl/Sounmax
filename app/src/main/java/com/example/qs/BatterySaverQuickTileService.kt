package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.data.BatterySaverDsp
import com.example.widget.SoundMaxWidget

class BatterySaverQuickTileService : TileService() {
    override fun onStartListening() = refresh()

    override fun onClick() {
        BatterySaverDsp.toggle(this)
        SoundMaxWidget.refreshAll(this)
        refresh()
    }

    private fun refresh() {
        val on = BatterySaverDsp.enabled(this)
        val saving = BatterySaverDsp.shouldSave(this)
        qsTile?.apply {
            state = if (on && saving) Tile.STATE_ACTIVE else if (on) Tile.STATE_INACTIVE else Tile.STATE_UNAVAILABLE
            label = BatterySaverDsp.label(this@BatterySaverQuickTileService)
            subtitle = "Sounmax Accu-DSP"
            updateTile()
        }
    }
}
