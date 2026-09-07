package com.example.qs

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.dsp.AncMode
import com.example.dsp.ListeningScenes
import com.example.media.FocusSession

/**
 * Één tegel voor scene + ANC + Focus.
 * Tik: start of stop Focus 25 + Focus-scene + ANC Maximaal.
 */
class ComboQuickTileService : TileService() {
    override fun onStartListening() {
        refresh()
    }

    override fun onClick() {
        val focusOn = FocusSession.isActive(this)
        if (focusOn) {
            FocusSession.toggle(this)
        } else {
            val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
            val scene = ListeningScenes.byId("focus") ?: ListeningScenes.ALL.first()
            prefs.edit()
                .putString("last_scene_id", scene.id)
                .putString("last_anc", AncMode.STRONG.name)
                .apply()
            sendBroadcast(
                Intent(SceneQuickTileService.ACTION_CYCLE_SCENE)
                    .setPackage(packageName)
                    .putExtra("scene_id", scene.id)
            )
            sendBroadcast(
                Intent(AncQuickTileService.ACTION_CYCLE_ANC)
                    .setPackage(packageName)
                    .putExtra("anc", AncMode.STRONG.name)
            )
            if (!FocusSession.isActive(this)) {
                FocusSession.toggle(this)
            }
        }
        refresh()
    }

    private fun refresh() {
        val prefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        val scene = ListeningScenes.byId(prefs.getString("last_scene_id", "focus"))
            ?: ListeningScenes.ALL.first()
        val ancName = prefs.getString("last_anc", AncMode.STRONG.name)
        val anc = runCatching { AncMode.valueOf(ancName ?: AncMode.STRONG.name) }
            .getOrDefault(AncMode.STRONG)
        val focusOn = FocusSession.isActive(this)
        val min = (FocusSession.remainingMs(this) / 60_000L).toInt()
        qsTile?.apply {
            state = if (focusOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = if (focusOn) "Focus $min m" else "${scene.emoji} ${scene.name}"
            subtitle = if (focusOn) "${anc.displayName}" else "Tik: Focus+ANC"
            updateTile()
        }
    }
}
