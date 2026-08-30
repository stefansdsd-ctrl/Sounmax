package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun GattInsightBar(sceneController: SceneController) {
    val status by sceneController.headsetStatus.collectAsStateWithLifecycle()
    if (!status.connected && status.discoveryLogs.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = when {
                            status.gattReady -> "GATT ${status.knownServices} bekend / ${status.unknownServices} nieuw"
                            status.connected -> "GATT verbindt…"
                            else -> "Geen headset"
                        },
                        fontSize = 11.sp
                    )
                }
            )
            status.rssiDbm?.let { rssi ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (status.rssiLiveGatt) "RSSI $rssi dBm live" else "RSSI $rssi dBm",
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }
        status.discoveryLogs.take(3).forEach { log ->
            Text(
                text = "${log.title}: ${log.detail}",
                fontSize = 10.sp,
                color = ImmersiveTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
