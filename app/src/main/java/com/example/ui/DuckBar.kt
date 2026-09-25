package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.dsp.HearingDoseGuard
import com.example.dsp.VolumeDuck
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.delay

@Composable
fun DuckBar() {
    val context = LocalContext.current
    var active by remember { mutableStateOf(VolumeDuck.isActive(context)) }
    var left by remember { mutableIntStateOf(VolumeDuck.remainingSec(context)) }
    var autoCap by remember { mutableStateOf(HearingDoseGuard.autoCapEnabled(context)) }

    LaunchedEffect(active) {
        while (active) {
            delay(500)
            active = VolumeDuck.isActive(context)
            left = VolumeDuck.remainingSec(context)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("duck_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = active,
            onClick = {
                val on = VolumeDuck.toggle(context)
                active = on
                left = VolumeDuck.remainingSec(context)
                Toast.makeText(
                    context,
                    if (on) "Omroep: volume 15s omlaag" else "Volume hersteld",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = {
                Text(
                    if (active) "Omroep ${left}s" else "Omroep 15s",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            },
            colors = colors(),
            modifier = Modifier.testTag("duck_chip")
        )
        FilterChip(
            selected = autoCap,
            onClick = {
                autoCap = HearingDoseGuard.toggleAutoCap(context)
                if (autoCap) HearingDoseGuard.applyCap(context)
                Toast.makeText(
                    context,
                    if (autoCap) "Auto-cap aan" else "Auto-cap uit",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = { Text(if (autoCap) "Auto-cap aan" else "Auto-cap", fontSize = 11.sp, maxLines = 1) },
            colors = colors(),
            modifier = Modifier.testTag("auto_cap_chip")
        )
    }
}

@Composable
private fun colors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary,
    selectedLabelColor = ImmersiveLavenderAccent
)
