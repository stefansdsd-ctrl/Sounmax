package com.example.ui

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.data.SavedTrackEntity
import com.example.data.SoundMaxDatabase
import com.example.dsp.BuiltinPresets
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class SceneController(private val viewModel: MainViewModel) {
    private val app = viewModel.getApplication<Application>()
    private val scope = viewModel.viewModelScope
    private val db = SoundMaxDatabase.getDatabase(app)
    private val savedTrackDao = db.savedTrackDao()
    private val prefs = app.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)

    val savedTracks: StateFlow<List<SavedTrackEntity>> = savedTrackDao.getAllSavedTracks()
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSceneId = MutableStateFlow(prefs.getString("last_scene_id", null))
    val activeSceneId: StateFlow<String?> = _activeSceneId.asStateFlow()

    private val _favoriteIds = MutableStateFlow(
        prefs.getStringSet("favorite_scenes", emptySet())?.toSet() ?: emptySet()
    )
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _safeVolumeEnabled = MutableStateFlow(prefs.getBoolean("safe_volume", false))
    val safeVolumeEnabled: StateFlow<Boolean> = _safeVolumeEnabled.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    private val _listeningMinutesToday = MutableStateFlow(prefs.getInt(todayKey(), 0))
    val listeningMinutesToday: StateFlow<Int> = _listeningMinutesToday.asStateFlow()

    private val _autoSceneEnabled = MutableStateFlow(prefs.getBoolean("auto_scene", true))
    val autoSceneEnabled: StateFlow<Boolean> = _autoSceneEnabled.asStateFlow()

    private val _suggestedScene = MutableStateFlow(ListeningScenes.suggestedForHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))
    val suggestedScene: StateFlow<ListeningScene> = _suggestedScene.asStateFlow()

    private var sleepJob: Job? = null
    private var doseJob: Job? = null

    init {
        if (_safeVolumeEnabled.value) setSafeVolume(true)
        startDoseTracker()
        val last = ListeningScenes.byId(_activeSceneId.value)
        when {
            last != null && !_autoSceneEnabled.value -> applyListeningScene(last, silent = true)
            _autoSceneEnabled.value -> applyListeningScene(_suggestedScene.value, silent = true)
        }
    }

    fun applyListeningScene(scene: ListeningScene, silent: Boolean = false) {
        val preset = BuiltinPresets.PRESETS.firstOrNull { it.name == scene.presetName }
            ?: BuiltinPresets.PRESETS.last()
        viewModel.applyPreset(preset)
        viewModel.setAncMode(scene.ancMode)
        scene.preferredCodec?.let { viewModel.setCodec(it) }
        setSafeVolume(scene.safeVolume)
        _activeSceneId.value = scene.id
        prefs.edit().putString("last_scene_id", scene.id).apply()
        if (!silent) {
            Toast.makeText(app, "Scene: ${scene.name}", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleFavorite(sceneId: String) {
        val next = _favoriteIds.value.toMutableSet()
        if (!next.add(sceneId)) next.remove(sceneId)
        _favoriteIds.value = next
        prefs.edit().putStringSet("favorite_scenes", next).apply()
    }

    fun orderedScenes(): List<ListeningScene> {
        val favs = _favoriteIds.value
        return ListeningScenes.ALL.sortedByDescending { it.id in favs }
    }

    fun setAutoSceneEnabled(enabled: Boolean) {
        _autoSceneEnabled.value = enabled
        prefs.edit().putBoolean("auto_scene", enabled).apply()
        if (enabled) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val scene = ListeningScenes.suggestedForHour(hour)
            _suggestedScene.value = scene
            applyListeningScene(scene)
        }
    }

    fun refreshSuggestedScene() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        _suggestedScene.value = ListeningScenes.suggestedForHour(hour)
    }

    fun setSafeVolume(enabled: Boolean) {
        _safeVolumeEnabled.value = enabled
        prefs.edit().putBoolean("safe_volume", enabled).apply()
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

    private fun startDoseTracker() {
        doseJob?.cancel()
        doseJob = scope.launch {
            while (true) {
                delay(60_000)
                if (viewModel.dspManager.isDspEnabled.value) {
                    val key = todayKey()
                    val next = prefs.getInt(key, 0) + 1
                    prefs.edit().putInt(key, next).apply()
                    _listeningMinutesToday.value = next
                    if (next == 120) {
                        Toast.makeText(app, "Gehoor: 2 uur luisteren vandaag. Pauze aanbevolen.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun todayKey(): String {
        val cal = Calendar.getInstance()
        return "dose_${cal.get(Calendar.YEAR)}_${cal.get(Calendar.DAY_OF_YEAR)}"
    }
}
