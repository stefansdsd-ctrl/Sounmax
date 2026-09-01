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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsp.ListeningScene
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
    val locked by sceneController.sceneLocked.collectAsStateWithLifecycle()
    val suggested by sceneController.suggestedScene.collectAsStateWithLifecycle()
    val dose by sceneController.listeningMinutesToday.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val scenes = remember(query) { sceneController.filteredScenes(query) }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Luister-scenes · ${dose} min vandaag · tip ${suggested.emoji} ${suggested.name}",
            color = ImmersiveTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().testTag("scene_search"),
            singleLine = true,
            placeholder = { Text("Zoek scene (trein, focus, game…)", fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ImmersiveLavenderAccent,
                unfocusedBorderColor = ImmersiveBorder
            )
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
            modifier = Modifier.testTag("listening_scenes_row")
        ) {
            items(scenes, key = { it.id }) { scene ->
                SceneChip(scene, activeSceneId == scene.id) {
                    sceneController.applyListeningScene(scene)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = autoScene,
                onClick = { sceneController.setAutoSceneEnabled(!autoScene) },
                label = { Text("Auto", fontSize = 11.sp) },
                colors = chipColors()
            )
            FilterChip(
                selected = locked,
                onClick = { sceneController.setSceneLocked(!locked) },
                label = { Text("Slot", fontSize = 11.sp) },
                colors = chipColors()
            )
            FilterChip(
                selected = safeVolume,
                onClick = { sceneController.setSafeVolume(!safeVolume) },
                label = { Text("Veilig volume", fontSize = 11.sp) },
                colors = chipColors(),
                modifier = Modifier.testTag("safe_volume_chip")
            )
            listOf(15, 30, 45, 60).forEach { mins ->
                FilterChip(
                    selected = sleepLeft == mins,
                    onClick = {
                        if (sleepLeft == mins) sceneController.cancelSleepTimer()
                        else sceneController.startSleepTimer(mins)
                    },
                    label = { Text("${mins}m", fontSize = 11.sp) },
                    colors = chipColors()
                )
            }
            if (sleepLeft > 0 && sleepLeft !in listOf(15, 30, 45, 60)) {
                FilterChip(
                    selected = true,
                    onClick = { sceneController.cancelSleepTimer() },
                    label = { Text("Timer ${sleepLeft}m", fontSize = 11.sp) },
                    colors = chipColors()
                )
            }
        }
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ImmersiveSurfaceActive,
    selectedLabelColor = ImmersiveLavenderAccent,
    labelColor = ImmersiveTextSecondary
)

@Composable
private fun SceneChip(scene: ListeningScene, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) ImmersiveSurfaceActive else ImmersiveSurface)
            .border(1.dp, if (selected) ImmersiveLavenderAccent else ImmersiveBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("scene_" + scene.id)
    ) {
        Text(scene.emoji + "  " + scene.name, color = ImmersiveTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(scene.description, color = ImmersiveTextSecondary, fontSize = 10.sp)
    }
}
