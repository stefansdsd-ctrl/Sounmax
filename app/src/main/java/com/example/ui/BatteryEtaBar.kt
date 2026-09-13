package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.BatteryEta
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun BatteryEtaBar(batteryPercent: Int?) {
    val context = LocalContext.current
    val label = remember(batteryPercent) {
        BatteryEta.label(BatteryEta.etaMinutes(context, batteryPercent))
    } ?: return
    FilterChip(
        selected = false,
        onClick = { },
        enabled = false,
        label = { Text(label, fontSize = 11.sp, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            disabledContainerColor = ImmersiveSurfaceActive,
            disabledLabelColor = ImmersiveTextSecondary
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("battery_eta_chip")
    )
}
