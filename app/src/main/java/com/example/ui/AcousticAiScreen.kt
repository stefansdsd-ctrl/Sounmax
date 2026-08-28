package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NoiseAware
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsp.BuiltinPresets
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonCyanPrimary
import com.example.ui.theme.SonicRedSecondary
import com.example.ui.theme.SoundActiveGreen
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBackground
import com.example.ui.theme.StudioDarkSurface
import com.example.ui.theme.StudioDarkSurfaceVariant

@Composable
fun AcousticAiScreen(
    viewModel: MainViewModel,
    onNavigateToEq: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aiState by viewModel.aiTunerState.collectAsStateWithLifecycle()
    val activeHeadphone by viewModel.dspManager.activeHeadphone.collectAsStateWithLifecycle()
    var promptInput by remember { mutableStateOf("") }

    val promptSuggestions = listOf(
        "Optimaliseer voor Philips TAH6519 met diepe sub-bass en heldere zang op YouTube Music",
        "Daft Punk concert met 3D surround soundstage en analoge warmte",
        "Joost Klein / Hardstyle met harde punchy kick en snijdende synths",
        "Akoestische gitaar & intieme vocalen zonder schel hoog",
        "Podcast stemverstaanbaarheid en ruisonderdrukking",
        "Nachtmodus relax & chill tegen luistermoeheid"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBackground)
            .testTag("acoustic_ai_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GoldTertiary.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldTertiary,
                        modifier = Modifier.padding(11.dp)
                    )
                }

                Column {
                    Text(
                        text = "Gemini AI Acoustic Studio",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Real-time AI akoestische optimalisatie voor ${activeHeadphone.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Prompt Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_prompt_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WAT WIL JE LUISTEREN?",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = GoldTertiary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = {
                            Text(
                                "bv. Ik luister naar Nederlandse rap op mijn Philips koptelefoon, geef me de beste punchy bass en zang...",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("ai_prompt_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldTertiary,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedContainerColor = StudioDarkSurfaceVariant,
                            unfocusedContainerColor = StudioDarkSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                viewModel.askAiTuner(promptInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldTertiary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_ai_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF001A24),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Genereer Akoestisch Profiel",
                            color = Color(0xFF001A24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Quick Suggestions Horizontal Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SNELLE VOORBEELD PROMPTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color(0xFF94A3B8)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(promptSuggestions) { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StudioDarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.clickable {
                                promptInput = suggestion
                                viewModel.askAiTuner(suggestion)
                            }
                        ) {
                            Text(
                                text = suggestion,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // AI Response Section
        item {
            when (val state = aiState) {
                is AiTunerState.Loading -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_loading_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldTertiary.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = GoldTertiary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Gemini AI berekent akoestische curve...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Analyseren van drivers, resonanties & YouTube Music bitrate",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                is AiTunerState.Success -> {
                    val rec = state.recommendation
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_recommendation_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(listOf(GoldTertiary, NeonCyanPrimary))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = GoldTertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = rec.presetName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SoundActiveGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "AI OPTIMALISATIE",
                                        color = SoundActiveGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = rec.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Mini EQ Visualizer preview
                            Text(
                                text = "10-BANDS EQ PROFIEL PREVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyanPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(StudioDarkSurfaceVariant, RoundedCornerShape(10.dp))
                                    .padding(8.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val bands = rec.eqPreset.bandGains
                                    val step = size.width / bands.size
                                    val centerY = size.height / 2

                                    drawLine(
                                        color = Color(0xFF475569),
                                        start = Offset(0f, centerY),
                                        end = Offset(size.width, centerY),
                                        strokeWidth = 1f
                                    )

                                    for (i in bands.indices) {
                                        val gain = bands[i]
                                        val barHeight = (gain / 12f) * (centerY * 0.9f)
                                        val x = i * step + step * 0.2f
                                        val barW = step * 0.6f

                                        drawRoundRect(
                                            color = if (gain >= 0) NeonCyanPrimary else SonicRedSecondary,
                                            topLeft = if (gain >= 0) Offset(x, centerY - barHeight) else Offset(x, centerY),
                                            size = Size(barW, Math.abs(barHeight).coerceAtLeast(2f)),
                                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // DSP parameters chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StudioDarkSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Bass Boost", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        Text("${(rec.eqPreset.bassBoost / 10f).toInt()}%", color = SonicRedSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StudioDarkSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("3D Virtualizer", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        Text("${(rec.eqPreset.virtualizer / 10f).toInt()}%", color = NeonCyanPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StudioDarkSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Vocal Clarity", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        Text("${rec.eqPreset.clarity}/10", color = SoundActiveGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Acoustic Insight Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldTertiary.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldTertiary.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = GoldTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text("Akoestisch Ingenieursadvies", color = GoldTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(rec.acousticInsight, color = Color(0xFFE2E8F0), fontSize = 11.sp, lineHeight = 15.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Aanbeveling: ${rec.ancRecommendation} • ${rec.codecRecommendation}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.applyPreset(rec.eqPreset)
                                    onNavigateToEq()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyanPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("apply_ai_profile_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF001A24),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Toepassen op 10-Band Equalizer",
                                    color = Color(0xFF001A24),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                is AiTunerState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Fout bij genereren:", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            Text(state.message, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                is AiTunerState.Idle -> {
                    // Placeholder card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Vraag Gemini AI voor Akoestische Tuning",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Kies hierboven een voorbeeldprompt of typ jouw favoriete artiest of album in voor een exacte 10-bands equalizer curve.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
