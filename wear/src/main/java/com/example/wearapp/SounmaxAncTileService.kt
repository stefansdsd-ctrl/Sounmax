package com.example.wearapp

import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.FontStyles
import androidx.wear.tiles.LayoutElementBuilders.Layout
import androidx.wear.tiles.LayoutElementBuilders.Text
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ModifiersBuilders.Modifiers
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.StateBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.TimelineBuilders
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.tasks.await

/** Compact Wear-tegel: toont ANC-modus, tik wisselt. */
class SounmaxAncTileService : TileService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return scope.future {
            val cmd = requestParams.state?.idToValueMapping
                ?.get(STATE_CMD)
                ?.stringValue
            if (!cmd.isNullOrBlank()) {
                runCatching { WearClient.send(this@SounmaxAncTileService, cmd) }
            }
            val anc = readAnc()
            val params = requestParams.deviceParameters
            TileBuilders.Tile.Builder()
                .setResourcesVersion("1")
                .setFreshnessIntervalMillis(10_000)
                .setState(
                    StateBuilders.State.Builder()
                        .addKeyValuePair(STATE_CMD, ActionBuilders.stringVal(""))
                        .build()
                )
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(Layout.Builder().setRoot(buildLayout(anc, params)).build())
                                .build()
                        )
                        .build()
                )
                .build()
        }
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder().setVersion("1").build()
        )
    }

    private fun buildLayout(
        anc: String,
        params: DeviceParametersBuilders.DeviceParameters?
    ): LayoutElementBuilders.LayoutElement {
        val label = WearMainActivity.ancLabel(anc)
        val click = Clickable.Builder()
            .setOnClick(
                ActionBuilders.LoadAction.Builder()
                    .setRequestState(
                        StateBuilders.State.Builder()
                            .addKeyValuePair(STATE_CMD, ActionBuilders.stringVal(WearPaths.CMD_CYCLE_ANC))
                            .build()
                    )
                    .build()
            )
            .setId(WearPaths.CMD_CYCLE_ANC)
            .build()
        val p = params ?: DeviceParametersBuilders.DeviceParameters.Builder().build()
        return Box.Builder()
            .setModifiers(Modifiers.Builder().setClickable(click).build())
            .addContent(
                Column.Builder()
                    .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                    .addContent(
                        Text.Builder()
                            .setText("ANC")
                            .setFontStyle(FontStyles.caption1(p).build())
                            .build()
                    )
                    .addContent(
                        Text.Builder()
                            .setText(label)
                            .setFontStyle(FontStyles.title2(p).setColor(argb(0xFFB39DFF.toInt())).build())
                            .build()
                    )
                    .addContent(
                        Text.Builder()
                            .setText("tik = wissel")
                            .setFontStyle(FontStyles.caption2(p).build())
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private suspend fun readAnc(): String {
        return runCatching {
            val items = Wearable.getDataClient(this).dataItems.await()
            val match = items.find { it.uri.path == WearPaths.STATUS }
            items.release()
            if (match == null) "STRONG" else {
                DataMapItem.fromDataItem(match).dataMap.getString(WearPaths.KEY_ANC) ?: "STRONG"
            }
        }.getOrElse { "STRONG" }
    }

    companion object {
        private const val STATE_CMD = "cmd"
    }
}
