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
import com.example.media.CalendarMeetingAdvisor
import com.example.media.MeetingSession
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun MeetingBar() {
    val context = LocalContext.current
    var on by remember { mutableStateOf(MeetingSession.isActive(context)) }
    var left by remember { mutableStateOf((MeetingSession.remainingMs(context) / 60_000L).toInt()) }
    var mins by remember { mutableStateOf(MeetingSession.lastMinutes(context)) }
    val hint = CalendarMeetingAdvisor.lastEventTitle
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).testTag("meeting_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = on,
            onClick = {
                MeetingSession.toggle(context)
                on = MeetingSession.isActive(context)
                left = (MeetingSession.remainingMs(context) / 60_000L).toInt()
            },
            label = {
                Text(
                    when {
                        on -> "Vergadering ${left}m"
                        !hint.isNullOrBlank() -> "Vergadering? ${hint.take(16)}"
                        else -> "Vergadering"
                    },
                    fontSize = 11.sp, maxLines = 1
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("meeting_chip")
        )
        listOf(15, 30, 60).forEach { m ->
            FilterChip(
                selected = mins == m && on,
                onClick = {
                    mins = m
                    MeetingSession.start(context, m)
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
                modifier = Modifier.testTag("meeting_${m}_chip")
            )
        }
    }
}
