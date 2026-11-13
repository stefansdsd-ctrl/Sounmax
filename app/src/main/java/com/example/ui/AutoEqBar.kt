package com.example.ui

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutoEqImport
import com.example.data.EqPersist
import com.example.dsp.EqUndo
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import kotlin.math.roundToInt

/**
 * UI for AutoEQ import: paste GraphicEQ from clipboard, sample curve, strength, apply + slot 0 save.
 */
@Composable
fun AutoEqBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    var strength by remember { mutableFloatStateOf(0.85f) }
    var lastInfo by remember { mutableStateOf<String?>(null) }

    fun applyText(text: String, name: String) {
        val result = AutoEqImport.parse(text, name = name, strength = strength)
        if (result == null) {
            Toast.makeText(context, "Geen AutoEQ-punten gevonden", Toast.LENGTH_SHORT).show()
            return
        }
        EqUndo.push(context, viewModel.dspManager)
        viewModel.applyPreset(result.preset)
        EqPersist.save(context, result.preset, slot = 0)
        lastInfo =
            "${result.sourceHint} · ${(strength * 100).roundToInt()}% · preamp ${"%.1f".format(result.preampDb)} dB"
        Toast.makeText(context, "AutoEQ toegepast: ${result.preset.name}", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("auto_eq_bar"),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = false,
                onClick = {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = cm.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
                    if (clip.isBlank()) {
                        Toast.makeText(context, "Klembord leeg", Toast.LENGTH_SHORT).show()
                    } else {
                        applyText(clip, "AutoEQ Import")
                    }
                },
                label = { Text("Plak AutoEQ", fontSize = 11.sp, maxLines = 1) },
                colors = chipColors(),
                modifier = Modifier.testTag("auto_eq_paste")
            )
            FilterChip(
                selected = false,
                onClick = {
                    applyText(AutoEqImport.sampleTah6519Correction(), "AutoEQ TAH6519 sample")
                },
                label = { Text("Sample TAH", fontSize = 11.sp, maxLines = 1) },
                colors = chipColors(),
                modifier = Modifier.testTag("auto_eq_sample")
            )
            FilterChip(
                selected = EqPersist.hasSaved(context, 0),
                onClick = {
                    val p = EqPersist.load(context, 0)
                    if (p == null) {
                        Toast.makeText(context, "Geen slot 0", Toast.LENGTH_SHORT).show()
                    } else {
                        EqUndo.push(context, viewModel.dspManager)
                        viewModel.applyPreset(p)
                        Toast.makeText(context, "Slot 0: ${p.name}", Toast.LENGTH_SHORT).show()
                    }
                },
                label = { Text("Slot 0", fontSize = 11.sp, maxLines = 1) },
                colors = chipColors(),
                modifier = Modifier.testTag("auto_eq_slot0")
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Sterkte ${(strength * 100).roundToInt()}%",
                color = ImmersiveTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.testTag("auto_eq_strength_label")
            )
            Slider(
                value = strength,
                onValueChange = { strength = it },
                valueRange = 0.1f..1f,
                modifier = Modifier
                    .weight(1f)
                    .testTag("auto_eq_strength")
            )
        }
        lastInfo?.let { info ->
            Text(
                info,
                color = ImmersiveTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                modifier = Modifier.testTag("auto_eq_info")
            )
        }
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary,
    selectedLabelColor = ImmersiveLavenderAccent
)
