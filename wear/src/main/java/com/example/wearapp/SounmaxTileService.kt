package com.example.wearapp

import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Box
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.LayoutElementBuilders.FontStyles
import androidx.wear.tiles.LayoutElementBuilders.Layout
import androidx.wear.tiles.LayoutElementBuilders.Spacer
import androidx.wear.tiles.LayoutElementBuilders.Text
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ModifiersBuilders.Modifiers
import androidx.wear.tiles.ModifiersBuilders.Padding
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

class SounmaxTileService : TileService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return scope.future {
            val cmd = requestParams.state?.idToValueMapping
                ?.get(STATE_CMD)
                ?.stringValue
            if (!cmd.isNullOrBlank()) {
                runCatching { WearClient.send(this@SounmaxTileService, cmd) }
            }
            val status = readStatus()
            val params = requestParams.deviceParameters
            TileBuilders.Tile.Builder()
                .setResourcesVersion("2")
                .setFreshnessIntervalMillis(15_000)
                .setState(
                    StateBuilders.State.Builder()
                        .addKeyValuePair(STATE_CMD, ActionBuilders.stringVal(""))
                        .build()
                )
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(Layout.Builder().setRoot(buildLayout(status, params)).build())
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
            ResourceBuilders.Resources.Builder().setVersion("2").build()
        )
    }

    private fun buildLayout(
        status: WearStatus,
        params: DeviceParametersBuilders.DeviceParameters?
    ): LayoutElementBuilders.LayoutElement {
        val dspLabel = if (status.dsp) "DSP uit" else "DSP aan"
        val bat = if (status.battery in 0..100) "${status.battery}%" else "--"
        val sleep = if (status.sleepMin > 0) "${status.sleepMin}m" else "slaap"
        val anc = WearMainActivity.ancLabel(status.anc)
        return Column.Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .addContent(
                Text.Builder()
                    .setText("${status.sceneEmoji} ${status.sceneName}")
                    .setFontStyle(font(params) { FontStyles.title3(it).build() })
                    .build()
            )
            .addContent(
                Text.Builder()
                    .setText("${if (status.dsp) "DSP aan" else "DSP uit"} · $bat")
                    .setFontStyle(font(params) { FontStyles.caption1(it).build() })
                    .build()
            )
            .addContent(Spacer.Builder().setHeight(dp(6f)).build())
            .addContent(actionChip(dspLabel, WearPaths.CMD_TOGGLE_DSP, params))
            .addContent(Spacer.Builder().setHeight(dp(4f)).build())
            .addContent(actionChip("ANC $anc", WearPaths.CMD_CYCLE_ANC, params))
            .addContent(Spacer.Builder().setHeight(dp(4f)).build())
            .addContent(actionChip("Volgende scene", WearPaths.CMD_NEXT_SCENE, params))
            .addContent(Spacer.Builder().setHeight(dp(4f)).build())
            .addContent(actionChip("Play / pauze", WearPaths.CMD_PLAY_PAUSE, params))
            .addContent(Spacer.Builder().setHeight(dp(4f)).build())
            .addContent(actionChip(sleep, WearPaths.CMD_CYCLE_SLEEP, params))
            .build()
    }

    private fun font(
        params: DeviceParametersBuilders.DeviceParameters?,
        block: (DeviceParametersBuilders.DeviceParameters) -> LayoutElementBuilders.FontStyle
    ): LayoutElementBuilders.FontStyle {
        val p = params ?: DeviceParametersBuilders.DeviceParameters.Builder().build()
        return block(p)
    }

    private fun actionChip(
        label: String,
        cmd: String,
        params: DeviceParametersBuilders.DeviceParameters?
    ): LayoutElementBuilders.LayoutElement {
        val click = Clickable.Builder()
            .setOnClick(
                ActionBuilders.LoadAction.Builder()
                    .setRequestState(
                        StateBuilders.State.Builder()
                            .addKeyValuePair(STATE_CMD, ActionBuilders.stringVal(cmd))
                            .build()
                    )
                    .build()
            )
            .setId(cmd)
            .build()
        return Box.Builder()
            .setModifiers(
                Modifiers.Builder()
                    .setClickable(click)
                    .setPadding(
                        Padding.Builder()
                            .setStart(dp(10f))
                            .setEnd(dp(10f))
                            .setTop(dp(6f))
                            .setBottom(dp(6f))
                            .build()
                    )
                    .build()
            )
            .addContent(
                Text.Builder()
                    .setText(label)
                    .setFontStyle(
                        font(params) {
                            FontStyles.button(it).setColor(argb(0xFFB39DFF.toInt())).build()
                        }
                    )
                    .build()
            )
            .build()
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
                    sceneEmoji = map.getString(WearPaths.KEY_SCENE_EMOJI) ?: "\uD83C\uDFA7",
                    battery = map.getInt(WearPaths.KEY_BATTERY, -1),
                    sleepMin = map.getInt(WearPaths.KEY_SLEEP, 0),
                    anc = map.getString(WearPaths.KEY_ANC) ?: "STRONG"
                )
            }
        }.getOrElse { WearStatus() }
    }

    companion object {
        private const val STATE_CMD = "cmd"
    }
}
