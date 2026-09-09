package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.media.AncHaptics

private val DarkColorScheme = darkColorScheme(
    primary = ImmersiveLavenderAccent,
    onPrimary = Color(0xFF1A1C1E),
    primaryContainer = ImmersiveDeepViolet,
    onPrimaryContainer = ImmersiveLavenderAccent,
    secondary = ImmersiveYtRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF450A0A),
    onSecondaryContainer = Color(0xFFFFD9D9),
    tertiary = ImmersiveAmber,
    onTertiary = Color(0xFF472A00),
    background = ImmersiveBackground,
    onBackground = ImmersiveTextPrimary,
    surface = ImmersiveSurface,
    onSurface = ImmersiveTextPrimary,
    surfaceVariant = ImmersiveSurfaceVariant,
    onSurfaceVariant = ImmersiveTextSecondary,
    outline = ImmersiveBorder
)

private val OledColorScheme = DarkColorScheme.copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color(0xFF0A0A0A),
    primaryContainer = Color(0xFF1A0F33)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val oled = AncHaptics.oledEnabled(LocalContext.current)
    MaterialTheme(
        colorScheme = if (oled) OledColorScheme else DarkColorScheme,
        typography = Typography,
        content = content
    )
}

fun appBackground(oled: Boolean): Color =
    if (oled) Color.Black else ImmersiveBackground
