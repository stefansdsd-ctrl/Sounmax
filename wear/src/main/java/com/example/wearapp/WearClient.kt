package com.example.wearapp

import android.content.Context
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

data class WearStatus(
    val dsp: Boolean = true,
    val sceneName: String = "Scene",
    val sceneEmoji: String = "🎧",
    val battery: Int = -1,
    val sleepMin: Int = 0,
    val headset: String = "Headset"
)

object WearClient {
    suspend fun readStatus(context: Context): WearStatus {
        val items = Wearable.getDataClient(context).dataItems.await()
        val match = items.find { it.uri.path == WearPaths.STATUS }
        items.release()
        if (match == null) return WearStatus()
        val map = DataMapItem.fromDataItem(match).dataMap
        return WearStatus(
            dsp = map.getBoolean(WearPaths.KEY_DSP, true),
            sceneName = map.getString(WearPaths.KEY_SCENE_NAME) ?: "Scene",
            sceneEmoji = map.getString(WearPaths.KEY_SCENE_EMOJI) ?: "🎧",
            battery = map.getInt(WearPaths.KEY_BATTERY, -1),
            sleepMin = map.getInt(WearPaths.KEY_SLEEP, 0),
            headset = map.getString(WearPaths.KEY_HEADSET) ?: "Headset"
        )
    }

    suspend fun send(context: Context, cmd: String) {
        val nodes = Wearable.getNodeClient(context).connectedNodes.await()
        val client = Wearable.getMessageClient(context)
        nodes.forEach { node ->
            client.sendMessage(node.id, WearPaths.CMD, cmd.toByteArray()).await()
        }
    }
}
