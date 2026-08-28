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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun HearingTestScreen(
    viewModel: MainViewModel,
    onNavigateToEq: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTestActive by viewModel.hearingTestActive.collectAsStateWithLifecycle()
    val testStep by viewModel.hearingTestStep.collectAsStateWithLifecycle()
    val latestProfile by viewModel.latestHearingProfile.collectAsStateWithLifecycle()
    val activeHeadphone by viewModel.dspManager.activeHeadphone.collectAsStateWithLifecycle()

    var testVolumePercent by remember { mutableFloatStateOf(30f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBackground)
            .testTag("hearing_test_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SoundActiveGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hearing,
                        contentDescription = null,
                        tint = SoundActiveGreen,
                        modifier = Modifier.padding(11.dp)
                    )
                }

                Column {
                    Text(
                        text = "Gehoor Kalibratie Studio",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Persoonlijke audiogram test voor ${activeHeadphone.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Active Test Wizard Card
        if (isTestActive) {
            val freq = viewModel.currentTestFreq
            val isLeft = viewModel.isTestingLeftEar
            val earName = if (isLeft) "Linkeroor (L)" else "Rechteroor (R)"

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_hearing_test_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyanPrimary)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isLeft) NeonCyanPrimary.copy(alpha = 0.2f) else SonicRedSecondary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = earName.uppercase(),
                                    color = if (isLeft) NeonCyanPrimary else SonicRedSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = "Stap ${testStep + 1} van 14",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$freq Hz",
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                            Text(
                                text = "Sinustoon Frequentie",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Test Volume Slider
                        Text(
                            text = "Test Volume Drempel: ${testVolumePercent.toInt()}%",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Slider(
                            value = testVolumePercent,
                            onValueChange = {
                                testVolumePercent = it
                                viewModel.playCurrentStepTone(it)
                            },
                            valueRange = 5f..80f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hearing_test_volume_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyanPrimary,
                                activeTrackColor = NeonCyanPrimary,
                                inactiveTrackColor = StudioDarkSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.playCurrentStepTone(testVolumePercent) },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioDarkSurfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = NeonCyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Speel Toon", color = Color.White, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.recordHearingResponse(testVolumePercent.toInt()) },
                                colors = ButtonDefaults.buttonColors(containerColor = SoundActiveGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("hear_tone_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF001A24),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ik Hoor Dit", color = Color(0xFF001A24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.cancelHearingTest() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Test Annuleren", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // Start Test CTA
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_hearing_test_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Waarom Gehoorkalibratie?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ieder menselijk oor reageert anders op hoge en lage tonen. Deze snelle 2-minuten test stuurt zuivere frequentietesttonen naar je linker- en rechteroorschelp van de ${activeHeadphone.name} en berekent een perfect compenserend 10-bands DSP profiel voor YouTube Music.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.startHearingTest() },
                            colors = ButtonDefaults.buttonColors(containerColor = SoundActiveGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_test_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = null,
                                tint = Color(0xFF001A24),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Persoonlijke Gehoortest",
                                color = Color(0xFF001A24),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Saved Audiogram & Profile
        item {
            latestProfile?.let { profile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("saved_hearing_profile_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoundActiveGreen.copy(alpha = 0.5f))
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
                                    imageVector = Icons.Default.Hearing,
                                    contentDescription = null,
                                    tint = SoundActiveGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = profile.profileName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SoundActiveGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${profile.scorePercent}% Match",
                                    color = SoundActiveGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audiogram Graph
                        Text(
                            text = "AUDIOGRAM FREQUENTIE RESPONS (L & R)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(StudioDarkSurfaceVariant, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val leftGains = profile.leftGains.split(",").mapNotNull { it.toFloatOrNull() }
                                val rightGains = profile.rightGains.split(",").mapNotNull { it.toFloatOrNull() }
                                val count = maxOf(leftGains.size, rightGains.size, 7)
                                val step = size.width / (count - 1)
                                val centerY = size.height / 2

                                // Zero line
                                drawLine(
                                    color = Color(0xFF475569),
                                    start = Offset(0f, centerY),
                                    end = Offset(size.width, centerY),
                                    strokeWidth = 1f
                                )

                                // Left ear path (Cyan)
                                val leftPath = Path()
                                leftGains.forEachIndexed { i, g ->
                                    val x = i * step
                                    val y = centerY - (g / 6f) * (centerY * 0.8f)
                                    if (i == 0) leftPath.moveTo(x, y) else leftPath.lineTo(x, y)
                                    drawCircle(color = NeonCyanPrimary, radius = 3.dp.toPx(), center = Offset(x, y))
                                }
                                drawPath(path = leftPath, color = NeonCyanPrimary, style = Stroke(width = 2.dp.toPx()))

                                // Right ear path (Red)
                                val rightPath = Path()
                                rightGains.forEachIndexed { i, g ->
                                    val x = i * step
                                    val y = centerY - (g / 6f) * (centerY * 0.8f)
                                    if (i == 0) rightPath.moveTo(x, y) else rightPath.lineTo(x, y)
                                    drawCircle(color = SonicRedSecondary, radius = 3.dp.toPx(), center = Offset(x, y))
                                }
                                drawPath(path = rightPath, color = SonicRedSecondary, style = Stroke(width = 2.dp.toPx()))
                            }

                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("125Hz", color = Color(0xFF64748B), fontSize = 9.sp)
                                Text("1kHz", color = Color(0xFF64748B), fontSize = 9.sp)
                                Text("8kHz", color = Color(0xFF64748B), fontSize = 9.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(NeonCyanPrimary, CircleShape))
                                Text("Linkeroor", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(SonicRedSecondary, CircleShape))
                                Text("Rechteroor", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.applyHearingCorrection(profile)
                                onNavigateToEq()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoundActiveGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("apply_hearing_compensation_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF001A24),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Activeer Gehoorcorrectie Equalizer",
                                color = Color(0xFF001A24),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } ?: run {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nog geen gehoortest uitgevoerd",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Voer de kalibratietest uit om je persoonlijke gehoorcurve op te slaan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
