package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveNavBackground
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextPrimary
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.ui.theme.ImmersiveYtRed

enum class SoundMaxTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    YT_MUSIC("YT Music", Icons.Default.MusicNote, "nav_tab_yt_music"),
    EQUALIZER("10-Band EQ", Icons.Default.GraphicEq, "nav_tab_equalizer"),
    HEADPHONE("Headphone", Icons.Default.Headphones, "nav_tab_headphone"),
    AI_STUDIO("AI Studio", Icons.Default.AutoAwesome, "nav_tab_ai_studio"),
    HEARING("Gehoor", Icons.Default.Hearing, "nav_tab_hearing")
}

@Composable
fun SoundMaxApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(SoundMaxTab.YT_MUSIC) }

    val isDspEnabled by viewModel.dspManager.isDspEnabled.collectAsStateWithLifecycle()
    val currentPreset by viewModel.dspManager.currentPreset.collectAsStateWithLifecycle()
    val activeAnc by viewModel.dspManager.ancMode.collectAsStateWithLifecycle()
    val selectedCodec by viewModel.dspManager.selectedCodec.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBackground),
        topBar = {
            MasterDspStatusBar(
                isDspEnabled = isDspEnabled,
                currentPreset = currentPreset,
                activeAnc = activeAnc,
                codecName = selectedCodec.codecName.split(" ").lastOrNull() ?: selectedCodec.name,
                onToggleDsp = { viewModel.setDspEnabled(it) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ImmersiveNavBackground,
                tonalElevation = 0.dp,
                modifier = Modifier.testTag("soundmax_bottom_navigation")
            ) {
                SoundMaxTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = (-0.2).sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (tab == SoundMaxTab.YT_MUSIC) ImmersiveYtRed else ImmersiveLavenderAccent,
                            selectedTextColor = if (tab == SoundMaxTab.YT_MUSIC) ImmersiveYtRed else ImmersiveLavenderAccent,
                            indicatorColor = ImmersiveSurfaceActive,
                            unselectedIconColor = ImmersiveTextSecondary,
                            unselectedTextColor = ImmersiveTextSecondary
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ImmersiveBackground)
        ) {
            when (selectedTab) {
                SoundMaxTab.YT_MUSIC -> {
                    YtMusicScreen(
                        viewModel = viewModel,
                        onNavigateToEq = { selectedTab = SoundMaxTab.EQUALIZER },
                        onNavigateToAi = { selectedTab = SoundMaxTab.AI_STUDIO }
                    )
                }
                SoundMaxTab.EQUALIZER -> {
                    EqualizerScreen(viewModel = viewModel)
                }
                SoundMaxTab.HEADPHONE -> {
                    HeadphoneScreen(viewModel = viewModel)
                }
                SoundMaxTab.AI_STUDIO -> {
                    AcousticAiScreen(
                        viewModel = viewModel,
                        onNavigateToEq = { selectedTab = SoundMaxTab.EQUALIZER }
                    )
                }
                SoundMaxTab.HEARING -> {
                    HearingTestScreen(
                        viewModel = viewModel,
                        onNavigateToEq = { selectedTab = SoundMaxTab.EQUALIZER }
                    )
                }
            }
        }
    }
}

