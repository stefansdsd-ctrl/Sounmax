package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.dsp.EqUndo
import com.example.dsp.NamedEqSlots
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EqSlotsBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    var tick by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("eq_slots_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NamedEqSlots.NAMES.forEachIndexed { i, name ->
            val filled = tick.let { NamedEqSlots.has(context, i) }
            Box(
                modifier = Modifier.combinedClickable(
                    onClick = {
                        if (filled) {
                            EqUndo.push(context, viewModel.dspManager)
                            if (NamedEqSlots.apply(context, i, viewModel.dspManager)) {
                                Toast.makeText(context, "$name geladen", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            EqUndo.push(context, viewModel.dspManager)
                            NamedEqSlots.save(context, i, viewModel.dspManager)
                            tick++
                            Toast.makeText(context, "$name opgeslagen", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onLongClick = {
                        EqUndo.push(context, viewModel.dspManager)
                        NamedEqSlots.save(context, i, viewModel.dspManager)
                        tick++
                        Toast.makeText(context, "$name overschreven", Toast.LENGTH_SHORT).show()
                    }
                )
            ) {
                FilterChip(
                    selected = filled,
                    onClick = {},
                    enabled = false,
                    label = { Text(name, fontSize = 11.sp, maxLines = 1) },
                    colors = FilterChipDefaults.filterChipColors(
                        disabledSelectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                        disabledContainerColor = ImmersiveSurfaceActive,
                        disabledLabelColor = ImmersiveTextSecondary,
                        disabledSelectedLabelColor = ImmersiveLavenderAccent
                    ),
                    modifier = Modifier.testTag("eq_slot_$i")
                )
            }
        }
        FilterChip(
            selected = false,
            onClick = {
                NamedEqSlots.clearAll(context)
                tick++
                Toast.makeText(context, "Slots leeg", Toast.LENGTH_SHORT).show()
            },
            label = { Text("Wis slots", fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("eq_slots_clear")
        )
    }
}
