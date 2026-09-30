package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DoorListen
import com.example.data.FeatureFinder
import com.example.data.HearingGuard
import com.example.data.HoldSolo
import com.example.data.StreetListen
import com.example.data.TalkSoft
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

/** Zoekveld op home: holds/scenes op trefwoord, tap = cycle. */
@Composable
fun FeatureFinderBar() {
    val context = LocalContext.current
    var query by remember { mutableStateOf(FeatureFinder.lastQuery(context)) }
    var status by remember { mutableStateOf("") }
    val hits = remember(query) { FeatureFinder.search(query) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("feature_finder_bar")
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                FeatureFinder.saveQuery(context, it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("feature_finder_input"),
            singleLine = true,
            placeholder = { Text("Zoek hold of scene…", fontSize = 12.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ImmersiveLavenderAccent,
                unfocusedBorderColor = ImmersiveSurfaceActive,
                focusedTextColor = ImmersiveLavenderAccent,
                unfocusedTextColor = ImmersiveTextSecondary,
                cursorColor = ImmersiveLavenderAccent
            )
        )
        if (status.isNotEmpty()) {
            Text(status, color = ImmersiveLavenderAccent, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = HoldSolo.enabled(context),
                onClick = { status = FeatureFinder.cycle(context, "solo") },
                label = { Text("Solo", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveTextSecondary
                ),
                modifier = Modifier.testTag("feature_hit_solo")
            )
            FilterChip(
                selected = HearingGuard.enabled(context) && HearingGuard.hardCap(context),
                onClick = { status = FeatureFinder.cycle(context, "safe") },
                label = { Text("Veilig", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("feature_hit_safe")
            )
            FilterChip(
                selected = TalkSoft.active(context),
                onClick = { status = FeatureFinder.cycle(context, "talk") },
                label = { Text("Gesprek", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveTextSecondary
                ),
                modifier = Modifier.testTag("feature_hit_talk")
            )
            FilterChip(
                selected = DoorListen.active(context),
                onClick = { status = FeatureFinder.cycle(context, "door") },
                label = { Text("Deur", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveTextSecondary
                ),
                modifier = Modifier.testTag("feature_hit_door")
            )
            FilterChip(
                selected = StreetListen.active(context),
                onClick = { status = FeatureFinder.cycle(context, "street") },
                label = { Text("Straat", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveTextSecondary
                ),
                modifier = Modifier.testTag("feature_hit_street")
            )
            FilterChip(
                selected = false,
                onClick = { status = FeatureFinder.cycle(context, "panic") },
                label = { Text("Alles uit", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("feature_hit_panic")
            )
            hits.filter { it.id != "panic" && it.id != "safe" && it.id != "talk" && it.id != "door" && it.id != "street" }.take(8).forEach { hit ->
                FilterChip(
                    selected = false,
                    onClick = { status = FeatureFinder.cycle(context, hit.id) },
                    label = { Text(hit.title, fontSize = 11.sp, maxLines = 1) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = ImmersiveSurfaceActive,
                        labelColor = ImmersiveTextSecondary
                    ),
                    modifier = Modifier.testTag("feature_hit_${hit.id}")
                )
            }
        }
    }
}
