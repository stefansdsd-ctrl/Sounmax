package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SceneUsage
import com.example.media.NightVolumeGuard
import com.example.media.WeatherSceneHint
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SmartSuggestBar(sceneController: SceneController) {
    val context = LocalContext.current
    val suggest = remember { SceneUsage.suggestNow(context) }
    val commuteLabel = remember { sceneController.commuteSuggestLabel() }
    var weatherLabel by remember { mutableStateOf(sceneController.weatherSuggestLabel()) }
    LaunchedEffect(Unit) {
        val hint = withContext(Dispatchers.IO) { WeatherSceneHint.refresh(context) }
        weatherLabel = hint?.label
    }
    var nightOn by remember { mutableStateOf(NightVolumeGuard.enabled(context)) }
    var locked by remember { mutableStateOf(sceneController.sceneLocked.value) }
    val nightNow = NightVolumeGuard.isNight()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("smart_suggest_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (commuteLabel != null) {
            AssistChip(
                onClick = { sceneController.applyCommuteSuggestion() },
                label = { Text(commuteLabel, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("commute_suggest_chip")
            )
        }
        if (weatherLabel != null) {
            AssistChip(
                onClick = { sceneController.applyWeatherSuggestion() },
                label = { Text(weatherLabel!!, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("weather_suggest_chip")
            )
        }
        if (suggest != null) {
            AssistChip(
                onClick = { sceneController.applyListeningScene(suggest) },
                label = { Text("Nu: ${suggest.name}", fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("smart_suggest_chip")
            )
        }
        FilterChip(
            selected = nightOn,
            onClick = {
                nightOn = !nightOn
                NightVolumeGuard.setEnabled(context, nightOn)
                if (nightOn) NightVolumeGuard.applyIfNeeded(context)
            },
            label = {
                Text(
                    if (nightNow) "Nacht-cap 50%" else "Nacht-cap",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("night_volume_chip")
        )
        FilterChip(
            selected = locked,
            onClick = {
                locked = !locked
                sceneController.setSceneLocked(locked)
            },
            label = { Text(if (locked) "Scene vast" else "Lock", fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("scene_lock_chip")
        )
    }
}
