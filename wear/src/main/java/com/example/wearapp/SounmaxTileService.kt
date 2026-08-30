package com.example.wearapp

import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.tasks.await

class SounmaxTileService : TileService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return scope.future {
            val status = readStatus()
            val dsp = if (status.dsp) "DSP aan" else "DSP uit"
            val bat = if (status.battery in 0..100) "${status.battery}%" else "--"
            val sleep = if (status.sleepMin > 0) " slaap ${status.sleepMin}m" else ""
            val text = "${status.sceneEmoji} ${status.sceneName}\n$dsp · $bat$sleep"
            androidx.wear.tiles.TileBuilders.Tile.Builder()
                .setResourcesVersion("1")
                .setFreshnessIntervalMillis(30_000)
                .setTileTimeline(
                    androidx.wear.tiles.TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            androidx.wear.tiles.TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(
                                    androidx.wear.tiles.LayoutElementBuilders.Layout.Builder()
                                        .setRoot(
                                            androidx.wear.tiles.LayoutElementBuilders.Text.Builder()
                                                .setText(text)
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        }
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ListenableFuture<androidx.wear.tiles.ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            androidx.wear.tiles.ResourceBuilders.Resources.Builder()
                .setVersion("1")
                .build()
        )
    }

    private suspend fun readStatus(): WearStatus {
        return runCatching {
            val items = Wearable.getDataClient(this).dataItems.await()
            val match = items.find { it.uri.path == WearPaths.STATUS }
            items.release()
            if (match == null) WearStatus() else {
                val map = DataMapItem.fromDataItem(match).dataMap
                WearStatus(
                    dsp = map.getBoolean(WearPaths.KEY_DSP, true),
                    sceneName = map.getString(WearPaths.KEY_SCENE_NAME) ?: "Scene",
                    sceneEmoji = map.getString(WearPaths.KEY_SCENE_EMOJI) ?: "🎧",
                    battery = map.getInt(WearPaths.KEY_BATTERY, -1),
                    sleepMin = map.getInt(WearPaths.KEY_SLEEP, 0)
                )
            }
        }.getOrElse { WearStatus() }
    }
}
