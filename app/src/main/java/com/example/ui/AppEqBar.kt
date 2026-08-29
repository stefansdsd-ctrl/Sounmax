package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.example.data.AppEqMemory
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun AppEqBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    val memory = remember { AppEqMemory(context) }
    var enabled by remember { mutableStateOf(memory.enabled) }
    var bindings by remember { mutableStateOf(memory.listBindings()) }

    if (bindings.isEmpty() && enabled) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("app_eq_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = enabled,
            onClick = {
                enabled = !enabled
                memory.enabled = enabled
                Toast.makeText(
                    context,
                    if (enabled) "Per-app EQ aan" else "Per-app EQ uit",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = { Text(if (enabled) "App-EQ aan" else "App-EQ uit", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.25f),
                selectedLabelColor = ImmersiveLavenderAccent
            )
        )
        bindings.forEach { binding ->
            AssistChip(
                onClick = {
                    memory.load(binding.packageName, ignoreEnabled = true)?.let {
                        viewModel.applyPreset(it)
                        Toast.makeText(context, "EQ: ${binding.label}", Toast.LENGTH_SHORT).show()
                    }
                },
                label = { Text("${binding.label} · ${binding.presetName}", fontSize = 11.sp) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary
                )
            )
            AssistChip(
                onClick = {
                    memory.delete(binding.packageName)
                    bindings = memory.listBindings()
                    Toast.makeText(context, "${binding.label} gewist", Toast.LENGTH_SHORT).show()
                },
                label = { Text("× ${binding.label}", fontSize = 11.sp) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary
                )
            )
        }
    }
}
