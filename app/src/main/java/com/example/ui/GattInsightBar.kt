package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsp.AncMode
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
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = when {
                            status.gattReady -> "GATT ${status.knownServices}/${status.unknownServices}"
                            status.connected -> "GATT…"
                            else -> "Geen headset"
                        },
                        fontSize = 11.sp
                    )
                }
            )
            status.rssiDbm?.let { rssi ->
                AssistChip(
                    onClick = {},
                    label = { Text("RSSI $rssi", fontSize = 11.sp) }
                )
            }
            if (status.ancStatus.isNotBlank()) {
                AssistChip(
                    onClick = {},
                    label = { Text(status.ancStatus.take(28), fontSize = 11.sp) }
                )
            }
            AssistChip(
                onClick = { sceneController.openNrfConnect() },
                label = { Text("nRF Connect", fontSize = 11.sp) }
            )
        }
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AssistChip(
                onClick = { sceneController.setHardwareAnc(AncMode.STRONG) },
                label = { Text("ANC aan", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.setHardwareAnc(AncMode.OFF) },
                label = { Text("ANC uit", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.setHardwareAnc(AncMode.AMBIENT) },
                label = { Text("Transparant", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.shareGattDump("baseline") },
                label = { Text("Dump baseline", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.shareGattDump("anc") },
                label = { Text("Dump ANC", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.shareGattDump("off") },
                label = { Text("Dump off", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.shareGattDump("awareness") },
                label = { Text("Dump transparant", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.importNrfClipboard() },
                label = { Text("Import nRF", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.diffNrfDumps("baseline", "anc") },
                label = { Text("Diff base/ANC", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = { sceneController.showNrfWorkflow() },
                label = { Text("nRF help", fontSize = 11.sp) }
            )
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
