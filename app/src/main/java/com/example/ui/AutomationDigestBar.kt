package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.AutomationDigest
import com.example.data.DoseAutoPause
import com.example.data.RainHold
import com.example.data.SchedulePause
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.widget.SoundMaxWidget

@Composable
fun AutomationDigestBar() {
    val context = LocalContext.current
    var digest by remember { mutableStateOf(AutomationDigest.summary(context)) }
    var pause by remember { mutableStateOf(SchedulePause.label(context)) }
    var rain by remember { mutableStateOf(RainHold.label(context)) }
    var dose by remember { mutableStateOf(DoseAutoPause.label(context)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("automation_digest_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Auto", fontSize = 11.sp, color = ImmersiveTextSecondary)
        FilterChip(
            selected = SchedulePause.active(context),
            onClick = {
                pause = SchedulePause.cycle(context)
                digest = AutomationDigest.summary(context)
                SoundMaxWidget.refreshAll(context)
            },
            label = { Text(pause, fontSize = 11.sp, maxLines = 1) },
            colors = chip(),
            modifier = Modifier.testTag("schedule_pause_chip")
        )
        FilterChip(
            selected = RainHold.active(context),
            onClick = {
                rain = RainHold.cycle(context)
                digest = AutomationDigest.summary(context)
                SoundMaxWidget.refreshAll(context)
            },
            label = { Text(rain, fontSize = 11.sp, maxLines = 1) },
            colors = chip(),
            modifier = Modifier.testTag("rain_hold_chip")
        )
        FilterChip(
            selected = DoseAutoPause.enabled(context),
            onClick = {
                DoseAutoPause.toggle(context)
                dose = DoseAutoPause.label(context)
                SoundMaxWidget.refreshAll(context)
            },
            label = { Text(dose, fontSize = 11.sp, maxLines = 1) },
            colors = chip(),
            modifier = Modifier.testTag("dose_auto_pause_chip")
        )
        Text(digest, fontSize = 11.sp, color = ImmersiveLavenderAccent, maxLines = 1)
    }
}

@Composable
private fun chip() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary,
    selectedLabelColor = ImmersiveLavenderAccent
)
