package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.VolumeDuck
import com.example.wear.WearBridge

class DuckQuickTileService : TileService() {
    override fun onStartListening() = paint()

    override fun onClick() {
        VolumeDuck.toggle(this)
        WearBridge.publishStatus(this)
        paint()
    }

    private fun paint() {
        val on = VolumeDuck.isActive(this)
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (on) "Omroep ${VolumeDuck.remainingSec(this)}s" else "Omroep 15s"
            subtitle = if (on) "Tik = herstel" else "Volume dip"
            updateTile()
        }
    }
}
