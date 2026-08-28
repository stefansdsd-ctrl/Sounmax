package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsp.ListeningScenes
import com.example.ui.theme.ImmersiveBorder
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextPrimary
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun ListeningScenesBar(
    sceneController: SceneController,
    modifier: Modifier = Modifier
) {
    val activeSceneId by sceneController.activeSceneId.collectAsStateWithLifecycle()
    val safeVolume by sceneController.safeVolumeEnabled.collectAsStateWithLifecycle()
    val sleepLeft by sceneController.sleepTimerMinutes.collectAsStateWithLifecycle()
    val autoScene by sceneController.autoSceneEnabled.collectAsStateWithLifecycle()
    val suggested by sceneController.suggestedScene.collectAsStateWithLifecycle()
    val doseMin by sceneController.listeningMinutesToday.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Luister-scenes",
                color = ImmersiveTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Vandaag ${doseMin} min" + if (doseMin >= 120) " ⚠" else "",
                color = if (doseMin >= 120) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                fontSize = 11.sp
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
            modifier = Modifier.testTag("listening_scenes_row")
        ) {
            items(ListeningScenes.ALL, key = { it.id }) { scene ->
                val selected = activeSceneId == scene.id
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) ImmersiveSurfaceActive else ImmersiveSurface)
                        .border(
                            1.dp,
                            if (selected) ImmersiveLavenderAccent else ImmersiveBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { sceneController.applyListeningScene(scene) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("scene_" + scene.id)
                ) {
                    Text(scene.emoji + "  " + scene.name, color = ImmersiveTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(scene.description, color = ImmersiveTextSecondary, fontSize = 10.sp)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = autoScene,
                onClick = { sceneController.setAutoSceneEnabled(!autoScene) },
                label = { Text("Auto ${suggested.emoji}", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveTextSecondary
                ),
                modifier = Modifier.testTag("auto_scene_chip")
            )
            FilterChip(
                selected = safeVolume,
                onClick = { sceneController.setSafeVolume(!safeVolume) },
                label = { Text("Veilig volume", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveTextSecondary
                ),
                modifier = Modifier.testTag("safe_volume_chip")
            )
            listOf(15, 30, 60, 90).forEach { mins ->
                FilterChip(
                    selected = sleepLeft == mins,
                    onClick = {
                        if (sleepLeft == mins) sceneController.cancelSleepTimer()
                        else sceneController.startSleepTimer(mins)
                    },
                    label = { Text("${mins}m", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ImmersiveSurfaceActive,
                        selectedLabelColor = ImmersiveLavenderAccent,
                        labelColor = ImmersiveTextSecondary
                    )
                )
            }
            if (sleepLeft > 0 && sleepLeft !in listOf(15, 30, 60, 90)) {
                FilterChip(
                    selected = true,
                    onClick = { sceneController.cancelSleepTimer() },
                    label = { Text("Timer ${sleepLeft}m", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ImmersiveSurfaceActive,
                        selectedLabelColor = ImmersiveLavenderAccent
                    )
                )
            }
        }
    }
}
