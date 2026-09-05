package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
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
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun PlacePinBar() {
    val context = LocalContext.current
    var status by remember {
        mutableStateOf(statusLine(context))
    }
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
    return "Nu: $ssid · thuis $home · werk $work"
}
