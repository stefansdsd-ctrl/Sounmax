package com.example.ui

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
import com.example.media.QuietHours
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.widget.SoundMaxWidget

@Composable
fun QuietHoursBar() {
    val context = LocalContext.current
    var label by remember { mutableStateOf(QuietHours.label(context)) }
    var quietNow by remember { mutableStateOf(QuietHours.isQuietNow(context)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("quiet_hours_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Stil", fontSize = 11.sp, color = ImmersiveTextSecondary)
        FilterChip(
            selected = QuietHours.enabled(context),
            onClick = {
                label = QuietHours.cycle(context)
                quietNow = QuietHours.isQuietNow(context)
                if (quietNow) QuietHours.enforce(context)
                SoundMaxWidget.refreshAll(context)
            },
            label = { Text(label, fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("quiet_hours_chip")
        )
        if (quietNow) {
            Text("nu actief", fontSize = 11.sp, color = ImmersiveLavenderAccent)
        }
    }
}
