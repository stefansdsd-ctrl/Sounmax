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
import com.example.dsp.EqShape162
import com.example.dsp.EqShape163
import com.example.dsp.EqShape164
import com.example.dsp.EqUndo
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun EqShapeBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    val dsp = viewModel.dspManager

    fun run(msg: String, block: () -> Unit) {
        EqUndo.push(context, dsp)
        block()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("eq_shape_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        group("Vorm")
        chip("Glad", "eq_smooth_chip") { run("EQ gladgestreken") { EqShape.smooth(dsp) } }
        chip("Soft", "eq_soft_chip") { run("2× glad") { EqShape.soft(dsp) } }
        chip("Spiegel", "eq_mirror_chip") { run("EQ gespiegeld (laag↔hoog)") { EqShape.mirror(dsp) } }
        chip("V", "eq_vcurve_chip") { run("V-curve (laag+hoog)") { EqShape.vCurve(dsp) } }
        chip("Smile", "eq_smile_chip") { run("Lichte V") { EqShape.smile(dsp) } }
        chip("Scoop", "eq_scoop_chip") { run("Scoop (middenboost)") { EqShape.scoop(dsp) } }
        chip("Loud", "eq_loudness_chip") { run("Loudness (laag+hoog)") { EqShape.loudness(dsp) } }
        chip("Edge", "eq_edge_chip") { run("Eerste+laatste +0,8 dB") { EqShape.edge(dsp) } }
        chip("Widen", "eq_widen_chip") { run("Uiteinden +1 dB") { EqShape162.widen(dsp) } }
        chip("Narrow", "eq_narrow_chip") { run("Uiteinden −1 dB") { EqShape162.narrow(dsp) } }
        chip("Flat", "eq_flat_chip") { run("Alle bands 0 dB") { EqShape.flat(dsp) } }

        group("Toon")
        chip("Warm", "eq_tilt_warm_chip") { run("Tilt warmer") { EqShape.tilt(dsp, brighter = false) } }
        chip("Helder", "eq_tilt_bright_chip") { run("Tilt helderder") { EqShape.tilt(dsp, brighter = true) } }
        chip("Punch", "eq_punch_chip") { run("Sub-bass +1,6 dB") { EqShape.punch(dsp) } }
        chip("Bloom", "eq_bloom_chip") { run("Laagste 2 bands +1,4 dB") { EqShape162.bloom(dsp) } }
        chip("Rumble", "eq_rumble_chip") { run("Laagste band −2,2 dB") { EqShape.rumble(dsp) } }
        chip("Fat", "eq_fat_chip") { run("Band 2–3 +1,5 dB") { EqShape.fat(dsp) } }
        chip("Body", "eq_body_chip") { run("Low-mid +1,3 dB") { EqShape.body(dsp) } }
        chip("Vocal", "eq_vocal_chip") { run("Vocal +1,4 dB") { EqShape.vocal(dsp) } }
        chip("Speech", "eq_speech_chip") { run("Spraakband omhoog") { EqShape.speech(dsp) } }
        chip("Phone", "eq_phone_chip") { run("Telefoonband") { EqShape163.phone(dsp) } }
        chip("Podcast", "eq_podcast_chip") { run("Podcast-EQ") { EqShape164.podcast(dsp) } }
        chip("Sparkle", "eq_sparkle_chip") { run("Hoogste 2 bands +1,2 dB") { EqShape163.sparkle(dsp) } }
        chip("Presence", "eq_presence_chip") { run("Presence +1,4 dB") { EqShape.presence(dsp) } }
        chip("Clarity", "eq_clarity_chip") { run("Upper-mids +1,3 dB") { EqShape.clarity(dsp) } }
        chip("Air", "eq_air_chip") { run("Hoogste band +1,5 dB") { EqShape.air(dsp) } }
        chip("WarmAir", "eq_warmair_chip") { run("Low-shelf + air") { EqShape.warmAir(dsp) } }
        chip("ShelfL", "eq_shelf_low_chip") { run("Low-shelf +1,5 dB") { EqShape.shelfLow(dsp) } }
        chip("ShelfH", "eq_shelf_high_chip") { run("High-shelf +1,5 dB") { EqShape.shelfHigh(dsp) } }

        group("Context")
        chip("Auto", "eq_car_chip") { run("Auto: rumble weg + presence") { EqShape164.car(dsp) } }
        chip("Gym", "eq_gym_chip") { run("Sport: punch + tight") { EqShape164.gym(dsp) } }
        chip("Film", "eq_film_chip") { run("Film: vocal + zacht hoog") { EqShape164.film(dsp) } }
        chip("Wandel", "eq_walk_chip") { run("Wandel: minder bass") { EqShape164.walk(dsp) } }
        chip("Radio", "eq_radio_chip") { run("Radio: mid-scoop") { EqShape164.radio(dsp) } }

        group("Schoon")
        chip("Mud", "eq_mud_chip") { run("200–500 Hz −1,8 dB") { EqShape.mud(dsp) } }
        chip("Boxy", "eq_boxy_chip") { run("Boxy-band −1,7 dB") { EqShape162.boxy(dsp) } }
        chip("Nasal", "eq_nasal_chip") { run("800–1,2 kHz −1,5 dB") { EqShape162.nasal(dsp) } }
        chip("Harsh", "eq_harsh_chip") { run("2–4 kHz −1,6 dB") { EqShape.harsh(dsp) } }
        chip("DeEss", "eq_deess_chip") { run("Hoogste 2 bands −1,6 dB") { EqShape.deess(dsp) } }
        chip("Hiss", "eq_hiss_chip") { run("Hoogste band −2 dB") { EqShape162.hiss(dsp) } }
        chip("Honk", "eq_honk_chip") { run("Mid 3–4 −1,8 dB") { EqShape163.honk(dsp) } }
        chip("SubCut", "eq_subcut_chip") { run("Sub −4 dB") { EqShape163.subCut(dsp) } }
        chip("Sleep", "eq_sleep_chip") { run("Nacht + hiss + zachter") { EqShape163.sleep(dsp) } }
        chip("Pocket", "eq_pocket_chip") { run("Bass −2 dB (lek/zak)") { EqShape.pocket(dsp) } }
        chip("Night", "eq_night_chip") { run("Hoogtes −1,2 dB") { EqShape.night(dsp) } }
        chip("MonoL", "eq_monol_chip") { run("Eerste 2 bands middelen") { EqShape.monoLow(dsp) } }

        group("Veilig")
        chip("Safe", "eq_safe_chip") { run("±4 dB + zachte highs") { EqShape.safe(dsp) } }
        chip("Tight", "eq_tight_chip") { run("Piek ±3 dB") { EqShape.tight(dsp) } }
        chip("Soft4", "eq_soft4_chip") { run("Piek ±4 dB") { EqShape163.soft4(dsp) } }
        chip("Clip6", "eq_clip_chip") { run("Piek ±6 dB") { EqShape.clip(dsp, 6f) } }
        chip("Fade", "eq_fade_chip") { run("50% naar vlak") { EqShape.fade(dsp) } }
        chip("Comp", "eq_comp_chip") { run("30% naar gemiddelde") { EqShape.compressMean(dsp) } }
        chip("Halveer", "eq_scale_half_chip") { run("EQ ×0,5") { EqShape.scale(dsp, 0.5f) } }
        chip("Sterker", "eq_scale_up_chip") { run("EQ ×1,25") { EqShape.scale(dsp, 1.25f) } }
        chip("Spread", "eq_spread_chip") { run("Contrast ×1,4") { EqShape.spread(dsp) } }

        group("Tools")
        chip("Norm", "eq_norm_chip") { run("EQ gecentreerd (gemiddelde 0 dB)") { EqShape.normalize(dsp) } }
        chip("Invert", "eq_invert_chip") { run("EQ geïnverteerd") { EqShape.invert(dsp) } }
        chip("←", "eq_shift_left_chip") { run("Bands naar lager") { EqShape.shift(dsp, -1) } }
        chip("→", "eq_shift_right_chip") { run("Bands naar hoger") { EqShape.shift(dsp, 1) } }
        chip("Peak", "eq_peak_chip") { run("Piek genormeerd op 6 dB") { EqShape.peakNorm(dsp, 6f) } }
        chip("RMS", "eq_rms_chip") { run("RMS genormeerd op 2 dB") { EqShape.matchRms(dsp) } }
        chip("Dood", "eq_dead_chip") { run("Kleine bands op 0") { EqShape.deadZones(dsp) } }
        chip("Snap", "eq_snap_chip") { run("Rond naar 0,5 dB") { EqShape.snap(dsp) } }
        chip("Bass", "eq_bass_iso_chip") { run("Alleen bass-bands") { EqShape.isolateBass(dsp) } }
        chip("Mid", "eq_mid_iso_chip") { run("Alleen midden-bands") { EqShape.isolateMids(dsp) } }
        chip("Treble", "eq_treble_iso_chip") { run("Alleen treble-bands") { EqShape.isolateTreble(dsp) } }
        chip("0Bass", "eq_zero_bass_chip") { run("Eerste 3 bands 0 dB") { EqShape162.zeroBass(dsp) } }
        chip("0Treble", "eq_zero_treble_chip") { run("Laatste 3 bands 0 dB") { EqShape162.zeroTreble(dsp) } }
        chip("Jitter", "eq_jitter_chip") { run("Lichte random ±0,4 dB") { EqShape.jitter(dsp) } }
        chip("Abs", "eq_abs_chip") { run("Alle gains positief") { EqShape.absGains(dsp) } }
        chip("Floor", "eq_floor_chip") { run("Laagste band op 0 dB") { EqShape.floorZero(dsp) } }
        chip("Ceil", "eq_ceiling_chip") { run("Hoogste band op 0 dB") { EqShape.ceilingZero(dsp) } }
    }
}

@Composable
private fun group(label: String) {
    Text(
        text = label.uppercase(),
        fontSize = 9.sp,
        color = ImmersiveLavenderAccent,
        modifier = Modifier.padding(start = 4.dp)
    )
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
