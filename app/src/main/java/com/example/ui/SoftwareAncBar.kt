package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.SoftwareAncController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SoftwareAncBar(
    controller: SoftwareAncController,
    modifier: Modifier = Modifier
) {
    val enabled by controller.enabled.collectAsState()
    val noiseDb by controller.noiseLevelDb.collectAsState()
    val snr by controller.snrDb.collectAsState()
    val isSpeech by controller.isSpeech.collectAsState()
    val strength by controller.reductionStrength.collectAsState()

    Card(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Software ANC", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Switch(checked = enabled, onCheckedChange = { controller.setEnabled(it) })
            }
            if (enabled) {
                Text("Ruiz: ${noiseDb.toInt()} dB  |  SNR: ${snr.toInt()} dB", style = MaterialTheme.typography.bodySmall)
                Text(if (isSpeech) "Spraak gedetecteerd" else "Achtergrondruis", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = strength,
                    onValueChange = { controller.setStrength(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Sterkte: ${(strength * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Microfoon-ANC uit (RECORD_AUDIO nodig)", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
