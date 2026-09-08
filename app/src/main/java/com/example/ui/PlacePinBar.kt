package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.GeofencePlaceAdvisor
import com.example.media.WifiPlaceAdvisor
import com.example.media.WifiRssiMap
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun PlacePinBar() {
    val context = LocalContext.current
    var status by remember { mutableStateOf(statusLine(context)) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    WifiPlaceAdvisor.pinCurrentAsHome(context)
                    status = statusLine(context)
                },
                modifier = Modifier.weight(1f).testTag("pin_home")
            ) {
                Text("Pin thuis", fontSize = 13.sp)
            }
            FilledTonalButton(
                onClick = {
                    WifiPlaceAdvisor.pinCurrentAsWork(context)
                    status = statusLine(context)
                },
                modifier = Modifier.weight(1f).testTag("pin_work")
            ) {
                Text("Pin werk", fontSize = 13.sp)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .testTag("room_pin_row"),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            WifiRssiMap.ROOMS.forEach { room ->
                FilterChip(
                    selected = WifiRssiMap.list(context).any { it.label == room.label },
                    onClick = {
                        WifiRssiMap.pinRoom(context, room.id)
                        status = statusLine(context)
                    },
                    label = { Text(room.label, fontSize = 11.sp, maxLines = 1) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                        containerColor = ImmersiveSurfaceActive,
                        labelColor = ImmersiveTextSecondary,
                        selectedLabelColor = ImmersiveLavenderAccent
                    ),
                    modifier = Modifier.testTag("pin_room_${room.id}")
                )
            }
        }
        Text(text = status, color = ImmersiveTextSecondary, fontSize = 12.sp)
    }
}

private fun statusLine(context: android.content.Context): String {
    val ssid = WifiPlaceAdvisor.currentSsid(context) ?: "wifi onbekend"
    val home = listOfNotNull(
        WifiPlaceAdvisor.homeSsid(context)?.let { "ssid $it" },
        if (GeofencePlaceAdvisor.hasHome(context)) "gps" else null
    ).joinToString("+").ifBlank { "—" }
    val work = listOfNotNull(
        WifiPlaceAdvisor.workSsid(context)?.let { "ssid $it" },
        if (GeofencePlaceAdvisor.hasWork(context)) "gps" else null
    ).joinToString("+").ifBlank { "—" }
    val rooms = WifiRssiMap.list(context).joinToString(",") { it.label }.ifBlank { "geen" }
    val match = WifiRssiMap.lastMatch(context).ifBlank { "—" }
    return "Nu: $ssid · thuis $home · werk $work · kamers $rooms · match $match"
}
