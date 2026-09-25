package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.TalkBoost
import com.example.wear.WearBridge

class TalkBoostQuickTileService : TileService() {
    override fun onStartListening() = paint()

    override fun onClick() {
        TalkBoost.toggle(this, dsp = null)
        WearBridge.publishStatus(this)
        paint()
    }

    private fun paint() {
        val on = TalkBoost.isActive(this)
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (on) "Gesprek ${TalkBoost.remainingSec(this)}s" else "Gesprek 20s"
            subtitle = if (on) "Tik = herstel" else "Volume + spraak"
            updateTile()
        }
    }
}
