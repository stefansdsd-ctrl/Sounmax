package com.example.wearapp

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService

class SounmaxComplicationService : SuspendingComplicationDataSourceService() {
    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return buildShort("🎧", "Focus", 82)
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val s = runCatching { WearClient.readStatus(this) }.getOrDefault(WearStatus())
        val bat = if (s.battery in 0..100) s.battery else -1
        return when (request.complicationType) {
            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                PlainComplicationText.Builder("${s.sceneEmoji} ${s.sceneName}" + if (bat >= 0) " · $bat%" else "").build(),
                PlainComplicationText.Builder("Tik = volgende scene").build()
            ).setTapAction(tap()).build()
            ComplicationType.RANGED_VALUE -> RangedValueComplicationData.Builder(
                value = if (bat >= 0) bat.toFloat() else 50f,
                min = 0f,
                max = 100f,
                contentDescription = PlainComplicationText.Builder(s.sceneName).build()
            ).setText(PlainComplicationText.Builder(if (bat >= 0) "$bat%" else s.sceneEmoji).build())
                .setTitle(PlainComplicationText.Builder(s.sceneName).build())
                .setTapAction(tap())
                .build()
            else -> buildShort(s.sceneEmoji, s.sceneName, bat)
        }
    }

    private fun buildShort(emoji: String, name: String, battery: Int): ComplicationData {
        val title = if (battery in 0..100) "$battery%" else name.take(8)
        return ShortTextComplicationData.Builder(
            PlainComplicationText.Builder(emoji).build(),
            PlainComplicationText.Builder("$emoji $name").build()
        ).setTitle(PlainComplicationText.Builder(title).build())
            .setTapAction(tap())
            .build()
    }

    private fun tap(): PendingIntent = PendingIntent.getActivity(
        this, 1,
        Intent(this, ComplicationTapActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}
