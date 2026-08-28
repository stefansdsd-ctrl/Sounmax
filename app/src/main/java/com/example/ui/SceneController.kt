package com.example.ui

import android.app.Application
import android.media.AudioManager
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.data.SavedTrackEntity
import com.example.data.SoundMaxDatabase
import com.example.dsp.BuiltinPresets
import com.example.dsp.ListeningScene
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SceneController(private val viewModel: MainViewModel) {
    private val app = viewModel.getApplication<Application>()
    private val scope = viewModel.viewModelScope
    private val db = SoundMaxDatabase.getDatabase(app)
    private val savedTrackDao = db.savedTrackDao()

    val savedTracks: StateFlow<List<SavedTrackEntity>> = savedTrackDao.getAllSavedTracks()
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSceneId = MutableStateFlow<String?>(null)
    val activeSceneId: StateFlow<String?> = _activeSceneId.asStateFlow()

    private val _safeVolumeEnabled = MutableStateFlow(false)
    val safeVolumeEnabled: StateFlow<Boolean> = _safeVolumeEnabled.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    private var sleepJob: Job? = null

    fun applyListeningScene(scene: ListeningScene) {
        val preset = BuiltinPresets.PRESETS.firstOrNull { it.name == scene.presetName }
            ?: BuiltinPresets.PRESETS.last()
        viewModel.applyPreset(preset)
        viewModel.setAncMode(scene.ancMode)
        setSafeVolume(scene.safeVolume)
        _activeSceneId.value = scene.id
        Toast.makeText(app, "Scene: ${scene.name}", Toast.LENGTH_SHORT).show()
    }

    fun setSafeVolume(enabled: Boolean) {
        _safeVolumeEnabled.value = enabled
        if (enabled && viewModel.dspManager.loudnessGain.value > 400) {
            viewModel.setLoudness(400)
        }
        val am = app.getSystemService(AudioManager::class.java)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * 0.7f).toInt().coerceAtLeast(1)
        if (enabled && am.getStreamVolume(AudioManager.STREAM_MUSIC) > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            Toast.makeText(app, "Veilig volume: max 70%", Toast.LENGTH_SHORT).show()
        }
    }

    fun startSleepTimer(minutes: Int) {
        sleepJob?.cancel()
        _sleepTimerMinutes.value = minutes
        if (minutes <= 0) return
        sleepJob = scope.launch {
            var left = minutes
            while (left > 0) {
                delay(60_000)
                left -= 1
                _sleepTimerMinutes.value = left
            }
            viewModel.setDspEnabled(false)
            Toast.makeText(app, "Slaaptimer: DSP uit", Toast.LENGTH_LONG).show()
        }
        Toast.makeText(app, "Slaaptimer: $minutes min", Toast.LENGTH_SHORT).show()
    }

    fun cancelSleepTimer() {
        sleepJob?.cancel()
        _sleepTimerMinutes.value = 0
    }

    fun saveTrackFavorite(videoId: String, title: String, artist: String, genre: String = "") {
        scope.launch {
            savedTrackDao.insertTrack(
                SavedTrackEntity(
                    videoId = videoId,
                    title = title,
                    artist = artist,
                    genre = genre,
                    recommendedPreset = viewModel.dspManager.currentPreset.value.name
                )
            )
            Toast.makeText(app, "Favoriet opgeslagen", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteTrackFavorite(track: SavedTrackEntity) {
        scope.launch { savedTrackDao.deleteTrack(track) }
    }
}
