package com.example.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.ui.input.pointer.pointerInput
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
    private const val KEY_ORDER = "scene_folder_order"

    fun current(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "Alles") ?: "Alles"

    fun set(context: Context, label: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, label).apply()
    }

    fun labels(context: Context): List<String> {
        val defaults = SceneGroups.LABELS.map { it.first }
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_ORDER, "") ?: ""
        if (raw.isBlank()) return defaults
        val saved = raw.split('|').filter { it.isNotBlank() }
        val extra = defaults.filter { it !in saved }
        return (saved.filter { it in defaults } + extra)
    }

    fun moveLeft(context: Context, label: String): List<String> = move(context, label, -1)

    fun moveRight(context: Context, label: String): List<String> = move(context, label, +1)

    private fun move(context: Context, label: String, dir: Int): List<String> {
        val list = labels(context).toMutableList()
        val i = list.indexOf(label)
        val j = i + dir
        if (i >= 0 && j in list.indices) {
            list.removeAt(i)
            list.add(j, label)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_ORDER, list.joinToString("|")).apply()
        }
        return list
    }
}

@Composable
fun SceneFolderBar(sceneController: SceneController? = null) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(sceneController?.sceneGroup?.value ?: SceneFolder.current(context)) }
    var labels by remember { mutableStateOf(SceneFolder.labels(context)) }
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
                modifier = Modifier
                    .testTag("scene_folder_$label")
                    .pointerInput(label) {
                        detectTapGestures(
                            onLongPress = { labels = SceneFolder.moveLeft(context, label) },
                            onDoubleTap = { labels = SceneFolder.moveRight(context, label) }
                        )
                    }
            )
        }
    }
}
