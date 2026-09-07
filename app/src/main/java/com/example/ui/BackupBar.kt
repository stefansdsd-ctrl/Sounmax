package com.example.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppEqMemory
import com.example.data.NowPlayingApp
import com.example.data.PresetBackup
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.launch

@Composable
fun BackupBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val memory = remember { AppEqMemory(context) }
    val appLabel = memory.label(NowPlayingApp.packageName)

    val createDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(PresetBackup.MIME)
    ) { uri ->
        if (uri != null) scope.launch { PresetBackup.writeToUri(context, uri) }
    }
    val openDoc = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) scope.launch { PresetBackup.importFromUri(context, uri) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = { createDoc.launch("sounmax-backup.json") },
            label = { Text("Bestand opslaan", fontSize = 11.sp) },
            colors = chipColors()
        )
        AssistChip(
            onClick = { openDoc.launch(arrayOf("application/json", "text/*", "*/*")) },
            label = { Text("Bestand openen", fontSize = 11.sp) },
            colors = chipColors()
        )
        AssistChip(
            onClick = { scope.launch { PresetBackup.exportToClipboard(context) } },
            label = { Text("Deel / klembord", fontSize = 11.sp) },
            colors = chipColors()
        )
        AssistChip(
            onClick = { scope.launch { PresetBackup.importFromClipboard(context) } },
            label = { Text("Plak backup", fontSize = 11.sp) },
            colors = chipColors()
        )
        AssistChip(
            onClick = { scope.launch { PresetBackup.restoreLatestSnapshot(context) } },
            label = { Text("Lokale snapshot", fontSize = 11.sp) },
            colors = chipColors()
        )
        AssistChip(
            onClick = {
                val pkg = NowPlayingApp.packageName
                if (pkg.isNullOrBlank()) {
                    Toast.makeText(context, "Speel eerst iets in Spotify of YT Music", Toast.LENGTH_SHORT).show()
                } else {
                    val preset = viewModel.dspManager.currentPreset.value
                    memory.save(pkg, preset, viewModel.dspManager.bandGains.value)
                    Toast.makeText(context, "EQ vastgelegd voor ${memory.label(pkg)}", Toast.LENGTH_SHORT).show()
                }
            },
            label = { Text("EQ voor $appLabel", fontSize = 11.sp) },
            colors = chipColors()
        )
    }
}

@Composable
private fun chipColors() = AssistChipDefaults.assistChipColors(
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary
)
