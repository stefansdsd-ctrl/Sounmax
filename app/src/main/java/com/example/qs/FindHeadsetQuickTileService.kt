package com.example.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.media.FindHeadset

class FindHeadsetQuickTileService : TileService() {
    override fun onStartListening() {
        qsTile?.apply {
            state = Tile.STATE_INACTIVE
            label = "Zoek headset"
            subtitle = "Chirp"
            updateTile()
        }
    }

    override fun onClick() {
        FindHeadset.ping(this)
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = "Pingt…"
            updateTile()
        }
        qsTile?.label = "Zoek headset"
        qsTile?.state = Tile.STATE_INACTIVE
        qsTile?.updateTile()
    }
}
