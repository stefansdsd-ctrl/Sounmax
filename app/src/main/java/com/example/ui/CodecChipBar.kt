package com.example.ui

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.BatteryEta
import com.example.media.BtCodecProbe
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun CodecChipBar(
    batteryPercent: Int? = null,
    device: BluetoothDevice? = null,
    rssi: Int? = null,
) {
    val context = LocalContext.current
    val codec = remember(device) { BtCodecProbe.label(context, device) }
    val signal = remember(rssi) { BtCodecProbe.qualityFromRssi(rssi) }
    val eta = remember(batteryPercent) {
        BatteryEta.label(BatteryEta.etaMinutes(context, batteryPercent))
    }
    val parts = buildList {
        batteryPercent?.let { add("$it%") }
        eta?.let { add(it) }
        codec?.let { add(it) }
        signal?.let { add(it) }
    }
    if (parts.isEmpty()) return
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("codec_battery_chip"),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterChip(
            selected = false,
            onClick = { },
            enabled = false,
            label = { Text(parts.joinToString(" · "), fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                disabledContainerColor = ImmersiveSurfaceActive,
                disabledLabelColor = ImmersiveTextSecondary
            )
        )
    }
}
