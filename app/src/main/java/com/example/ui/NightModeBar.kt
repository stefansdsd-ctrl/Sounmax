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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.EarRestOneTap
import com.example.media.FindHeadset
import com.example.media.MorningBoostOneTap
import com.example.media.NightModeOneTap
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun NightModeBar() {
    val context = LocalContext.current
    val colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
        containerColor = ImmersiveSurfaceActive,
        labelColor = ImmersiveTextSecondary,
        selectedLabelColor = ImmersiveLavenderAccent
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("night_mode_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = false,
            onClick = {
                Toast.makeText(context, NightModeOneTap.apply(context), Toast.LENGTH_SHORT).show()
            },
            label = { Text("Nachtmodus", fontSize = 11.sp, maxLines = 1) },
            colors = colors,
            modifier = Modifier.testTag("night_mode_chip")
        )
        FilterChip(
            selected = false,
            onClick = {
                Toast.makeText(context, MorningBoostOneTap.apply(context), Toast.LENGTH_SHORT).show()
            },
            label = { Text("Ochtend", fontSize = 11.sp, maxLines = 1) },
            colors = colors,
            modifier = Modifier.testTag("morning_boost_chip")
        )
        FilterChip(
            selected = false,
            onClick = {
                Toast.makeText(context, EarRestOneTap.apply(context), Toast.LENGTH_SHORT).show()
            },
            label = { Text("Oorpauze", fontSize = 11.sp, maxLines = 1) },
            colors = colors,
            modifier = Modifier.testTag("ear_rest_chip")
        )
        FilterChip(
            selected = false,
            onClick = {
                FindHeadset.ping(context)
                Toast.makeText(context, "Zoek headset…", Toast.LENGTH_SHORT).show()
            },
            label = { Text("Zoek", fontSize = 11.sp, maxLines = 1) },
            colors = colors,
            modifier = Modifier.testTag("find_headset_home_chip")
        )
    }
}
