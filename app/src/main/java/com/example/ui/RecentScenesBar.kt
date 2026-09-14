package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.media.RecentScenes
import com.example.media.SceneAutomation
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun RecentScenesBar(sceneController: SceneController) {
    val context = LocalContext.current
    val active by sceneController.activeSceneId.collectAsStateWithLifecycle()
    val scenes = remember(active) {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, android.content.Context.MODE_PRIVATE)
        RecentScenes.scenes(prefs).filter { it.id != active }.take(5)
    }
    if (scenes.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("recent_scenes_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Recent", fontSize = 11.sp, color = ImmersiveTextSecondary)
        scenes.forEach { scene ->
            AssistChip(
                onClick = { sceneController.applyListeningScene(scene) },
                label = { Text("${scene.emoji} ${scene.name}", fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("recent_scene_${scene.id}")
            )
        }
    }
}
