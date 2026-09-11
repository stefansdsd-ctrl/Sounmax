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
        return buildShort("🎧", "Focus", 82, "ANC", "LDAC")
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val s = runCatching { WearClient.readStatus(this) }.getOrDefault(WearStatus())
        val bat = if (s.battery in 0..100) s.battery else -1
        val anc = ancShort(s.anc)
        return when (request.complicationType) {
            ComplicationType.LONG_TEXT -> {
                val codec = s.codec.takeIf { it.isNotBlank() }?.uppercase()?.take(8)
                val rssi = if (s.rssi < 0) " ${s.rssi}dBm" else ""
                val extra = listOfNotNull(
                    if (bat >= 0) "$bat%" else null,
                    anc,
                    codec,
                    if (s.focus) "FOCUS" else null
                ).joinToString(" · ")
                LongTextComplicationData.Builder(
                    PlainComplicationText.Builder("${s.sceneEmoji} ${s.sceneName}").build(),
                    PlainComplicationText.Builder("Tik = scene · dubbel = zoek").build()
                ).setTitle(PlainComplicationText.Builder(extra + rssi).build())
                    .setTapAction(tap())
                    .build()
            }
            ComplicationType.RANGED_VALUE -> RangedValueComplicationData.Builder(
                value = if (bat >= 0) bat.toFloat() else 50f,
                min = 0f,
                max = 100f,
                contentDescription = PlainComplicationText.Builder(
                    "${s.sceneName} $anc"
                ).build()
            ).setText(PlainComplicationText.Builder(if (bat >= 0) "$bat%" else s.sceneEmoji).build())
                .setTitle(PlainComplicationText.Builder("${s.sceneEmoji} $anc").build())
                .setTapAction(tap())
                .build()
            else -> buildShort(s.sceneEmoji, s.sceneName, bat, anc, s.codec)
        }
    }

    private fun ancShort(anc: String): String = when (anc.uppercase()) {
        "STRONG", "ANC_STRONG", "ON" -> "ANC"
        "MILD", "ANC_MILD" -> "ANC·"
        "OFF", "DISABLE", "DISABLED" -> "OFF"
        "AWARE", "TRANSPARENCY", "TRANSPARENT" -> "TR"
        else -> anc.take(3).ifBlank { "ANC" }
    }

    private fun buildShort(
        emoji: String,
        name: String,
        battery: Int,
        anc: String,
        codec: String
    ): ComplicationData {
        val title = when {
            battery in 0..100 -> "$battery%"
            anc.isNotBlank() -> anc
            else -> name.take(8)
        }
        val desc = buildString {
            append("$emoji $name")
            if (anc.isNotBlank()) append(" $anc")
            if (codec.isNotBlank()) append(" $codec")
        }
        return ShortTextComplicationData.Builder(
            PlainComplicationText.Builder(emoji).build(),
            PlainComplicationText.Builder(desc).build()
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
