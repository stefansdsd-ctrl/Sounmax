package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.NoiseAware
import androidx.compose.material.icons.filled.NoiseControlOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsp.AncMode
import com.example.dsp.BluetoothCodec
import com.example.dsp.BuiltinPresets
import com.example.dsp.HeadphoneDevice
import com.example.dsp.LdacQualityMode
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

@Composable
fun HeadphoneScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeHeadphone by viewModel.dspManager.activeHeadphone.collectAsStateWithLifecycle()
    val activeAnc by viewModel.dspManager.ancMode.collectAsStateWithLifecycle()
    val selectedCodec by viewModel.dspManager.selectedCodec.collectAsStateWithLifecycle()
    val audioLatencyMs by viewModel.dspManager.audioLatencyMs.collectAsStateWithLifecycle()
    val diagnostics by viewModel.dspManager.diagnosticMetrics.collectAsStateWithLifecycle()
    val diagnosticLogs by viewModel.dspManager.diagnosticLogs.collectAsStateWithLifecycle()

    var showLogsConsole by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBackground)
            .testTag("headphone_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title & System Action Header
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
                        Icon(
                            imageVector = Icons.Default.BluetoothConnected,
                            contentDescription = null,
                            tint = ImmersiveLavenderAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Bluetooth & LDAC Studio",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveTextPrimary
                        )
                    }
                    Text(
                        text = "Signaaldiagnose, LDAC Hi-Res Booster & ANC Management",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // System Bluetooth Quick Link
                    IconButton(
                        onClick = { viewModel.openBluetoothSettings(context) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(ImmersiveSurfaceActive, RoundedCornerShape(10.dp))
                            .testTag("open_system_bluetooth_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsBluetooth,
                            contentDescription = "Bluetooth Instellingen",
                            tint = ImmersiveLavenderAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Developer Options Link
                    IconButton(
                        onClick = { viewModel.openDeveloperOptions(context) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(ImmersiveSurfaceActive, RoundedCornerShape(10.dp))
                            .testTag("open_developer_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = "Ontwikkelaarsopties",
                            tint = ImmersiveAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Active Headphone Status Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_headphone_status_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(
                            ImmersiveLavenderAccent.copy(alpha = 0.5f),
                            ImmersiveDeepViolet.copy(alpha = 0.3f)
                        )
                    )
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = ImmersiveLavenderAccent.copy(alpha = 0.15f),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = null,
                                        tint = ImmersiveLavenderAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = ImmersiveActiveGreen.copy(alpha = 0.2f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(ImmersiveActiveGreen)
                                            )
                                            Text(
                                                text = "A2DP VERBONDEN",
                                                color = ImmersiveActiveGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (selectedCodec == BluetoothCodec.LDAC) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ImmersiveAmber.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "LDAC 990k",
                                                color = ImmersiveAmber,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = activeHeadphone.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveTextPrimary
                                )
                                Text(
                                    text = "${activeHeadphone.brand} • ${activeHeadphone.impedanceOhms} Ω Drivers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextSecondary
                                )
                            }
                        }

                        // Battery Indicator
                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryFull,
                                    contentDescription = "Batterij",
                                    tint = ImmersiveActiveGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${activeHeadphone.batteryPercent}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveTextPrimary
                                )
                            }
                            Text(
                                text = "Accu Status",
                                fontSize = 10.sp,
                                color = ImmersiveTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = activeHeadphone.soundProfileSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // ==========================================
        // 📡 CONNECTION DIAGNOSTIC & TELEMETRY DASHBOARD
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("connection_diagnostic_dashboard_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(ImmersiveAmber.copy(alpha = 0.4f), ImmersiveLavenderAccent.copy(alpha = 0.3f))
                    )
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header of Diagnostics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ImmersiveAmber.copy(alpha = 0.15f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.NetworkCheck,
                                        contentDescription = null,
                                        tint = ImmersiveAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "BLUETOOTH STREAMING DIAGNOSE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.1.sp
                                    ),
                                    color = ImmersiveAmber
                                )
                                Text(
                                    text = "Real-time RF Signaal & A2DP Telemetrie",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ImmersiveTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Quality Score Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ImmersiveActiveGreen.copy(alpha = 0.15f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(ImmersiveActiveGreen, Color.Transparent))
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SignalCellularAlt,
                                    contentDescription = null,
                                    tint = ImmersiveActiveGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${diagnostics.connectionQualityPercent}% Signaalkwaliteit",
                                    color = ImmersiveActiveGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4-Column Live Metric Matrix
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Metric 1: Bitrate
                        DiagnosticMetricPill(
                            title = "BITRATE",
                            value = "${diagnostics.currentBitrateKbps} kbps",
                            subtext = if (diagnostics.currentBitrateKbps >= 990) "Hi-Res Master" else "HD Audio",
                            accentColor = ImmersiveAmber,
                            modifier = Modifier.weight(1f)
                        )

                        // Metric 2: Sample Rate & Depth
                        DiagnosticMetricPill(
                            title = "RESOLUTIE",
                            value = "${diagnostics.sampleRateHz / 1000}k / ${diagnostics.bitDepth}b",
                            subtext = "PCM Studio Master",
                            accentColor = ImmersiveLavenderAccent,
                            modifier = Modifier.weight(1f)
                        )

                        // Metric 3: RSSI Signal
                        val rssiColor = if (diagnostics.rssiDbm > -50) ImmersiveActiveGreen else ImmersiveAmber
                        DiagnosticMetricPill(
                            title = "RSSI SIGNAAL",
                            value = "${diagnostics.rssiDbm} dBm",
                            subtext = if (diagnostics.rssiDbm > -50) "Sterk Bereik" else "Matig Bereik",
                            accentColor = rssiColor,
                            modifier = Modifier.weight(1f)
                        )

                        // Metric 4: Jitter / Packet Loss
                        DiagnosticMetricPill(
                            title = "JITTER",
                            value = "${diagnostics.jitterMs} ms",
                            subtext = "${diagnostics.packetLossPercent}% Loss",
                            accentColor = ImmersiveActiveGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Connection Health Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "A2DP Buffer Stabiliteit: ${diagnostics.bufferHealthPercent}%",
                                color = ImmersiveTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Transport: AVDTP (MTU ${diagnostics.bluetoothMtuSize}B)",
                                color = ImmersiveTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(ImmersiveSurfaceActive)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(diagnostics.connectionQualityPercent / 100f)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(ImmersiveLavenderAccent, ImmersiveAmber, ImmersiveActiveGreen)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = ImmersiveBorderSubtle)

                    Spacer(modifier = Modifier.height(14.dp))

                    // ==========================================
                    // ⚡ FORCEER & OPTIMALISEER LDAC CONTROLS
                    // ==========================================
                    Text(
                        text = "LDAC CODEC BESTURING & OPTIMALISATIE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        ),
                        color = ImmersiveLavenderAccent
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // LDAC Action Buttons: Forceer LDAC 990k & Optimaliseer Stream
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Main Force LDAC Button
                        Button(
                            onClick = { viewModel.forceLdacCodec(LdacQualityMode.QUALITY_990) },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("force_ldac_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ImmersiveAmber,
                                contentColor = Color(0xFF1A1C1E)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Forceer LDAC 990k",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Optimize Buffer & Stream Button
                        Button(
                            onClick = { viewModel.optimizeLdacStreaming() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("optimize_ldac_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ImmersiveSurfaceActive,
                                contentColor = ImmersiveLavenderAccent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(ImmersiveLavenderAccent.copy(alpha = 0.6f), Color.Transparent))
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = ImmersiveLavenderAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Optimaliseer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // LDAC Profile Modes Selector (990k, 660k, 330k, Adaptief)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        LdacQualityMode.values().forEach { mode ->
                            val isSelected = diagnostics.ldacMode == mode && selectedCodec == BluetoothCodec.LDAC
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ImmersiveDeepViolet else ImmersiveSurfaceVariant,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            if (isSelected) ImmersiveAmber else ImmersiveBorderSubtle,
                                            Color.Transparent
                                        )
                                    )
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.forceLdacCodec(mode) }
                                    .testTag("ldac_mode_${mode.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (mode.isBestQuality) Icons.Default.AutoAwesome else Icons.Default.Speed,
                                            contentDescription = null,
                                            tint = if (isSelected) ImmersiveAmber else ImmersiveTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = mode.modeName,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isSelected) Color.White else ImmersiveTextPrimary
                                                )
                                                if (mode.isBestQuality) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = ImmersiveAmber.copy(alpha = 0.25f)
                                                    ) {
                                                        Text(
                                                            text = "STUDIO MASTER",
                                                            color = ImmersiveAmber,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Black,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = mode.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ImmersiveTextSecondary,
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Actief",
                                            tint = ImmersiveAmber,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Live Diagnostic Terminal Console
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogsConsole = !showLogsConsole }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = ImmersiveLavenderAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (showLogsConsole) "Verberg A2DP Diagnostisch Logboek" else "Toon Live A2DP Diagnostisch Logboek",
                                color = ImmersiveLavenderAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${diagnosticLogs.size} logs",
                            color = ImmersiveTextMuted,
                            fontSize = 10.sp
                        )
                    }

                    // Terminal Logs Output Box
                    AnimatedVisibility(
                        visible = showLogsConsole,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F1115))
                                .border(1.dp, ImmersiveBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            diagnosticLogs.forEach { logLine ->
                                Text(
                                    text = logLine,
                                    color = if (logLine.contains("geforceerd") || logLine.contains("LDAC")) ImmersiveAmber else Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 🎧 ACTIVE NOISE CANCELLATION (ANC) STUDIO
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("anc_control_card"),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NoiseAware,
                                contentDescription = null,
                                tint = ImmersiveLavenderAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "ACTIVE NOISE CANCELING (ANC)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                ),
                                color = ImmersiveLavenderAccent
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (activeAnc.noiseReductionDb > 0) ImmersiveLavenderAccent.copy(alpha = 0.2f) else ImmersiveSurfaceActive
                        ) {
                            Text(
                                text = if (activeAnc.noiseReductionDb > 0) "-${activeAnc.noiseReductionDb} dB Ruis" else "Passief",
                                color = if (activeAnc.noiseReductionDb > 0) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AncMode.values().forEach { mode ->
                            val isSelected = activeAnc == mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) ImmersiveDeepViolet else ImmersiveSurfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) ImmersiveLavenderAccent else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setAncMode(mode) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("anc_mode_${mode.name.lowercase()}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = when (mode) {
                                            AncMode.STRONG -> Icons.Default.NoiseAware
                                            AncMode.ADAPTIVE -> Icons.Default.Hearing
                                            AncMode.AMBIENT -> Icons.AutoMirrored.Filled.VolumeDown
                                            AncMode.WIND_GUARD -> Icons.Default.Air
                                            AncMode.OFF -> Icons.Default.NoiseControlOff
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Column {
                                        Text(
                                            text = mode.displayName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) Color.White else ImmersiveTextPrimary
                                        )
                                        Text(
                                            text = mode.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ImmersiveTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Geselecteerd",
                                        tint = ImmersiveLavenderAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 🎼 BLUETOOTH CODEC SELECTOR
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bluetooth_codec_card"),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = ImmersiveAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "BLUETOOTH AUDIO PROTOCOL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                ),
                                color = ImmersiveAmber
                            )
                        }

                        Text(
                            text = "A2DP Codec Stack",
                            style = MaterialTheme.typography.labelSmall,
                            color = ImmersiveTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    BluetoothCodec.values().forEach { codec ->
                        val isSelected = selectedCodec == codec
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ImmersiveDeepViolet else ImmersiveSurfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ImmersiveAmber else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setCodec(codec) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("codec_${codec.name.lowercase()}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = codec.codecName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) ImmersiveAmber else ImmersiveTextPrimary
                                    )
                                    if (codec.isHiRes) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ImmersiveAmber.copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                text = "HI-RES",
                                                color = ImmersiveAmber,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${codec.bitrateInfo} • ${codec.sampleRateInfo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ImmersiveAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // ⏱️ LIP-SYNC & LATENCY COMPENSATION
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("latency_sync_card"),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = ImmersiveLavenderAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "LIP-SYNC & LATENCY COMPENSATIE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                ),
                                color = ImmersiveLavenderAccent
                            )
                        }

                        Text(
                            text = "$audioLatencyMs ms",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = ImmersiveLavenderAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Corrigeer Bluetooth audiovertraging bij het bekijken van YouTube Music clips.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = audioLatencyMs.toFloat(),
                        onValueChange = { viewModel.setLatencySync(it.toInt()) },
                        valueRange = 0f..300f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("latency_sync_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = ImmersiveLavenderAccent,
                            activeTrackColor = ImmersiveLavenderAccent,
                            inactiveTrackColor = ImmersiveSurfaceActive
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0ms (Direct)", color = ImmersiveTextMuted, fontSize = 10.sp)
                        Text("150ms", color = ImmersiveTextMuted, fontSize = 10.sp)
                        Text("300ms", color = ImmersiveTextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // ==========================================
        // 🎧 HEADPHONE DEVICE PROFILES
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "HOOFDTELEFOON PROFIEL KIEZEN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    ),
                    color = ImmersiveTextSecondary
                )

                BuiltinPresets.HEADPHONE_DEVICES.forEach { device ->
                    val isSelected = activeHeadphone.id == device.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectHeadphone(device) }
                            .testTag("device_card_${device.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ImmersiveDeepViolet else ImmersiveSurface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    if (isSelected) ImmersiveLavenderAccent else ImmersiveBorderSubtle,
                                    Color.Transparent
                                )
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ImmersiveLavenderAccent.copy(alpha = 0.2f) else ImmersiveSurfaceActive,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = null,
                                        tint = if (isSelected) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = device.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else ImmersiveTextPrimary
                                    )
                                    if (device.hasAnc) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ImmersiveYtRed.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "ANC",
                                                color = ImmersiveYtRed,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${device.brand} • ${device.defaultPresetName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Actief",
                                    tint = ImmersiveLavenderAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticMetricPill(
    title: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ImmersiveSurfaceVariant,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(accentColor.copy(alpha = 0.3f), Color.Transparent)
            )
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ImmersiveTextMuted,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 8.sp,
                color = ImmersiveTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
