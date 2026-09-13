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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.FocusSession
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun FocusBar() {
    val context = LocalContext.current
    var on by remember { mutableStateOf(FocusSession.isActive(context)) }
    var left by remember { mutableStateOf((FocusSession.remainingMs(context) / 60_000L).toInt()) }
    var mins by remember { mutableStateOf(FocusSession.lastMinutes(context)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("focus_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = on,
            onClick = {
                if (on) {
                    FocusSession.cancel(context)
                    on = false
                    left = 0
                } else {
                    FocusSession.start(context, mins)
                    on = true
                    left = mins
                }
            },
            label = {
                Text(
                    if (on) "Focus ${left}m" else "Focus",
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
            modifier = Modifier.testTag("focus_chip")
        )
        listOf(25, 45, 90).forEach { m ->
            FilterChip(
                selected = mins == m && on,
                onClick = {
                    mins = m
                    FocusSession.start(context, m)
                    on = true
                    left = m
                },
                label = { Text("${m}m", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary,
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("focus_${m}_chip")
            )
        }
    }
}
