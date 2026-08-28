package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.EqPresetEntity
import com.example.dsp.BuiltinPresets
import com.example.dsp.EqPreset
import com.example.ui.theme.ImmersiveActiveGreen
import com.example.ui.theme.ImmersiveAmber
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveBorder
import com.example.ui.theme.ImmersiveBorderSubtle
import com.example.ui.theme.ImmersiveDeepViolet
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveSurfaceVariant
import com.example.ui.theme.ImmersiveTextMuted
import com.example.ui.theme.ImmersiveTextPrimary
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.ui.theme.ImmersiveYtRed
import kotlin.math.roundToInt

enum class PresetFilterCategory(val label: String, val icon: ImageVector) {
    ALL("Alles", Icons.Default.Tune),
    GENRE("Genres (YT Music)", Icons.Default.MusicNote),
    HEADPHONES("Koptelefoons", Icons.Default.Headphones),
    CUSTOM("Mijn Profielen", Icons.Default.Bookmark)
}

@Composable
fun EqualizerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDspEnabled by viewModel.dspManager.isDspEnabled.collectAsStateWithLifecycle()
    val currentPreset by viewModel.dspManager.currentPreset.collectAsStateWithLifecycle()
    val bandGains by viewModel.dspManager.bandGains.collectAsStateWithLifecycle()
    val bassBoost by viewModel.dspManager.bassBoostStrength.collectAsStateWithLifecycle()
    val virtualizer by viewModel.dspManager.virtualizerStrength.collectAsStateWithLifecycle()
    val loudness by viewModel.dspManager.loudnessGain.collectAsStateWithLifecycle()
    val clarity by viewModel.dspManager.clarityGain.collectAsStateWithLifecycle()
    val balance by viewModel.dspManager.balance.collectAsStateWithLifecycle()
    val customDbPresets by viewModel.customDbPresets.collectAsStateWithLifecycle()

    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }
    var selectedCategoryTab by remember { mutableStateOf(PresetFilterCategory.ALL) }

    // Save Preset Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = ImmersiveLavenderAccent)
                    Text(
                        text = "Aangepast Profiel Opslaan",
                        color = ImmersiveTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Sla de huidige 10-bands frequentiecurve en master effecten op in de database:",
                        color = ImmersiveTextSecondary,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = newPresetName,
                        onValueChange = { newPresetName = it },
                        placeholder = { Text("bv. Philips Bass Boost Extra, EDM Studio...", color = ImmersiveTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImmersiveLavenderAccent,
                            unfocusedBorderColor = ImmersiveBorder,
                            focusedContainerColor = ImmersiveSurfaceActive,
                            unfocusedContainerColor = ImmersiveSurfaceVariant,
                            focusedTextColor = ImmersiveTextPrimary,
                            unfocusedTextColor = ImmersiveTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_preset_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPresetName.isNotBlank()) {
                            viewModel.saveCurrentAsCustomPreset(newPresetName.trim(), "Aangepast")
                            showSaveDialog = false
                            newPresetName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersiveLavenderAccent,
                        contentColor = Color(0xFF1A1C1E)
                    ),
                    modifier = Modifier.testTag("confirm_save_preset_button")
                ) {
                    Text("Opslaan in Database", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Annuleren", color = ImmersiveTextSecondary)
                }
            },
            containerColor = ImmersiveSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
            .testTag("equalizer_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Title & Action Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ImmersiveYtRed,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("YT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "10-Band Pro Equalizer",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveTextPrimary
                        )
                    }
                    Text(
                        text = "Geoptimaliseerd voor YouTube Music & Bluetooth Headsets",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Reset to 0 dB Flat
                    IconButton(
                        onClick = { viewModel.resetBandsToFlat() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(ImmersiveSurfaceActive, RoundedCornerShape(12.dp))
                            .testTag("reset_flat_eq_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset naar Vlak",
                            tint = ImmersiveTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Save Custom Profile Button
                    IconButton(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(ImmersiveLavenderAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .border(1.dp, ImmersiveLavenderAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .testTag("save_preset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Profiel Opslaan",
                            tint = ImmersiveLavenderAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Live Dynamic Frequency Curve Canvas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .testTag("eq_curve_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(ImmersiveBorder, ImmersiveDeepViolet.copy(alpha = 0.3f))
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val numBands = bandGains.size
                        val stepX = size.width / (numBands - 1)
                        val centerY = size.height / 2

                        // Zero reference dB line
                        drawLine(
                            color = Color(0xFF2D2F33),
                            start = Offset(0f, centerY),
                            end = Offset(size.width, centerY),
                            strokeWidth = 1.dp.toPx()
                        )

                        // +6dB and -6dB reference guide lines
                        drawLine(
                            color = Color(0xFF1F2024),
                            start = Offset(0f, centerY - size.height * 0.25f),
                            end = Offset(size.width, centerY - size.height * 0.25f),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFF1F2024),
                            start = Offset(0f, centerY + size.height * 0.25f),
                            end = Offset(size.width, centerY + size.height * 0.25f),
                            strokeWidth = 1.dp.toPx()
                        )

                        val path = Path()
                        val fillPath = Path()
                        fillPath.moveTo(0f, size.height)

                        for (i in 0 until numBands) {
                            val gain = bandGains[i].coerceIn(-12f, 12f)
                            val x = i * stepX
                            val y = centerY - (gain / 12f) * (centerY * 0.85f)

                            if (i == 0) {
                                path.moveTo(x, y)
                                fillPath.lineTo(x, y)
                            } else {
                                val prevX = (i - 1) * stepX
                                val prevGain = bandGains[i - 1].coerceIn(-12f, 12f)
                                val prevY = centerY - (prevGain / 12f) * (centerY * 0.85f)
                                val controlX = (prevX + x) / 2f
                                path.cubicTo(controlX, prevY, controlX, y, x, y)
                                fillPath.cubicTo(controlX, prevY, controlX, y, x, y)
                            }
                        }

                        fillPath.lineTo(size.width, size.height)
                        fillPath.close()

                        // Gradient fill under curve
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ImmersiveLavenderAccent.copy(alpha = 0.35f),
                                    ImmersiveDeepViolet.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Glowing curve stroke
                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    ImmersiveLavenderAccent,
                                    ImmersiveLavenderAccent.copy(alpha = 0.85f)
                                )
                            ),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Peak markers for each band
                        for (i in 0 until numBands) {
                            val gain = bandGains[i].coerceIn(-12f, 12f)
                            val x = i * stepX
                            val y = centerY - (gain / 12f) * (centerY * 0.85f)
                            
                            drawCircle(
                                color = ImmersiveLavenderAccent,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                            if (kotlin.math.abs(gain) > 0.5f) {
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }

                    // dB Scale Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("31Hz (Sub)", color = ImmersiveTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("+12dB / -12dB", color = ImmersiveLavenderAccent.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("16kHz (Air)", color = ImmersiveTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Profile Info Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ImmersiveSurfaceVariant,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(ImmersiveLavenderAccent.copy(alpha = 0.3f), ImmersiveBorderSubtle)
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (currentPreset.category == "Koptelefoons") Icons.Default.Headphones else Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = ImmersiveLavenderAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = currentPreset.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveTextPrimary
                            )
                            if (currentPreset.isCustom) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ImmersiveAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "OPGESLAGEN",
                                        color = ImmersiveAmber,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        if (currentPreset.description.isNotBlank()) {
                            Text(
                                text = currentPreset.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Preset Categories Horizontal Tabs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "EQ PROFIELEN & PRESETS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = ImmersiveTextSecondary
                )

                // Category Filter Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetFilterCategory.values().forEach { cat ->
                        val isSelected = selectedCategoryTab == cat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ImmersiveSurfaceActive else ImmersiveSurface,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(
                                        if (isSelected) ImmersiveLavenderAccent.copy(alpha = 0.5f) else ImmersiveBorderSubtle,
                                        Color.Transparent
                                    )
                                )
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCategoryTab = cat }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.label,
                                    tint = if (isSelected) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = cat.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ImmersiveTextPrimary else ImmersiveTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Preset Chips Row based on selected category
                val filteredBuiltins = when (selectedCategoryTab) {
                    PresetFilterCategory.ALL -> BuiltinPresets.PRESETS
                    PresetFilterCategory.GENRE -> BuiltinPresets.PRESETS.filter { it.category == "Muziekgenres" }
                    PresetFilterCategory.HEADPHONES -> BuiltinPresets.PRESETS.filter { it.category == "Koptelefoons" }
                    PresetFilterCategory.CUSTOM -> emptyList()
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Custom Presets first if CUSTOM or ALL selected
                    if (selectedCategoryTab == PresetFilterCategory.CUSTOM || selectedCategoryTab == PresetFilterCategory.ALL) {
                        items(customDbPresets) { dbPreset ->
                            val isSelected = currentPreset.name == dbPreset.name
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ImmersiveDeepViolet else ImmersiveSurface,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            if (isSelected) ImmersiveAmber else ImmersiveBorderSubtle,
                                            Color.Transparent
                                        )
                                    )
                                ),
                                modifier = Modifier.clickable { viewModel.applyCustomDbPreset(dbPreset) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isSelected) ImmersiveAmber else ImmersiveAmber.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = dbPreset.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else ImmersiveTextPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Verwijder",
                                        tint = ImmersiveYtRed.copy(alpha = 0.8f),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.deleteCustomPreset(dbPreset.id) }
                                    )
                                }
                            }
                        }
                    }

                    // Built-in presets
                    items(filteredBuiltins) { preset ->
                        val isSelected = currentPreset.name == preset.name
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ImmersiveLavenderAccent else ImmersiveSurface,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(
                                        if (isSelected) ImmersiveLavenderAccent else ImmersiveBorderSubtle,
                                        Color.Transparent
                                    )
                                )
                            ),
                            modifier = Modifier.clickable { viewModel.applyPreset(preset) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color(0xFF1A1C1E)
                                    )
                                }
                                Text(
                                    text = preset.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF1A1C1E) else ImmersiveTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 10-Band Sliders Card with Precision Steppers
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("10_band_sliders_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(ImmersiveBorder, ImmersiveBorderSubtle)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "10 FREQUENTIE KANALEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = ImmersiveLavenderAccent
                        )
                        Text(
                            text = "±12 dB PARAMETRISCH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = ImmersiveTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    bandGains.forEachIndexed { index, gain ->
                        val label = BuiltinPresets.FREQUENCY_LABELS.getOrElse(index) { "Band $index" }
                        val freqDesc = when (index) {
                            0, 1 -> "Sub-Bass"
                            2, 3 -> "Mid-Bass"
                            4, 5 -> "Midrange"
                            6, 7 -> "High-Mids"
                            else -> "Treble/Air"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Frequency Label + Description
                            Column(modifier = Modifier.width(68.dp)) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveTextPrimary
                                )
                                Text(
                                    text = freqDesc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ImmersiveTextMuted,
                                    fontSize = 9.sp
                                )
                            }

                            // -0.5 dB Stepper
                            Surface(
                                shape = CircleShape,
                                color = ImmersiveSurfaceActive,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { viewModel.stepBandGain(index, -0.5f) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Minder",
                                        tint = ImmersiveTextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            // Smooth Slider
                            Slider(
                                value = gain,
                                onValueChange = { viewModel.updateBandGain(index, (it * 2).roundToInt() / 2f) },
                                valueRange = -12f..12f,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 6.dp)
                                    .testTag("eq_band_slider_$index"),
                                colors = SliderDefaults.colors(
                                    thumbColor = ImmersiveLavenderAccent,
                                    activeTrackColor = ImmersiveLavenderAccent,
                                    inactiveTrackColor = ImmersiveSurfaceActive
                                )
                            )

                            // +0.5 dB Stepper
                            Surface(
                                shape = CircleShape,
                                color = ImmersiveSurfaceActive,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { viewModel.stepBandGain(index, 0.5f) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Meer",
                                        tint = ImmersiveTextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            // Value display (Tap to reset band to 0dB)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (gain != 0f) ImmersiveSurfaceActive else Color.Transparent,
                                modifier = Modifier
                                    .width(56.dp)
                                    .padding(start = 6.dp)
                                    .clickable { viewModel.updateBandGain(index, 0f) }
                            ) {
                                Text(
                                    text = "${if (gain > 0) "+" else ""}${String.format("%.1f", gain)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (gain > 0) ImmersiveLavenderAccent else if (gain < 0) ImmersiveYtRed else ImmersiveTextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Master DSP Dynamic Enhancers
        item {
            Text(
                text = "DSP MASTER EFFECTEN & SOUNDSTAGE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = ImmersiveTextSecondary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bass Boost Card
                DspEnhancerCard(
                    title = "Deep Bass Boost",
                    subtitle = "Sub-driver resonantie",
                    icon = Icons.Default.Waves,
                    valuePercent = (bassBoost / 10f).toInt(),
                    accentColor = ImmersiveYtRed,
                    onValueChange = { viewModel.setBassBoost((it * 10).toInt()) },
                    modifier = Modifier.weight(1f)
                )

                // 3D Spatial Virtualizer Card
                DspEnhancerCard(
                    title = "3D Virtualizer",
                    subtitle = "Binaurale soundstage",
                    icon = Icons.Default.SurroundSound,
                    valuePercent = (virtualizer / 10f).toInt(),
                    accentColor = ImmersiveLavenderAccent,
                    onValueChange = { viewModel.setVirtualizer((it * 10).toInt()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Loudness Enhancer Card
                DspEnhancerCard(
                    title = "Loudness Peak",
                    subtitle = "Dynamische headroom",
                    icon = Icons.Default.VolumeUp,
                    valuePercent = (loudness / 10f).toInt(),
                    accentColor = ImmersiveAmber,
                    onValueChange = { viewModel.setLoudness((it * 10).toInt()) },
                    modifier = Modifier.weight(1f)
                )

                // Vocal Clarity Card
                DspEnhancerCard(
                    title = "Vocal Clarity",
                    subtitle = "Dialoog articulatie",
                    icon = Icons.Default.GraphicEq,
                    valuePercent = (clarity * 10).toInt(),
                    accentColor = ImmersiveActiveGreen,
                    onValueChange = { viewModel.setClarity(it / 10f) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Stereo Balance & YouTube Music Fast Test Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_slider_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(ImmersiveBorder, ImmersiveBorderSubtle)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Stereo Balans (Links / Rechts)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveTextPrimary
                        )
                        Text(
                            text = if (balance == 0) "Midden (0)" else if (balance < 0) "L ${-balance}%" else "R +${balance}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveLavenderAccent,
                            modifier = Modifier.clickable { viewModel.setBalance(0) }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Links", color = ImmersiveTextSecondary, fontSize = 11.sp)
                        Slider(
                            value = balance.toFloat(),
                            onValueChange = { viewModel.setBalance(it.toInt()) },
                            valueRange = -100f..100f,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .testTag("stereo_balance_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = ImmersiveLavenderAccent,
                                activeTrackColor = ImmersiveLavenderAccent,
                                inactiveTrackColor = ImmersiveSurfaceActive
                            )
                        )
                        Text("Rechts", color = ImmersiveTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Fast Action Button to Test in YouTube Music
        item {
            Button(
                onClick = { viewModel.launchYouTubeMusicNative(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("test_in_yt_music_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImmersiveYtRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Test Deze Equalizer in YouTube Music",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DspEnhancerCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    valuePercent: Int,
    accentColor: Color,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("enhancer_card_${title.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(accentColor.copy(alpha = 0.25f), ImmersiveBorderSubtle)
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "$valuePercent%",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = ImmersiveTextPrimary
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ImmersiveTextSecondary,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Slider(
                value = valuePercent.toFloat(),
                onValueChange = onValueChange,
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = ImmersiveSurfaceActive
                )
            )
        }
    }
}
