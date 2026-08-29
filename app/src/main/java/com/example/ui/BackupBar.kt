package com.example.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun BackupBar(viewModel: MainViewModel, sceneController: SceneController) {
    val context = LocalContext.current
    val appLabel by sceneController.nowPlayingAppLabel.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = { viewModel.backupAllPresets(context) },
            label = { Text("Backup presets", fontSize = 11.sp) },
            colors = chipColors()
        )
        AssistChip(
            onClick = { viewModel.restorePresetsFromClipboard(context) },
            label = { Text("Herstel backup", fontSize = 11.sp) },
            colors = chipColors()
        )
        AssistChip(
            onClick = { sceneController.bindCurrentAppEq() },
            label = { Text("EQ voor ${appLabel ?: "app"}", fontSize = 11.sp) },
            colors = chipColors()
        )
    }
}

@Composable
private fun chipColors() = AssistChipDefaults.assistChipColors(
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary
)
