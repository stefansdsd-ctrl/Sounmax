package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.media.MultipointSwitcher
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.delay

@Composable
fun MultipointBar() {
    val context = LocalContext.current
    var sinks by remember { mutableStateOf(MultipointSwitcher.refresh(context)) }
    LaunchedEffect(Unit) {
        while (true) {
            sinks = MultipointSwitcher.refresh(context)
            delay(8_000)
        }
    }
    if (sinks.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("multipoint_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sinks, key = { it.address }) { sink ->
                val batt = sink.batteryPercent?.let { " $it%" } ?: ""
                FilterChip(
                    selected = sink.connected,
                    onClick = {
                        val ok = MultipointSwitcher.switchTo(context, sink.address)
                        Toast.makeText(
                            context,
                            if (ok) "Wissel → ${sink.name}" else "Wissel mislukt",
                            Toast.LENGTH_SHORT
                        ).show()
                        sinks = MultipointSwitcher.refresh(context)
                    },
                    label = {
                        Text(
                            "${if (sink.connected) "● " else ""}${sink.name}$batt",
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
                    modifier = Modifier.testTag("multipoint_${sink.address}")
                )
            }
        }
    }
}
