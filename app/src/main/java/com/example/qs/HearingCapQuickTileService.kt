package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.HearingDoseGuard
import com.example.wear.WearBridge

class HearingCapQuickTileService : TileService() {
    override fun onStartListening() = paint()

    override fun onClick() {
        HearingDoseGuard.applyCap(this)
        WearBridge.publishStatus(this)
        paint()
    }

    private fun paint() {
        val advice = HearingDoseGuard.adviceNow(this)
        qsTile?.apply {
            state = if (advice.suggestPause || advice.capPercent < 100) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = "Cap ${advice.capPercent}%"
            subtitle = if (advice.suggestPause) "Oorpauze" else "Volume"
            updateTile()
        }
    }
}
