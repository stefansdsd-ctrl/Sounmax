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

@OptIn(ExperimentalFoundationApi::class)
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
    val favorites by sceneController.favoriteIds.collectAsStateWithLifecycle()
    val crossfeed by sceneController.crossfeedEnabled.collectAsStateWithLifecycle()
    val eqLocked by sceneController.eqLocked.collectAsStateWithLifecycle()
    val monoMix by sceneController.monoMix.collectAsStateWithLifecycle()
    val nightGuard by sceneController.nightGuard.collectAsStateWithLifecycle()
    val headset by sceneController.detectedHeadset.collectAsStateWithLifecycle()
    val scenes = sceneController.orderedScenes()

    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = ImmersiveSurfaceActive,
        selectedLabelColor = ImmersiveLavenderAccent,
        labelColor = ImmersiveTextSecondary
    )

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
                text = (headset?.let { "BT · ${it.take(18)}  ·  " } ?: "") +
                    "Vandaag ${doseMin} min" + if (doseMin >= 120) " ⚠" else "",
                color = if (doseMin >= 120) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                fontSize = 11.sp
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
            modifier = Modifier.testTag("listening_scenes_row")
        ) {
            items(scenes, key = { it.id }) { scene ->
                val selected = activeSceneId == scene.id
                val pinned = scene.id in favorites
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) ImmersiveSurfaceActive else ImmersiveSurface)
                        .border(
                            1.dp,
                            if (selected) ImmersiveLavenderAccent else ImmersiveBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .combinedClickable(
                            onClick = { sceneController.applyListeningScene(scene) },
                            onLongClick = { sceneController.toggleFavorite(scene.id) }
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("scene_" + scene.id)
                ) {
                    Text(
                        (if (pinned) "★ " else "") + scene.emoji + "  " + scene.name,
                        color = ImmersiveTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(scene.description, color = ImmersiveTextSecondary, fontSize = 10.sp)
                }
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
            modifier = Modifier.testTag("wellness_chips_row")
        ) {
            item {
                FilterChip(
                    selected = autoScene,
                    onClick = { sceneController.setAutoSceneEnabled(!autoScene) },
                    label = { Text("Auto ${suggested.emoji}", fontSize = 11.sp) },
                    colors = chipColors,
                    modifier = Modifier.testTag("auto_scene_chip")
                )
            }
            item {
                FilterChip(
                    selected = safeVolume,
                    onClick = { sceneController.setSafeVolume(!safeVolume) },
                    label = { Text("Veilig volume", fontSize = 11.sp) },
                    colors = chipColors,
                    modifier = Modifier.testTag("safe_volume_chip")
                )
            }
            item {
                FilterChip(
                    selected = nightGuard,
                    onClick = { sceneController.setNightGuard(!nightGuard) },
                    label = { Text("Nachtwacht", fontSize = 11.sp) },
                    colors = chipColors,
                    modifier = Modifier.testTag("night_guard_chip")
                )
            }
            item {
                FilterChip(
                    selected = crossfeed,
                    onClick = { sceneController.setCrossfeed(!crossfeed) },
                    label = { Text("Crossfeed", fontSize = 11.sp) },
                    colors = chipColors,
                    modifier = Modifier.testTag("crossfeed_chip")
                )
            }
            item {
                FilterChip(
                    selected = monoMix,
                    onClick = { sceneController.setMonoMix(!monoMix) },
                    label = { Text(if (monoMix) "Mono" else "Stereo", fontSize = 11.sp) },
                    colors = chipColors,
                    modifier = Modifier.testTag("mono_mix_chip")
                )
            }
            item {
                FilterChip(
                    selected = eqLocked,
                    onClick = { sceneController.setEqLocked(!eqLocked) },
                    label = { Text(if (eqLocked) "EQ slot" else "EQ lock", fontSize = 11.sp) },
                    colors = chipColors,
                    modifier = Modifier.testTag("eq_lock_chip")
                )
            }
            item {
                FilterChip(
                    selected = activeSceneId == "rest",
                    onClick = { ListeningScenes.byId("rest")?.let { sceneController.applyListeningScene(it) } },
                    label = { Text("Oor-pauze", fontSize = 11.sp) },
                    colors = chipColors,
                    modifier = Modifier.testTag("ear_rest_chip")
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { sceneController.detectHeadset() },
                    label = { Text("Detecteer BT", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(labelColor = ImmersiveTextPrimary),
                    modifier = Modifier.testTag("detect_bt_chip")
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { sceneController.shareCurrentEq() },
                    label = { Text("Deel EQ", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(labelColor = ImmersiveTextPrimary),
                    modifier = Modifier.testTag("share_eq_chip")
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { sceneController.importEqFromClipboard() },
                    label = { Text("Importeer EQ", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(labelColor = ImmersiveTextPrimary),
                    modifier = Modifier.testTag("import_eq_chip")
                )
            }
            items(listOf(15, 30, 60, 90, 120)) { mins ->
                FilterChip(
                    selected = sleepLeft == mins,
                    onClick = {
                        if (sleepLeft == mins) sceneController.cancelSleepTimer()
                        else sceneController.startSleepTimer(mins)
                    },
                    label = { Text("${mins}m", fontSize = 11.sp) },
                    colors = chipColors
                )
            }
            if (sleepLeft > 0 && sleepLeft !in listOf(15, 30, 60, 90, 120)) {
                item {
                    FilterChip(
                        selected = true,
                        onClick = { sceneController.cancelSleepTimer() },
                        label = { Text("Timer ${sleepLeft}m", fontSize = 11.sp) },
                        colors = chipColors
                    )
                }
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { sceneController.snapshotEq("A") },
                    label = { Text("EQ A", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(labelColor = ImmersiveTextSecondary)
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { sceneController.snapshotEq("B") },
                    label = { Text("EQ B", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(labelColor = ImmersiveTextSecondary)
                )
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { sceneController.toggleEqAb() },
                    label = { Text("A/B", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(labelColor = ImmersiveTextPrimary),
                    modifier = Modifier.testTag("ab_toggle")
                )
            }
        }
    }
}
