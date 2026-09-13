package com.example.ui

import android.widget.Toast
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
import com.example.data.AppEqMemory
import com.example.data.NowPlayingApp
import com.example.media.AppVolumeCap
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

/** Eén chip-rij: volume-cap + per-app EQ voor de huidige speel-app. */
@Composable
fun AppMixBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    val memory = remember { AppEqMemory(context) }
    var eqOn by remember { mutableStateOf(memory.enabled) }
    var volOn by remember { mutableStateOf(AppVolumeCap.enabled(context)) }
    val pkg = NowPlayingApp.packageName
    var frac by remember { mutableStateOf(AppVolumeCap.fraction(context, pkg)) }
    val bindings = remember(eqOn) { memory.listBindings() }
    val current = bindings.firstOrNull { it.packageName == pkg }
    val label = pkg?.substringAfterLast('.') ?: "app"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("app_mix_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = volOn || eqOn,
            onClick = {
                val next = !(volOn || eqOn)
                volOn = next
                eqOn = next
                AppVolumeCap.setEnabled(context, next)
                memory.enabled = next
            },
            label = { Text(if (volOn || eqOn) "App-mix aan" else "App-mix", fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("app_mix_toggle")
        )
        if (volOn && pkg != null) {
            FilterChip(
                selected = frac < 0.99f,
                onClick = { frac = AppVolumeCap.cycleFraction(context, pkg) },
                label = { Text("Vol $label ${(frac * 100).toInt()}%", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary,
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("app_mix_vol")
            )
        }
        if (eqOn && current != null) {
            FilterChip(
                selected = true,
                onClick = {
                    memory.load(current.packageName, ignoreEnabled = true)?.let {
                        viewModel.applyPreset(it)
                        Toast.makeText(context, "EQ: ${current.label}", Toast.LENGTH_SHORT).show()
                    }
                },
                label = { Text("EQ ${current.label} ${current.strength}%", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.25f),
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("app_mix_eq")
            )
        }
    }
}
