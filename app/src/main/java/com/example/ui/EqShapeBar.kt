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
        chip("Clip6", "eq_clip_chip") {
            EqUndo.push(context, dsp)
            EqShape.clip(dsp, 6f)
            Toast.makeText(context, "Piek ±6 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Peak", "eq_peak_chip") {
            EqUndo.push(context, dsp)
            EqShape.peakNorm(dsp, 6f)
            Toast.makeText(context, "Piek genormeerd op 6 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Dood", "eq_dead_chip") {
            EqUndo.push(context, dsp)
            EqShape.deadZones(dsp)
            Toast.makeText(context, "Kleine bands op 0", Toast.LENGTH_SHORT).show()
        }
        chip("Bass", "eq_bass_iso_chip") {
            EqUndo.push(context, dsp)
            EqShape.isolateBass(dsp)
            Toast.makeText(context, "Alleen bass-bands", Toast.LENGTH_SHORT).show()
        }
        chip("Treble", "eq_treble_iso_chip") {
            EqUndo.push(context, dsp)
            EqShape.isolateTreble(dsp)
            Toast.makeText(context, "Alleen treble-bands", Toast.LENGTH_SHORT).show()
        }
        chip("Jitter", "eq_jitter_chip") {
            EqUndo.push(context, dsp)
            EqShape.jitter(dsp)
            Toast.makeText(context, "Lichte random ±0,4 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Mid", "eq_mid_iso_chip") {
            EqUndo.push(context, dsp)
            EqShape.isolateMids(dsp)
            Toast.makeText(context, "Alleen midden-bands", Toast.LENGTH_SHORT).show()
        }
        chip("Snap", "eq_snap_chip") {
            EqUndo.push(context, dsp)
            EqShape.snap(dsp)
            Toast.makeText(context, "Rond naar 0,5 dB", Toast.LENGTH_SHORT).show()
        }
        chip("V", "eq_vcurve_chip") {
            EqUndo.push(context, dsp)
            EqShape.vCurve(dsp)
            Toast.makeText(context, "V-curve (laag+hoog)", Toast.LENGTH_SHORT).show()
        }
        chip("Scoop", "eq_scoop_chip") {
            EqUndo.push(context, dsp)
            EqShape.scoop(dsp)
            Toast.makeText(context, "Scoop (middenboost)", Toast.LENGTH_SHORT).show()
        }
        chip("Abs", "eq_abs_chip") {
            EqUndo.push(context, dsp)
            EqShape.absGains(dsp)
            Toast.makeText(context, "Alle gains positief", Toast.LENGTH_SHORT).show()
        }
        chip("Presence", "eq_presence_chip") {
            EqUndo.push(context, dsp)
            EqShape.presence(dsp)
            Toast.makeText(context, "Presence +1,4 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Punch", "eq_punch_chip") {
            EqUndo.push(context, dsp)
            EqShape.punch(dsp)
            Toast.makeText(context, "Sub-bass +1,6 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Air", "eq_air_chip") {
            EqUndo.push(context, dsp)
            EqShape.air(dsp)
            Toast.makeText(context, "Hoogste band +1,5 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Loud", "eq_loudness_chip") {
            EqUndo.push(context, dsp)
            EqShape.loudness(dsp)
            Toast.makeText(context, "Loudness (laag+hoog)", Toast.LENGTH_SHORT).show()
        }
        chip("Floor", "eq_floor_chip") {
            EqUndo.push(context, dsp)
            EqShape.floorZero(dsp)
            Toast.makeText(context, "Laagste band op 0 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Ceil", "eq_ceiling_chip") {
            EqUndo.push(context, dsp)
            EqShape.ceilingZero(dsp)
            Toast.makeText(context, "Hoogste band op 0 dB", Toast.LENGTH_SHORT).show()
        }
        chip("RMS", "eq_rms_chip") {
            EqUndo.push(context, dsp)
            EqShape.matchRms(dsp)
            Toast.makeText(context, "RMS genormeerd op 2 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Tight", "eq_tight_chip") {
            EqUndo.push(context, dsp)
            EqShape.tight(dsp)
            Toast.makeText(context, "Piek ±3 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Spread", "eq_spread_chip") {
            EqUndo.push(context, dsp)
            EqShape.spread(dsp)
            Toast.makeText(context, "Contrast ×1,4", Toast.LENGTH_SHORT).show()
        }
        chip("Soft", "eq_soft_chip") {
            EqUndo.push(context, dsp)
            EqShape.soft(dsp)
            Toast.makeText(context, "2× glad", Toast.LENGTH_SHORT).show()
        }
        chip("Body", "eq_body_chip") {
            EqUndo.push(context, dsp)
            EqShape.body(dsp)
            Toast.makeText(context, "Low-mid +1,3 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Vocal", "eq_vocal_chip") {
            EqUndo.push(context, dsp)
            EqShape.vocal(dsp)
            Toast.makeText(context, "Vocal +1,4 dB", Toast.LENGTH_SHORT).show()
        }
        chip("Night", "eq_night_chip") {
            EqUndo.push(context, dsp)
            EqShape.night(dsp)
            Toast.makeText(context, "Hoogtes −1,2 dB", Toast.LENGTH_SHORT).show()
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
