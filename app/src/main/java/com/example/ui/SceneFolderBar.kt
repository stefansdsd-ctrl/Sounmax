package com.example.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.dsp.SceneGroups
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

object SceneFolder {
    private const val PREFS = "soundmax_ui"
    private const val KEY = "scene_folder"

    fun current(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "Alles") ?: "Alles"

    fun set(context: Context, label: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, label).apply()
    }
}

@Composable
fun SceneFolderBar(sceneController: SceneController? = null) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(sceneController?.sceneGroup?.value ?: SceneFolder.current(context)) }
    val labels = SceneGroups.LABELS.map { it.first }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("scene_folder_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(labels) { label ->
            FilterChip(
                selected = selected == label,
                onClick = {
                    selected = label
                    SceneFolder.set(context, label)
                    sceneController?.setSceneGroup(label)
                },
                label = { Text(label, fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary,
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("scene_folder_$label")
            )
        }
    }
}
