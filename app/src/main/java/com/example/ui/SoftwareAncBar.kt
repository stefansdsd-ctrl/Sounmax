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

    Card(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Software ANC", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
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
                Text("Ruis: ${noiseDb.toInt()} dB  |  SNR: ${snr.toInt()} dB", style = MaterialTheme.typography.bodySmall)
                Text(if (isSpeech) "Spraak gedetecteerd" else "Achtergrondruis", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = strength,
                    onValueChange = { controller.setStrength(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Sterkte: ${(strength * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            } else if (!hasPermission) {
                Text("RECORD_AUDIO permissie nodig voor Software ANC", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Software ANC uit", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
