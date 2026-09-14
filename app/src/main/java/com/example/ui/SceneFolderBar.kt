package com.example.ui

import android.content.Context
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.HiddenScenes
import com.example.dsp.SceneGroups
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import kotlin.math.roundToInt

object SceneFolder {
    private const val PREFS = "soundmax_ui"
    private const val KEY = "scene_folder"
    private const val KEY_ORDER = "scene_folder_order"
    private const val KEY_SHOW_HIDDEN = "scene_folder_show_hidden"

    fun current(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "Alles") ?: "Alles"

    fun set(context: Context, label: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, label).apply()
    }

    fun showHidden(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_SHOW_HIDDEN, false)

    fun setShowHidden(context: Context, value: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SHOW_HIDDEN, value).apply()
    }

    fun allOrdered(context: Context): List<String> {
        val defaults = SceneGroups.LABELS.map { it.first }
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_ORDER, "") ?: ""
        if (raw.isBlank()) return defaults
        val saved = raw.split('|').filter { it.isNotBlank() }
        val extra = defaults.filter { it !in saved }
        return (saved.filter { it in defaults } + extra)
    }

    fun labels(context: Context, includeHidden: Boolean = showHidden(context)): List<String> {
        val ordered = allOrdered(context)
        return if (includeHidden) ordered else ordered.filter { !HiddenScenes.isHidden(context, it) }
    }

    fun saveOrder(context: Context, order: List<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_ORDER, order.joinToString("|")).apply()
    }

    fun resetOrder(context: Context): List<String> {
        val defaults = SceneGroups.LABELS.map { it.first }
        saveOrder(context, defaults)
        return labels(context)
    }

    fun moveLeft(context: Context, label: String): List<String> = move(context, label, -1)

    fun moveRight(context: Context, label: String): List<String> = move(context, label, +1)

    fun moveTo(context: Context, from: Int, to: Int): List<String> {
        val visible = labels(context).toMutableList()
        if (from !in visible.indices || to !in visible.indices || from == to) return visible
        val full = allOrdered(context).toMutableList()
        val fromLabel = visible[from]
        val toLabel = visible[to]
        val fi = full.indexOf(fromLabel)
        val ti = full.indexOf(toLabel)
        if (fi < 0 || ti < 0) return visible
        val item = full.removeAt(fi)
        full.add(ti, item)
        saveOrder(context, full)
        return labels(context)
    }

    private fun move(context: Context, label: String, dir: Int): List<String> {
        val full = allOrdered(context).toMutableList()
        val i = full.indexOf(label)
        val j = i + dir
        if (i >= 0 && j in full.indices) {
            full.removeAt(i)
            full.add(j, label)
            saveOrder(context, full)
        }
        return labels(context)
    }
}

@Composable
fun SceneFolderBar(sceneController: SceneController? = null) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var showHidden by remember { mutableStateOf(SceneFolder.showHidden(context)) }
    var selected by remember { mutableStateOf(sceneController?.sceneGroup?.value ?: SceneFolder.current(context)) }
    var labels by remember { mutableStateOf(SceneFolder.labels(context, showHidden)) }
    var dragIndex by remember { mutableIntStateOf(-1) }
    var dragX by remember { mutableFloatStateOf(0f) }
    val stepPx = with(density) { 72.dp.toPx() }
    val hiddenCount = HiddenScenes.hidden(context).size

    fun reload() {
        labels = SceneFolder.labels(context, showHidden)
        if (selected !in labels) {
            selected = "Alles"
            SceneFolder.set(context, "Alles")
            sceneController?.setSceneGroup("Alles")
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("scene_folder_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(labels, key = { _, label -> label }) { index, label ->
            val dragging = dragIndex == index
            val hidden = HiddenScenes.isHidden(context, label)
            FilterChip(
                selected = selected == label,
                onClick = {
                    selected = label
                    SceneFolder.set(context, label)
                    sceneController?.setSceneGroup(label)
                },
                label = {
                    Text(
                        if (hidden) "$label ·" else label,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary,
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier
                    .testTag("scene_folder_$label")
                    .zIndex(if (dragging) 1f else 0f)
                    .offset { IntOffset(if (dragging) dragX.roundToInt() else 0, 0) }
                    .pointerInput(label, index, labels, showHidden) {
                        detectTapGestures(
                            onLongPress = {
                                if (label != "Alles" && label != "Favorieten") {
                                    HiddenScenes.toggle(context, label)
                                    reload()
                                } else {
                                    labels = SceneFolder.moveLeft(context, label)
                                }
                            },
                            onDoubleTap = {
                                if (label == "Alles") {
                                    labels = SceneFolder.resetOrder(context)
                                } else {
                                    labels = SceneFolder.moveRight(context, label)
                                }
                            }
                        )
                    }
                    .pointerInput(label, index, labels) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                dragIndex = index
                                dragX = 0f
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                dragX += amount.x
                            },
                            onDragCancel = {
                                dragIndex = -1
                                dragX = 0f
                            },
                            onDragEnd = {
                                val from = dragIndex
                                val delta = if (stepPx == 0f) 0 else (dragX / stepPx).roundToInt()
                                val to = (from + delta).coerceIn(0, labels.lastIndex)
                                if (from >= 0 && to != from) {
                                    labels = SceneFolder.moveTo(context, from, to)
                                }
                                dragIndex = -1
                                dragX = 0f
                            }
                        )
                    }
            )
        }
        if (hiddenCount > 0 || showHidden) {
            item(key = "show_hidden") {
                FilterChip(
                    selected = showHidden,
                    onClick = {
                        showHidden = !showHidden
                        SceneFolder.setShowHidden(context, showHidden)
                        reload()
                    },
                    label = {
                        Text(
                            if (showHidden) "Verberg extra" else "Toon verborgen ($hiddenCount)",
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                        containerColor = ImmersiveSurfaceActive,
                        labelColor = ImmersiveTextSecondary,
                        selectedLabelColor = ImmersiveLavenderAccent
                    ),
                    modifier = Modifier.testTag("scene_folder_show_hidden")
                )
            }
        }
    }
}
