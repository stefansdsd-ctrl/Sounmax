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
import com.example.data.HandoffDuck
import com.example.data.MorningDuck
import com.example.data.ResumeDuck
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
    var handoffOn by remember { mutableStateOf(HandoffDuck.enabled(context)) }
    var handoffLive by remember { mutableStateOf(HandoffDuck.active(context)) }
    var resumeOn by remember { mutableStateOf(ResumeDuck.enabled(context)) }
    var resumeLive by remember { mutableStateOf(ResumeDuck.active(context)) }
    var morningOn by remember { mutableStateOf(MorningDuck.enabled(context)) }
    var morningLive by remember { mutableStateOf(MorningDuck.active(context)) }

    LaunchedEffect(active, handoffOn, resumeOn, morningOn) {
        while (active || handoffOn || resumeOn || morningOn) {
            delay(500)
            active = VolumeDuck.isActive(context)
            left = VolumeDuck.remainingSec(context)
            handoffLive = HandoffDuck.active(context)
            resumeLive = ResumeDuck.active(context)
            morningLive = MorningDuck.active(context)
            if (!active && !handoffOn && !resumeOn && !morningOn) break
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
        FilterChip(
            selected = handoffOn,
            onClick = {
                Toast.makeText(context, HandoffDuck.cycle(context), Toast.LENGTH_SHORT).show()
                handoffOn = HandoffDuck.enabled(context)
                handoffLive = HandoffDuck.active(context)
            },
            label = {
                Text(
                    if (handoffLive) "Wissel 62%" else if (handoffOn) "Wissel aan" else "Wissel",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            },
            colors = colors(),
            modifier = Modifier.testTag("handoff_chip")
        )
        FilterChip(
            selected = resumeOn,
            onClick = {
                Toast.makeText(context, ResumeDuck.cycle(context), Toast.LENGTH_SHORT).show()
                resumeOn = ResumeDuck.enabled(context)
                resumeLive = ResumeDuck.active(context)
            },
            label = {
                Text(
                    if (resumeLive) "Hervat 68%" else if (resumeOn) "Hervat aan" else "Hervat",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            },
            colors = colors(),
            modifier = Modifier.testTag("resume_chip")
        )
        FilterChip(
            selected = morningOn,
            onClick = {
                Toast.makeText(context, MorningDuck.cycle(context), Toast.LENGTH_SHORT).show()
                morningOn = MorningDuck.enabled(context)
                morningLive = MorningDuck.active(context)
            },
            label = {
                Text(
                    if (morningLive) "Ochtend 48%" else if (morningOn) "Ochtend aan" else "Ochtend",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            },
            colors = colors(),
            modifier = Modifier.testTag("morning_chip")
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
