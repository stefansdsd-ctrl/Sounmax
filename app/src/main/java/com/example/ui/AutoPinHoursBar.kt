package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.example.data.AutoPinSchedule
import com.example.data.PinProfiles
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun AutoPinHoursBar() {
    val context = LocalContext.current
    var hoursLabel by remember { mutableStateOf(AutoPinSchedule.hoursLabel(context)) }
    var weekendHome by remember { mutableStateOf(AutoPinSchedule.weekendHome(context)) }
    var autoLabel by remember { mutableStateOf(AutoPinSchedule.label(context)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("auto_pin_hours_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = {
                hoursLabel = AutoPinSchedule.cycleHours(context)
                autoLabel = AutoPinSchedule.label(context)
            },
            label = { Text(hoursLabel, fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("auto_pin_hours_chip")
        )
        FilterChip(
            selected = weekendHome,
            onClick = {
                weekendHome = AutoPinSchedule.toggleWeekendHome(context)
                autoLabel = AutoPinSchedule.label(context)
            },
            label = { Text(if (weekendHome) "Za/zo Thuis" else "Za/zo schema", fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("weekend_home_chip")
        )
        Text(autoLabel, fontSize = 11.sp, color = ImmersiveTextSecondary)
        Text(PinProfiles.label(context), fontSize = 11.sp, color = ImmersiveTextSecondary)
    }
}
