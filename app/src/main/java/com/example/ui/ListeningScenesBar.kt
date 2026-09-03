package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
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
    val weekDose by sceneController.listeningMinutesWeek.collectAsStateWithLifecycle()
    val callTransparency by sceneController.callTransparency.collectAsStateWithLifecycle()
    val sceneGroup by sceneController.sceneGroup.collectAsStateWithLifecycle()
    val doseWarning by sceneController.doseWarning.collectAsStateWithLifecycle()
    val favoriteIds by sceneController.favoriteSceneIds.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val scenes = remember(query, sceneGroup, favoriteIds) { sceneController.filteredScenes(query, sceneGroup) }
    val recents = remember(activeSceneId) { sceneController.recentScenes() }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Luister-scenes · ${dose} min vandaag · ${weekDose} min deze week · tip ${suggested.emoji} ${suggested.name}",
            color = ImmersiveTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        doseWarning?.let {
            Text(it, color = ImmersiveLavenderAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(com.example.dsp.ListeningScenes.GROUPS, key = { it.first }) { (label, _) ->
                FilterChip(
                    selected = sceneGroup == label,
                    onClick = { sceneController.setSceneGroup(label) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = chipColors()
                )
            }
        }
        if (recents.isNotEmpty() && query.isBlank() && sceneGroup == "Alles") {
            Text("Recent", color = ImmersiveTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recents, key = { "r_" + it.id }) { scene ->
                    SceneChip(
                        scene = scene,
                        selected = activeSceneId == scene.id,
                        favorite = scene.id in favoriteIds,
                        onClick = { sceneController.applyListeningScene(scene) },
                        onLongClick = { sceneController.toggleFavoriteScene(scene.id) }
                    )
                }
            }
        }
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
                SceneChip(
                    scene = scene,
                    selected = activeSceneId == scene.id,
                    favorite = scene.id in favoriteIds,
                    onClick = { sceneController.applyListeningScene(scene) },
                    onLongClick = { sceneController.toggleFavoriteScene(scene.id) }
                )
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
            FilterChip(
                selected = callTransparency,
                onClick = { sceneController.setCallTransparency(!callTransparency) },
                label = { Text("Call-transparantie", fontSize = 11.sp) },
                colors = chipColors(),
                modifier = Modifier.testTag("call_transparency_chip")
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SceneChip(
    scene: ListeningScene,
    selected: Boolean,
    favorite: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) ImmersiveSurfaceActive else ImmersiveSurface)
            .border(1.dp, if (selected) ImmersiveLavenderAccent else ImmersiveBorder, RoundedCornerShape(14.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("scene_" + scene.id)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(scene.emoji + "  " + scene.name, color = ImmersiveTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Icon(
                imageVector = if (favorite) Icons.Default.Star else Icons.Outlined.Star,
                contentDescription = "Favoriet",
                tint = if (favorite) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Text(scene.description, color = ImmersiveTextSecondary, fontSize = 10.sp)
        Text("Houd ingedrukt voor ★", color = ImmersiveTextSecondary, fontSize = 9.sp)
    }
}
