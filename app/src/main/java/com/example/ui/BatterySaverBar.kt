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
import com.example.data.BatterySaverDsp
import com.example.data.QuietHours
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.widget.SoundMaxWidget

@Composable
fun BatterySaverBar() {
    val context = LocalContext.current
    var saverLabel by remember { mutableStateOf(BatterySaverDsp.label(context)) }
    var saverOn by remember { mutableStateOf(BatterySaverDsp.enabled(context)) }
    var saving by remember { mutableStateOf(BatterySaverDsp.shouldSave(context)) }
    var nightLabel by remember { mutableStateOf(QuietHours.label(context)) }
    var nightOn by remember { mutableStateOf(QuietHours.enabled(context)) }
    var nightNow by remember { mutableStateOf(QuietHours.activeNow(context)) }

    val colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
        containerColor = ImmersiveSurfaceActive,
        labelColor = ImmersiveTextSecondary,
        selectedLabelColor = ImmersiveLavenderAccent
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("battery_saver_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = saverOn,
            onClick = {
                saverOn = BatterySaverDsp.toggle(context)
                saverLabel = BatterySaverDsp.label(context)
                saving = BatterySaverDsp.shouldSave(context)
                SoundMaxWidget.refreshAll(context)
                Toast.makeText(context, saverLabel, Toast.LENGTH_SHORT).show()
            },
            label = { Text(saverLabel, fontSize = 11.sp, maxLines = 1) },
            colors = colors,
            modifier = Modifier.testTag("battery_saver_chip")
        )
        FilterChip(
            selected = nightOn,
            onClick = {
                nightOn = QuietHours.toggle(context)
                nightLabel = QuietHours.label(context)
                nightNow = QuietHours.activeNow(context)
                SoundMaxWidget.refreshAll(context)
                Toast.makeText(context, nightLabel, Toast.LENGTH_SHORT).show()
            },
            label = { Text(nightLabel, fontSize = 11.sp, maxLines = 1) },
            colors = colors,
            modifier = Modifier.testTag("night_cap_chip")
        )
        if (saving) Text("spaar", fontSize = 11.sp, color = ImmersiveLavenderAccent)
        if (nightNow) Text("nacht", fontSize = 11.sp, color = ImmersiveLavenderAccent)
    }
}
