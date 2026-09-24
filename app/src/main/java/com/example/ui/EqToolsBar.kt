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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsp.EqShare
import com.example.dsp.EqUndo
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun EqToolsBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    var hint by remember { mutableStateOf(EqShare.closestSlot(context, viewModel.dspManager)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("eq_tools_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = false,
            onClick = {
                EqUndo.push(context, viewModel.dspManager)
                EqShare.flatten(viewModel.dspManager)
                hint = EqShare.closestSlot(context, viewModel.dspManager)
                Toast.makeText(context, "EQ vlak (undo mogelijk)", Toast.LENGTH_SHORT).show()
            },
            label = { Text("Vlak", fontSize = 11.sp, maxLines = 1) },
            colors = colors(),
            modifier = Modifier.testTag("eq_flatten_chip")
        )
        FilterChip(
            selected = false,
            onClick = {
                EqShare.copy(context, viewModel.dspManager)
                Toast.makeText(context, "EQ gekopieerd", Toast.LENGTH_SHORT).show()
            },
            label = { Text("Kopieer EQ", fontSize = 11.sp, maxLines = 1) },
            colors = colors(),
            modifier = Modifier.testTag("eq_copy_chip")
        )
        FilterChip(
            selected = false,
            onClick = {
                EqUndo.push(context, viewModel.dspManager)
                val ok = EqShare.paste(context, viewModel.dspManager)
                hint = EqShare.closestSlot(context, viewModel.dspManager)
                Toast.makeText(
                    context,
                    if (ok) "EQ geplakt" else "Geen SMX-EQ op klembord",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = { Text("Plak EQ", fontSize = 11.sp, maxLines = 1) },
            colors = colors(),
            modifier = Modifier.testTag("eq_paste_chip")
        )
        if (hint != null) {
            FilterChip(
                selected = true,
                onClick = { hint = EqShare.closestSlot(context, viewModel.dspManager) },
                label = { Text("~ $hint", fontSize = 11.sp, maxLines = 1) },
                colors = colors(),
                modifier = Modifier.testTag("eq_closest_chip")
            )
        }
    }
}

@Composable
private fun colors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary,
    selectedLabelColor = ImmersiveLavenderAccent
)
