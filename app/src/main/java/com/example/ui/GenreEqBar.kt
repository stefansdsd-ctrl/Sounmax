package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NowPlayingApp
import com.example.dsp.AdaptiveTrackEq
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.delay

@Composable
fun GenreEqBar(viewModel: MainViewModel) {
    var title by remember { mutableStateOf(NowPlayingApp.title.orEmpty()) }
    var artist by remember { mutableStateOf(NowPlayingApp.artist.orEmpty()) }
    var genre by remember { mutableStateOf(NowPlayingApp.genre.orEmpty()) }
    var label by remember { mutableStateOf("neutraal") }

    LaunchedEffect(Unit) {
        while (true) {
            title = NowPlayingApp.title.orEmpty()
            artist = NowPlayingApp.artist.orEmpty()
            genre = NowPlayingApp.genre.orEmpty()
            label = AdaptiveTrackEq.hint(genre, title, artist).label
            delay(1500)
        }
    }

    if (title.isBlank()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("genre_eq_bar"),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = listOfNotNull(title.takeIf { it.isNotBlank() }, artist.takeIf { it.isNotBlank() })
                .joinToString(" · "),
            color = ImmersiveTextSecondary,
            fontSize = 11.sp,
            maxLines = 1
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilledTonalButton(
                onClick = { viewModel.applyGenreEqFromNowPlaying() },
                modifier = Modifier.weight(1f).testTag("genre_eq_apply")
            ) {
                Text("Genre-EQ · $label", fontSize = 13.sp)
            }
        }
    }
}
