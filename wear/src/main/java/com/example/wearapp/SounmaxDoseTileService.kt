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

/** Wear-tegel: dagelijkse gehoordosis, tik zet rust-EQ. */
class SounmaxDoseTileService : TileService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return scope.future {
            val cmd = requestParams.state?.idToValueMapping
                ?.get(STATE_CMD)
                ?.stringValue
            if (!cmd.isNullOrBlank()) {
                runCatching { WearClient.send(this@SounmaxDoseTileService, cmd) }
            }
            val dose = readDose()
            val params = requestParams.deviceParameters
            TileBuilders.Tile.Builder()
                .setResourcesVersion("1")
                .setFreshnessIntervalMillis(30_000)
                .setState(
                    StateBuilders.State.Builder()
                        .addKeyValuePair(STATE_CMD, ActionBuilders.stringVal(""))
                        .build()
                )
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(Layout.Builder().setRoot(buildLayout(dose, params)).build())
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
        doseMin: Int,
        params: DeviceParametersBuilders.DeviceParameters?
    ): LayoutElementBuilders.LayoutElement {
        val click = Clickable.Builder()
            .setOnClick(
                ActionBuilders.LoadAction.Builder()
                    .setRequestState(
                        StateBuilders.State.Builder()
                            .addKeyValuePair(STATE_CMD, ActionBuilders.stringVal(WearPaths.CMD_EQ_DOSE))
                            .build()
                    )
                    .build()
            )
            .setId(WearPaths.CMD_EQ_DOSE)
            .build()
        val p = params ?: DeviceParametersBuilders.DeviceParameters.Builder().build()
        val color = if (doseMin >= 180) 0xFFFF8A80.toInt() else 0xFFB39DFF.toInt()
        return Box.Builder()
            .setModifiers(Modifiers.Builder().setClickable(click).build())
            .addContent(
                Column.Builder()
                    .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                    .addContent(
                        Text.Builder()
                            .setText("Dosis")
                            .setFontStyle(FontStyles.caption1(p).build())
                            .build()
                    )
                    .addContent(
                        Text.Builder()
                            .setText("${doseMin}m / 180")
                            .setFontStyle(FontStyles.title2(p).setColor(argb(color)).build())
                            .build()
                    )
                    .addContent(
                        Text.Builder()
                            .setText(if (doseMin >= 180) "tik = rust-EQ" else "tik = oorpauze")
                            .setFontStyle(FontStyles.caption2(p).build())
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private suspend fun readDose(): Int {
        return runCatching {
            val items = Wearable.getDataClient(this).dataItems.await()
            val match = items.find { it.uri.path == WearPaths.STATUS }
            items.release()
            if (match == null) 0 else {
                DataMapItem.fromDataItem(match).dataMap.getInt(WearPaths.KEY_DOSE, 0)
            }
        }.getOrElse { 0 }
    }

    companion object {
        private const val STATE_CMD = "cmd"
    }
}
