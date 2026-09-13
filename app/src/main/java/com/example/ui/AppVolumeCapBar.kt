package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.NowPlayingApp
import com.example.media.AppVolumeCap
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun AppVolumeCapBar() {
    val context = LocalContext.current
    var on by remember { mutableStateOf(AppVolumeCap.enabled(context)) }
    val pkg = NowPlayingApp.packageName
    var frac by remember { mutableStateOf(AppVolumeCap.fraction(context, pkg)) }
    val label = pkg?.substringAfterLast('.') ?: "app"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("app_volume_cap_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = on,
            onClick = {
                on = !on
                AppVolumeCap.setEnabled(context, on)
            },
            label = { Text(if (on) "App-vol aan" else "App-vol", fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("app_volume_cap_toggle")
        )
        if (on && pkg != null) {
            FilterChip(
                selected = frac < 0.99f,
                onClick = {
                    frac = AppVolumeCap.cycleFraction(context, pkg)
                },
                label = { Text("$label ${(frac * 100).toInt()}%", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary,
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("app_volume_cap_frac")
            )
        }
    }
}
