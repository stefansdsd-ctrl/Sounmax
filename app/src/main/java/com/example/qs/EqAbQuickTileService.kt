package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.DspHolder
import com.example.dsp.EqSnapshot

class EqAbQuickTileService : TileService() {
    override fun onStartListening() {
        val side = EqSnapshot.activeSide(this)
        qsTile?.apply {
            state = if (side == "B") Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = "EQ $side"
            subtitle = "A/B"
            updateTile()
        }
    }

    override fun onClick() {
        val dsp = DspHolder.instance
        val current = dsp?.bandGains?.value ?: emptyList()
        EqSnapshot.toggle(this, current) { gains ->
            gains.forEachIndexed { i, g -> dsp?.updateBandGain(i, g) }
        }
        onStartListening()
    }
}
