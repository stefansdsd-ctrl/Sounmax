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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HomePins
import com.example.data.LastSceneRestore
import com.example.data.PinProfiles
import com.example.data.SceneUsage
import com.example.dsp.AncMode
import com.example.dsp.HearingDoseGuard
import com.example.dsp.SceneLookup
import com.example.media.HourSceneSuggest
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
    val hourSuggest = remember { if (suggest == null) HourSceneSuggest.suggest(context) else null }
    val commuteLabel = remember { sceneController.commuteSuggestLabel() }
    var holdLabel by remember { mutableStateOf(sceneController.manualHoldLabel()) }
    var weatherLabel by remember { mutableStateOf(sceneController.weatherSuggestLabel()) }
    val batteryLabel = remember { sceneController.batterySaverLabel() }
    val dose = remember { HearingDoseGuard.adviceNow(context) }
    val undoLabel = remember { sceneController.undoLabel() }
    val bestNowLabel = remember { sceneController.bestNowLabel() }
    val favs = remember { sceneController.favoriteScenes() }
    val lastLabel = remember { LastSceneRestore.label(context) }
    val lastScene = remember { LastSceneRestore.scene(context) }
    var pinSetLabel by remember { mutableStateOf(PinProfiles.label(context)) }
    val ancModes = remember {
        listOf(AncMode.STRONG, AncMode.ADAPTIVE, AncMode.WIND_GUARD, AncMode.AMBIENT)
    }
    var ancIdx by remember { mutableIntStateOf(1) }
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
        AssistChip(
            onClick = {
                val p = PinProfiles.cycle(context)
                pinSetLabel = "Set: ${p.name}"
                HomePins.scenes(context).firstOrNull()?.let { sceneController.applyListeningScene(it) }
            },
            label = { Text(pinSetLabel, fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("pin_profile_chip")
        )
        AssistChip(
            onClick = {
                HomePins.next(context, sceneController.activeSceneId.value)
                    ?.let { sceneController.applyListeningScene(it) }
            },
            label = { Text("Pin-wissel", fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("pin_cycle_chip")
        )
        if (lastScene != null && lastLabel != null) {
            AssistChip(
                onClick = { sceneController.applyListeningScene(lastScene) },
                label = { Text(lastLabel, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("last_scene_chip")
            )
        }
        if (dose.suggestPause) {
            AssistChip(
                onClick = {
                    HearingDoseGuard.applyCap(context)
                    SceneLookup.byId("oorpauze")?.let { sceneController.applyListeningScene(it) }
                },
                label = { Text(dose.message, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("hearing_dose_chip")
            )
        }
        if (favs.isNotEmpty()) {
            AssistChip(
                onClick = {
                    val cur = sceneController.activeSceneId.value
                    val idx = favs.indexOfFirst { it.id == cur }
                    sceneController.applyListeningScene(favs[(idx + 1).coerceAtLeast(0) % favs.size])
                },
                label = { Text("Fav: ${favs.first().name}", fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("favorite_next_chip")
            )
        }
        AssistChip(
            onClick = { sceneController.applySafeListen() },
            label = { Text("Veilig", fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("safe_listen_chip")
        )
        AssistChip(
            onClick = {
                sceneController.setHardwareAnc(AncMode.WIND_GUARD)
                SceneLookup.byId("terrasavond")?.let { sceneController.applyListeningScene(it) }
                    ?: SceneLookup.byId("bike")?.let { sceneController.applyListeningScene(it) }
            },
            label = { Text("Wind", fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("wind_guard_chip")
        )
        AssistChip(
            onClick = {
                sceneController.setHardwareAnc(AncMode.STRONG)
                SceneLookup.byId("parkeergarage")?.let { sceneController.applyListeningScene(it) }
                    ?: SceneLookup.byId("zwembadhal")?.let { sceneController.applyListeningScene(it) }
            },
            label = { Text("Echo", fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("echo_anc_chip")
        )
        if (bestNowLabel != null) {
            AssistChip(
                onClick = { sceneController.applyBestNow() },
                label = { Text(bestNowLabel, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("best_now_chip")
            )
        }
        AssistChip(
            onClick = {
                ancIdx = (ancIdx + 1) % ancModes.size
                sceneController.setHardwareAnc(ancModes[ancIdx])
            },
            label = { Text("ANC: ${ancModes[ancIdx].name.lowercase()}", fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("anc_cycle_chip")
        )
        if (undoLabel != null) {
            AssistChip(
                onClick = { sceneController.undoLastScene() },
                label = { Text(undoLabel, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("undo_scene_chip")
            )
        }
        if (holdLabel != null) {
            AssistChip(
                onClick = {
                    sceneController.clearManualHold()
                    holdLabel = null
                },
                label = { Text(holdLabel!!, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("manual_hold_chip")
            )
        }
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
        if (batteryLabel != null) {
            AssistChip(
                onClick = { sceneController.applyBatterySaver() },
                label = { Text(batteryLabel, fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("battery_saver_chip")
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
        if (hourSuggest != null && suggest == null) {
            AssistChip(
                onClick = { sceneController.applyListeningScene(hourSuggest) },
                label = { Text("Rond nu: ${hourSuggest.name}", fontSize = 11.sp, maxLines = 1) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("hour_suggest_chip")
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
