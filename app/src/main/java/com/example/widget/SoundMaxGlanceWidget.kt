package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.dsp.ListeningScenes
import com.example.media.DspControlService
import com.example.media.BatterySaverOneTap
import com.example.media.EarRestOneTap
import com.example.media.EveningWindDownOneTap
import com.example.media.FindHeadset
import com.example.media.FocusSession
import com.example.media.MorningBoostOneTap
import com.example.media.OneTapProfiles
import com.example.media.TravelLock
import com.example.media.PhoneBatteryAdvisor
import com.example.media.QuietHours

class SoundMaxGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val ui = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        val enabled = ui.getBoolean(DspControlService.KEY_DSP, true)
        val scene = ListeningScenes.byId(wellness.getString("last_scene_id", null))
            ?: ListeningScenes.ALL.first()
        val anc = wellness.getString("last_anc", "STRONG") ?: "STRONG"
        val battery = wellness.getInt(SoundMaxWidget.KEY_BATTERY, -1)
        val phone = PhoneBatteryAdvisor.level(context)
        val sleepLeft = SoundMaxWidget.remainingSleepMinutes(
            wellness.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L)
        )
        val focusLeft = if (FocusSession.isActive(context)) {
            (FocusSession.remainingMs(context) / 60_000L).toInt()
        } else 0
        val quiet = QuietHours.isQuietNow(context)
        val title = wellness.getString(SoundMaxWidget.KEY_HEADSET_NAME, null)?.take(18) ?: "Sounmax"
        val phoneTxt = phone?.let { "$it%" } ?: "--%"
        val status = buildString {
            append(if (battery in 0..100) "BT $battery%" else "BT --%")
            append(" · TEL $phoneTxt")
            if (quiet) append(" · STIL")
        }
        val extra = when {
            focusLeft > 0 -> "Focus ${focusLeft}m"
            sleepLeft > 0 -> "Slaap ${sleepLeft}m"
            else -> if (enabled) "DSP aan" else "DSP uit"
        }
        provideContent {
            GlanceTheme {
                Content(
                    title = title,
                    status = status,
                    scene = "${scene.emoji} ${scene.name}",
                    anc = anc.take(6),
                    extra = extra,
                    dspOn = enabled
                )
            }
        }
    }

    @Composable
    private fun Content(
        title: String,
        status: String,
        scene: String,
        anc: String,
        extra: String,
        dspOn: Boolean
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "$status · ANC $anc",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant, fontSize = 11.sp)
            )
            Spacer(GlanceModifier.height(6.dp))
            Text(
                text = scene,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = extra,
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant, fontSize = 11.sp)
            )
            Spacer(GlanceModifier.height(8.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                ActionChip("ANC", GlanceWidgetAction.ANC)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("◀", GlanceWidgetAction.PREV)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("▶", GlanceWidgetAction.NEXT)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip(if (dspOn) "DSP" else "raw", GlanceWidgetAction.DSP)
            }
            Spacer(GlanceModifier.height(6.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                ActionChip("Undo", GlanceWidgetAction.UNDO)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("Slaap", GlanceWidgetAction.SLEEP)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("Nu", GlanceWidgetAction.SUGGEST)
            }
            Spacer(GlanceModifier.height(6.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                ActionChip("Ochtend", GlanceWidgetAction.MORNING)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("Pauze", GlanceWidgetAction.REST)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("Zoek", GlanceWidgetAction.FIND)
            }
            Spacer(GlanceModifier.height(6.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                ActionChip("Focus", GlanceWidgetAction.FOCUS)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("Sport", GlanceWidgetAction.GYM)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("Avond", GlanceWidgetAction.EVENING)
            }
            Spacer(GlanceModifier.height(6.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                ActionChip("Reis", GlanceWidgetAction.TRAVEL)
                Spacer(GlanceModifier.width(6.dp))
                ActionChip("Accu", GlanceWidgetAction.SAVER)
            }
        }
    }

    @Composable
    private fun ActionChip(label: String, action: String) {
        Text(
            text = label,
            style = TextStyle(color = GlanceTheme.colors.onPrimary, fontSize = 12.sp),
            modifier = GlanceModifier
                .background(GlanceTheme.colors.primary)
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clickable(
                    actionRunCallback<GlanceControlAction>(
                        actionParametersOf(GlanceControlAction.key to action)
                    )
                )
        )
    }

    companion object {
        suspend fun refresh(context: Context) {
            SoundMaxGlanceWidget().updateAll(context)
        }
    }
}

object GlanceWidgetAction {
    const val ANC = "anc"
    const val PREV = "prev"
    const val NEXT = "next"
    const val DSP = "dsp"
    const val UNDO = "undo"
    const val SLEEP = "sleep"
    const val SUGGEST = "suggest"
    const val MORNING = "morning"
    const val REST = "rest"
    const val FIND = "find"
    const val FOCUS = "focus"
    const val GYM = "gym"
    const val EVENING = "evening"
    const val TRAVEL = "travel"
    const val SAVER = "saver"
}

class SoundMaxGlanceReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SoundMaxGlanceWidget()
}

class GlanceControlAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        when (parameters[key]) {
            GlanceWidgetAction.ANC -> SoundMaxWidget.cycleAnc(context)
            GlanceWidgetAction.PREV -> SoundMaxWidget.cycleScene(context, -1)
            GlanceWidgetAction.NEXT -> SoundMaxWidget.cycleScene(context, +1)
            GlanceWidgetAction.DSP -> {
                val ui = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
                ui.edit()
                    .putBoolean(DspControlService.KEY_DSP, !ui.getBoolean(DspControlService.KEY_DSP, true))
                    .apply()
                DspControlService.start(context)
            }
            GlanceWidgetAction.UNDO -> SoundMaxWidget.undoScene(context)
            GlanceWidgetAction.SLEEP -> SoundMaxWidget.cycleSleep(context)
            GlanceWidgetAction.MORNING -> MorningBoostOneTap.apply(context)
            GlanceWidgetAction.REST -> EarRestOneTap.apply(context)
            GlanceWidgetAction.FIND -> FindHeadset.ping(context)
            GlanceWidgetAction.FOCUS -> FocusSession.cycleOrToggle(context)
            GlanceWidgetAction.GYM -> OneTapProfiles.apply(context, "gym")
            GlanceWidgetAction.EVENING -> EveningWindDownOneTap.apply(context)
            GlanceWidgetAction.TRAVEL -> TravelLock.toggle(context)
            GlanceWidgetAction.SAVER -> BatterySaverOneTap.toggle(context)
            else -> SoundMaxWidget.applySuggested(context)
        }
        SoundMaxGlanceWidget().update(context, glanceId)
        SoundMaxWidget.refreshAll(context)
    }

    companion object {
        val key = ActionParameters.Key<String>("glance_action")
    }
}
