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
import com.example.data.LastSceneRestore
import com.example.data.SceneUsage
import com.example.data.WidgetDefaultScene
import com.example.dsp.ListeningScene
import com.example.media.WeatherAdvisor
import com.example.dsp.ListeningScenes

class FavoriteScenesGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val defaultScene = WidgetDefaultScene.scene(context)
        val favs = SoundMaxWidget.favoriteScenes(context)
            .filter { it.id != defaultScene?.id }
            .take(3)
        val suggested = defaultScene ?: pickSuggested(context) ?: LastSceneRestore.scene(context)
        provideContent {
            GlanceTheme {
                Content(favs, suggested, defaultScene != null)
            }
        }
    }

    @Composable
    private fun Content(favs: List<ListeningScene>, suggested: ListeningScene?, isDefault: Boolean) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp)
        ) {
            Text(
                text = "Favorieten",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(GlanceModifier.height(8.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                favs.forEachIndexed { index, scene ->
                    if (index > 0) Spacer(GlanceModifier.width(6.dp))
                    SceneChip(scene)
                }
            }
            if (suggested != null) {
                Spacer(GlanceModifier.height(8.dp))
                val prefix = if (isDefault) "1-tap" else "Nu"
                Text(
                    text = "$prefix: ${suggested.emoji} ${suggested.name}",
                    style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant, fontSize = 12.sp),
                    modifier = GlanceModifier.clickable(
                        actionRunCallback<ApplySceneAction>(
                            actionParametersOf(ApplySceneAction.sceneIdKey to suggested.id)
                        )
                    )
                )
            }
        }
    }

    @Composable
    private fun SceneChip(scene: ListeningScene) {
        Text(
            text = "${scene.emoji} ${scene.name}",
            style = TextStyle(color = GlanceTheme.colors.onPrimary, fontSize = 12.sp),
            modifier = GlanceModifier
                .background(GlanceTheme.colors.primary)
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clickable(
                    actionRunCallback<ApplySceneAction>(
                        actionParametersOf(ApplySceneAction.sceneIdKey to scene.id)
                    )
                )
        )
    }

    companion object {
        fun pickSuggested(context: Context): ListeningScene? {
            WidgetDefaultScene.scene(context)?.let { return it }
            val used = SceneUsage.suggestNow(context)
            if (used != null && SceneUsage.count(context, used.id) >= 2) return used
            return WeatherAdvisor.suggest(context, ListeningScenes.suggestedNow())
        }

        suspend fun refresh(context: Context) {
            FavoriteScenesGlanceWidget().updateAll(context)
        }
    }
}

class FavoriteScenesGlanceReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FavoriteScenesGlanceWidget()
}

class ApplySceneAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val id = parameters[sceneIdKey]
        SoundMaxWidget.applyScene(context, id)
        FavoriteScenesGlanceWidget().update(context, glanceId)
        SoundMaxWidget.refreshAll(context)
    }

    companion object {
        val sceneIdKey = ActionParameters.Key<String>("scene_id")
    }
}
