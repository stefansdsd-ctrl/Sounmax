package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsp.EqShape
import com.example.dsp.EqUndo
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun EqShapeBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    val dsp = viewModel.dspManager

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("eq_shape_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chip("Glad", "eq_smooth_chip") {
            EqUndo.push(context, dsp)
            EqShape.smooth(dsp)
            Toast.makeText(context, "EQ gladgestreken", Toast.LENGTH_SHORT).show()
        }
        chip("Spiegel", "eq_mirror_chip") {
            EqUndo.push(context, dsp)
            EqShape.mirror(dsp)
            Toast.makeText(context, "EQ gespiegeld (laag\u2194hoog)", Toast.LENGTH_SHORT).show()
        }
        chip("Norm", "eq_norm_chip") {
            EqUndo.push(context, dsp)
            EqShape.normalize(dsp)
            Toast.makeText(context, "EQ gecentreerd (gemiddelde 0 dB)", Toast.LENGTH_SHORT).show()
        }
        chip("Warm", "eq_tilt_warm_chip") {
            EqUndo.push(context, dsp)
            EqShape.tilt(dsp, brighter = false)
            Toast.makeText(context, "Tilt warmer", Toast.LENGTH_SHORT).show()
        }
        chip("Helder", "eq_tilt_bright_chip") {
            EqUndo.push(context, dsp)
            EqShape.tilt(dsp, brighter = true)
            Toast.makeText(context, "Tilt helderder", Toast.LENGTH_SHORT).show()
        }
        chip("Invert", "eq_invert_chip") {
            EqUndo.push(context, dsp)
            EqShape.invert(dsp)
            Toast.makeText(context, "EQ geïnverteerd", Toast.LENGTH_SHORT).show()
        }
        chip("Halveer", "eq_scale_half_chip") {
            EqUndo.push(context, dsp)
            EqShape.scale(dsp, 0.5f)
            Toast.makeText(context, "EQ ×0,5", Toast.LENGTH_SHORT).show()
        }
        chip("Sterker", "eq_scale_up_chip") {
            EqUndo.push(context, dsp)
            EqShape.scale(dsp, 1.25f)
            Toast.makeText(context, "EQ ×1,25", Toast.LENGTH_SHORT).show()
        }
        chip("←", "eq_shift_left_chip") {
            EqUndo.push(context, dsp)
            EqShape.shift(dsp, -1)
            Toast.makeText(context, "Bands naar lager", Toast.LENGTH_SHORT).show()
        }
        chip("→", "eq_shift_right_chip") {
            EqUndo.push(context, dsp)
            EqShape.shift(dsp, 1)
            Toast.makeText(context, "Bands naar hoger", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun chip(label: String, tag: String, onClick: () -> Unit) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
            containerColor = ImmersiveSurfaceActive,
            labelColor = ImmersiveTextSecondary,
            selectedLabelColor = ImmersiveLavenderAccent
        ),
        modifier = Modifier.testTag(tag)
    )
}
