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
import com.example.media.CallOneTap
import com.example.media.FlightOneTap
import com.example.media.PodcastOneTap
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun ContextChipsBar() {
    val context = LocalContext.current
    var callOn by remember { mutableStateOf(CallOneTap.isOn(context)) }
    var podOn by remember { mutableStateOf(PodcastOneTap.isOn(context)) }
    var flightOn by remember { mutableStateOf(FlightOneTap.isOn(context)) }
    var flightLeft by remember { mutableStateOf(FlightOneTap.remainingMin(context)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("context_chips_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Chip(
            label = if (callOn) "Bel aan" else "Bel",
            selected = callOn,
            tag = "call_chip"
        ) {
            CallOneTap.toggle(context)
            callOn = CallOneTap.isOn(context)
        }
        Chip(
            label = if (podOn) "Podcast aan" else "Podcast",
            selected = podOn,
            tag = "podcast_chip"
        ) {
            PodcastOneTap.toggle(context)
            podOn = PodcastOneTap.isOn(context)
        }
        Chip(
            label = if (flightOn) "Vlucht ${flightLeft}m" else "Vliegtuig",
            selected = flightOn,
            tag = "flight_chip"
        ) {
            FlightOneTap.toggle(context)
            flightOn = FlightOneTap.isOn(context)
            flightLeft = FlightOneTap.remainingMin(context)
        }
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, tag: String, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
            containerColor = ImmersiveSurfaceActive,
            labelColor = ImmersiveTextSecondary,
            selectedLabelColor = ImmersiveLavenderAccent
        ),
        modifier = Modifier.testTag(tag)
    )
}
