package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.SoftwareAncController

@Composable
fun SoftwareAncBar(
    controller: SoftwareAncController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val enabled by controller.enabled.collectAsState()
    val noiseDb by controller.noiseLevelDb.collectAsState()
    val snr by controller.snrDb.collectAsState()
    val isSpeech by controller.isSpeech.collectAsState()
    val strength by controller.reductionStrength.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) controller.setEnabled(true)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Software ANC",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = enabled && hasPermission,
                    onCheckedChange = { wantOn ->
                        if (wantOn) {
                            if (hasPermission) {
                                controller.setEnabled(true)
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        } else {
                            controller.setEnabled(false)
                        }
                    }
                )
            }

            if (enabled && hasPermission) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Ruis: ${noiseDb.toInt()} dB  |  SNR: ${snr.toInt()} dB",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    if (isSpeech) "Spraak gedetecteerd – ANC verminderd" else "Achtergrondruis – ANC actief",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(8.dp))
                Text("Sterkte", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = strength,
                    onValueChange = { controller.setStrength(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${(strength * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            } else if (!hasPermission) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "RECORD_AUDIO permissie nodig. Tik om te activeren.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Spacer(Modifier.height(4.dp))
                Text("Uit", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
