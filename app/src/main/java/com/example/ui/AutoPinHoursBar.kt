package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    var showPicker by remember { mutableStateOf(false) }
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
            onClick = { showPicker = true },
            label = { Text(hoursLabel, fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("auto_pin_hours_chip")
        )
        AssistChip(
            onClick = {
                hoursLabel = AutoPinSchedule.cycleHours(context)
                autoLabel = AutoPinSchedule.label(context)
            },
            label = { Text("Preset", fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary
            ),
            modifier = Modifier.testTag("auto_pin_hours_preset")
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
    if (showPicker) {
        AutoPinHoursDialog(
            onDismiss = { showPicker = false },
            onSaved = {
                hoursLabel = AutoPinSchedule.hoursLabel(context)
                autoLabel = AutoPinSchedule.label(context)
                showPicker = false
            }
        )
    }
}

@Composable
private fun AutoPinHoursDialog(
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    var workStart by remember { mutableFloatStateOf(AutoPinSchedule.workStart(context).toFloat()) }
    var workEnd by remember { mutableFloatStateOf(AutoPinSchedule.workEnd(context).toFloat()) }
    var travelStart by remember { mutableFloatStateOf(AutoPinSchedule.travelStart(context).toFloat()) }
    var travelEnd by remember { mutableFloatStateOf(AutoPinSchedule.travelEnd(context).toFloat()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Auto-pin uren") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HourSlider("Werk van", workStart) { workStart = it }
                HourSlider("Werk tot", workEnd) { workEnd = it }
                HourSlider("Weg van", travelStart) { travelStart = it }
                HourSlider("Weg tot", travelEnd) { travelEnd = it }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    AutoPinSchedule.setHours(
                        context,
                        workStart.toInt(),
                        workEnd.toInt(),
                        travelStart.toInt(),
                        travelEnd.toInt()
                    )
                    onSaved()
                },
                modifier = Modifier.testTag("auto_pin_hours_save")
            ) { Text("Opslaan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuleren") }
        }
    )
}

@Composable
private fun HourSlider(label: String, value: Float, onChange: (Float) -> Unit) {
    Column {
        Text("$label ${value.toInt()}u", fontSize = 13.sp, color = ImmersiveTextSecondary)
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..23f,
            steps = 22
        )
    }
}
