package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsp.EqLock
import com.example.dsp.EqUndo
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun EqUndoBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    var depth by remember { mutableIntStateOf(EqUndo.depth(context)) }
    var redo by remember { mutableIntStateOf(EqUndo.redoDepth(context)) }
    var locked by remember { mutableStateOf(EqLock.isLocked(context)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("eq_undo_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = depth > 0,
            onClick = {
                val ok = EqUndo.popApply(context, viewModel.dspManager)
                depth = EqUndo.depth(context)
                redo = EqUndo.redoDepth(context)
                Toast.makeText(
                    context,
                    if (ok) "EQ ongedaan ($depth over)" else "Geen EQ-historie",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = { Text("EQ undo ($depth)", fontSize = 11.sp, maxLines = 1) },
            colors = chipColors(),
            modifier = Modifier.testTag("eq_undo_chip")
        )
        FilterChip(
            selected = redo > 0,
            onClick = {
                val ok = EqUndo.redoApply(context, viewModel.dspManager)
                depth = EqUndo.depth(context)
                redo = EqUndo.redoDepth(context)
                Toast.makeText(
                    context,
                    if (ok) "EQ opnieuw ($redo over)" else "Niets om te herhalen",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = { Text("Redo ($redo)", fontSize = 11.sp, maxLines = 1) },
            colors = chipColors(),
            modifier = Modifier.testTag("eq_redo_chip")
        )
        FilterChip(
            selected = false,
            onClick = {
                EqUndo.push(context, viewModel.dspManager)
                depth = EqUndo.depth(context)
                redo = EqUndo.redoDepth(context)
                Toast.makeText(context, "EQ-punt opgeslagen", Toast.LENGTH_SHORT).show()
            },
            label = { Text("Bewaar punt", fontSize = 11.sp, maxLines = 1) },
            colors = chipColors(),
            modifier = Modifier.testTag("eq_undo_save_chip")
        )
        FilterChip(
            selected = locked,
            onClick = {
                locked = EqLock.toggle(context)
                Toast.makeText(
                    context,
                    if (locked) "EQ vergrendeld" else "EQ vrij",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = { Text(if (locked) "EQ vast" else "EQ vrij", fontSize = 11.sp, maxLines = 1) },
            colors = chipColors(),
            modifier = Modifier.testTag("eq_lock_chip")
        )
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary,
    selectedLabelColor = ImmersiveLavenderAccent
)
