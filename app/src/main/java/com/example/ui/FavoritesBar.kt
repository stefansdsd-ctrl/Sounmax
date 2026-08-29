package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun FavoritesBar(viewModel: MainViewModel) {
    val favorites by viewModel.favoritePresets.collectAsStateWithLifecycle()
    val recent by viewModel.recentPresets.collectAsStateWithLifecycle()
    val chips = (favorites + recent.filter { r -> favorites.none { it.id == r.id } }).take(10)
    if (chips.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chips.forEach { preset ->
            AssistChip(
                onClick = { viewModel.applyCustomDbPreset(preset) },
                label = { Text(preset.name, fontSize = 11.sp, maxLines = 1) },
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleFavorite(preset.id, !preset.isFavorite) }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favoriet",
                            tint = ImmersiveLavenderAccent,
                            modifier = Modifier.alpha(if (preset.isFavorite) 1f else 0.35f)
                        )
                    }
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary
                )
            )
        }
    }
}
