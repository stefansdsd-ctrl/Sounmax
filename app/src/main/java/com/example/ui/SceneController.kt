package com.example.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.provider.Settings
import android.widget.Toast
import com.example.media.HeadsetStatusMonitor
import com.example.media.NowPlayingMonitor
import com.example.media.NowPlayingTrack
import com.example.qs.AncQuickTileService
import com.example.qs.DspQuickTileService
import com.example.qs.SceneQuickTileService
import com.example.qs.SleepTimerQuickTileService
import com.example.dsp.AncMode
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

    private val _sceneLocked = MutableStateFlow(prefs.getBoolean("scene_locked", false))
    val sceneLocked: StateFlow<Boolean> = _sceneLocked.asStateFlow()

    val headsetMonitor = HeadsetStatusMonitor(
        app,
        onConnectionChanged = { connected ->
            if (connected) {
                val last = ListeningScenes.byId(_activeSceneId.value)
                if (last != null && (_sceneLocked.value || !_autoSceneEnabled.value)) {
                    applyListeningScene(last, silent = true)
                }
            }
        }
    )
    val headsetStatus = headsetMonitor.status

    private val _suggestedScene = MutableStateFlow(ListeningScenes.suggestedForHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))
    val suggestedScene: StateFlow<ListeningScene> = _suggestedScene.asStateFlow()

    private val _crossfeedEnabled = MutableStateFlow(prefs.getBoolean("crossfeed", false))
    val crossfeedEnabled: StateFlow<Boolean> = _crossfeedEnabled.asStateFlow()

    private val _eqLocked = MutableStateFlow(prefs.getBoolean("eq_locked", false))
    val eqLocked: StateFlow<Boolean> = _eqLocked.asStateFlow()

    private val _monoMix = MutableStateFlow(prefs.getBoolean("mono_mix", false))
    val monoMix: StateFlow<Boolean> = _monoMix.asStateFlow()

    private val _nightGuard = MutableStateFlow(prefs.getBoolean("night_guard", true))
    val nightGuard: StateFlow<Boolean> = _nightGuard.asStateFlow()

    private val _detectedHeadset = MutableStateFlow<String?>(null)
    val detectedHeadset: StateFlow<String?> = _detectedHeadset.asStateFlow()

    private val _autoNowPlaying = MutableStateFlow(prefs.getBoolean("auto_now_playing", true))
    val autoNowPlaying: StateFlow<Boolean> = _autoNowPlaying.asStateFlow()

    private val _adaptiveVolume = MutableStateFlow(prefs.getBoolean("adaptive_volume", true))
    val adaptiveVolume: StateFlow<Boolean> = _adaptiveVolume.asStateFlow()

    private val _nowPlayingLabel = MutableStateFlow<String?>(null)
    val nowPlayingLabel: StateFlow<String?> = _nowPlayingLabel.asStateFlow()

    private var sleepJob: Job? = null
    private var doseJob: Job? = null
    private var savedVirtualizerForCrossfeed: Int? = null
    private var nowPlayingMonitor: NowPlayingMonitor? = null
    private var lastAdaptiveLoudness: Int? = null

    private val dspTileReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                DspQuickTileService.ACTION_TOGGLE_DSP -> {
                    viewModel.setDspEnabled(intent.getBooleanExtra("enabled", true))
                }
                SceneQuickTileService.ACTION_CYCLE_SCENE -> {
                    if (_sceneLocked.value) return
                    val scene = ListeningScenes.byId(intent.getStringExtra("scene_id")) ?: return
                    applyListeningScene(scene)
                }
                SleepTimerQuickTileService.ACTION_SLEEP_CHANGED -> {
                    startSleepTimer(intent.getIntExtra("minutes", 0))
                }
                AncQuickTileService.ACTION_CYCLE_ANC -> {
                    val name = intent.getStringExtra("anc") ?: return
                    val mode = runCatching { AncMode.valueOf(name) }.getOrNull() ?: return
                    viewModel.setAncMode(mode)
                }
            }
        }
    }

    init {
        if (_safeVolumeEnabled.value) setSafeVolume(true)
        if (_crossfeedEnabled.value) applyCrossfeedInternal(true)
        if (_monoMix.value) viewModel.dspManager.setMonoMix(true)
        detectHeadset(silent = true)
        headsetMonitor.start()
        startDoseTracker()
        try {
            val filter = IntentFilter().apply {
                addAction(DspQuickTileService.ACTION_TOGGLE_DSP)
                addAction(SceneQuickTileService.ACTION_CYCLE_SCENE)
                addAction(SleepTimerQuickTileService.ACTION_SLEEP_CHANGED)
                addAction(AncQuickTileService.ACTION_CYCLE_ANC)
            }
            app.registerReceiver(dspTileReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } catch (_: Exception) {
            val filter = IntentFilter().apply {
                addAction(DspQuickTileService.ACTION_TOGGLE_DSP)
                addAction(SceneQuickTileService.ACTION_CYCLE_SCENE)
                addAction(SleepTimerQuickTileService.ACTION_SLEEP_CHANGED)
                addAction(AncQuickTileService.ACTION_CYCLE_ANC)
            }
            app.registerReceiver(dspTileReceiver, filter)
        }
        if (_autoNowPlaying.value) startNowPlaying()
        if (_adaptiveVolume.value) applyAdaptiveVolume()
        val last = ListeningScenes.byId(_activeSceneId.value)
        when {
            last != null && !_autoSceneEnabled.value -> applyListeningScene(last, silent = true)
            _autoSceneEnabled.value -> applyListeningScene(_suggestedScene.value, silent = true)
        }
    }

    fun applyListeningScene(scene: ListeningScene, silent: Boolean = false) {
        if (_eqLocked.value) {
            viewModel.setAncMode(scene.ancMode)
            scene.preferredCodec?.let { viewModel.setCodec(it) }
            setSafeVolume(scene.safeVolume)
            _activeSceneId.value = scene.id
            prefs.edit().putString("last_scene_id", scene.id).putString("last_anc", scene.ancMode.name).apply()
            if (!silent) Toast.makeText(app, "Scene ${scene.name} (EQ vergrendeld)", Toast.LENGTH_SHORT).show()
            return
        }
        val preset = BuiltinPresets.PRESETS.firstOrNull { it.name == scene.presetName }
            ?: BuiltinPresets.PRESETS.last()
        viewModel.applyPreset(preset)
        viewModel.setAncMode(scene.ancMode)
        scene.preferredCodec?.let { viewModel.setCodec(it) }
        setSafeVolume(scene.safeVolume)
        _activeSceneId.value = scene.id
        prefs.edit().putString("last_scene_id", scene.id).putString("last_anc", scene.ancMode.name).apply()
        if (_crossfeedEnabled.value) applyCrossfeedInternal(true)
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

    fun setSceneLocked(locked: Boolean) {
        _sceneLocked.value = locked
        prefs.edit().putBoolean("scene_locked", locked).apply()
        if (locked) {
            _autoSceneEnabled.value = false
            prefs.edit().putBoolean("auto_scene", false).apply()
        }
        Toast.makeText(
            app,
            if (locked) "Scene vergrendeld — auto/beweging uit" else "Scene ontgrendeld",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setAutoSceneEnabled(enabled: Boolean) {
        if (enabled && _sceneLocked.value) {
            Toast.makeText(app, "Eerst scene-slot uit", Toast.LENGTH_SHORT).show()
            return
        }
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

    fun setEqLocked(locked: Boolean) {
        _eqLocked.value = locked
        prefs.edit().putBoolean("eq_locked", locked).apply()
        Toast.makeText(
            app,
            if (locked) "EQ vergrendeld — scenes wijzigen ANC/codec, niet bands"
            else "EQ ontgrendeld",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setCrossfeed(enabled: Boolean) {
        _crossfeedEnabled.value = enabled
        prefs.edit().putBoolean("crossfeed", enabled).apply()
        applyCrossfeedInternal(enabled)
        Toast.makeText(
            app,
            if (enabled) "Crossfeed aan — minder in-head stereo"
            else "Crossfeed uit",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun applyCrossfeedInternal(enabled: Boolean) {
        if (enabled) {
            if (savedVirtualizerForCrossfeed == null) {
                savedVirtualizerForCrossfeed = viewModel.dspManager.virtualizerStrength.value
            }
            viewModel.setVirtualizer(280)
            viewModel.setClarity((viewModel.dspManager.clarityGain.value).coerceAtLeast(3.5f))
        } else {
            savedVirtualizerForCrossfeed?.let { viewModel.setVirtualizer(it) }
            savedVirtualizerForCrossfeed = null
        }
    }

    fun shareCurrentEq() {
        val bands = viewModel.dspManager.bandGains.value
        val preset = viewModel.dspManager.currentPreset.value
        val text = buildString {
            appendLine("Sounmax EQ: ${preset.name}")
            appendLine("Bands (dB): ${bands.joinToString(", ") { String.format(\"%.1f\", it) }}")
            appendLine("Bass ${viewModel.dspManager.bassBoostStrength.value} | Spatial ${viewModel.dspManager.virtualizerStrength.value}")
            appendLine("Loudness ${viewModel.dspManager.loudnessGain.value} | Clarity ${viewModel.dspManager.clarityGain.value}")
            appendLine("ANC ${viewModel.dspManager.ancMode.value.displayName} | Codec ${viewModel.dspManager.selectedCodec.value.codecName}")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Sounmax preset: ${preset.name}")
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(Intent.createChooser(intent, "Deel EQ-preset").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun startSleepTimer(minutes: Int) {
        sleepJob?.cancel()
        _sleepTimerMinutes.value = minutes
        if (minutes <= 0) return
        sleepJob = scope.launch {
            var left = minutes
            while (left > 1) {
                delay(60_000)
                left -= 1
                _sleepTimerMinutes.value = left
            }
            fadeOutAndStop()
        }
        Toast.makeText(app, "Slaaptimer: $minutes min", Toast.LENGTH_SHORT).show()
    }

    private suspend fun fadeOutAndStop() {
        val startLoud = viewModel.dspManager.loudnessGain.value
        val startBass = viewModel.dspManager.bassBoostStrength.value
        for (step in 8 downTo 0) {
            _sleepTimerMinutes.value = if (step == 0) 0 else 1
            viewModel.setLoudness((startLoud * step / 8f).toInt())
            viewModel.setBassBoost((startBass * step / 8f).toInt())
            delay(7_500)
        }
        viewModel.setDspEnabled(false)
        viewModel.setLoudness(startLoud)
        viewModel.setBassBoost(startBass)
        Toast.makeText(app, "Slaaptimer: fade-out klaar, DSP uit", Toast.LENGTH_LONG).show()
    }

    fun snapshotEq(slot: String) = viewModel.snapshotAb(slot)
    fun toggleEqAb() = viewModel.toggleAb()

    fun setMonoMix(enabled: Boolean) {
        _monoMix.value = enabled
        prefs.edit().putBoolean("mono_mix", enabled).apply()
        viewModel.dspManager.setMonoMix(enabled)
        Toast.makeText(app, if (enabled) "Mono-mix aan" else "Stereo aan", Toast.LENGTH_SHORT).show()
    }

    fun setNightGuard(enabled: Boolean) {
        _nightGuard.value = enabled
        prefs.edit().putBoolean("night_guard", enabled).apply()
        if (enabled) applyNightGuardIfNeeded()
        Toast.makeText(app, if (enabled) "Nachtwacht aan" else "Nachtwacht uit", Toast.LENGTH_SHORT).show()
    }

    fun detectHeadset(silent: Boolean = false) {
        val match = viewModel.dspManager.refreshConnectedHeadset()
        val raw = viewModel.dspManager.connectedHeadsetName.value
        _detectedHeadset.value = match?.name ?: raw
        if (!silent) {
            val msg = when {
                match != null -> "Koppeling: ${match.name}"
                raw != null -> "BT-apparaat: $raw (geen preset-match)"
                else -> "Geen headset gevonden — check Bluetooth-toestemming"
            }
            Toast.makeText(app, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun importEqFromClipboard() {
        val clip = app.getSystemService(android.content.ClipboardManager::class.java)
            ?.primaryClip?.getItemAt(0)?.coerceToText(app)?.toString().orEmpty()
        val parsed = parseEqShare(clip)
        if (parsed == null) {
            Toast.makeText(app, "Plak eerst een Sounmax EQ-tekst", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.applyPreset(parsed)
    }

    private fun parseEqShare(text: String): com.example.dsp.EqPreset? {
        val bandLine = text.lineSequence().firstOrNull { it.contains("Bands", ignoreCase = true) } ?: return null
        val nums = Regex("-?\\d+(?:\\.\\d+)?").findAll(bandLine).map { it.value.toFloat() }.toList()
        if (nums.size < 10) return null
        val name = text.lineSequence().firstOrNull { it.startsWith("Sounmax EQ") }
            ?.substringAfter(":")?.trim() ?: "Geïmporteerd"
        return com.example.dsp.EqPreset(
            name = name,
            bandGains = nums.take(10),
            isCustom = true,
            description = "Geïmporteerd via klembord"
        )
    }

    fun setAutoNowPlaying(enabled: Boolean) {
        _autoNowPlaying.value = enabled
        prefs.edit().putBoolean("auto_now_playing", enabled).apply()
        if (enabled) startNowPlaying() else nowPlayingMonitor?.stop()
    }

    fun setAdaptiveVolume(enabled: Boolean) {
        _adaptiveVolume.value = enabled
        prefs.edit().putBoolean("adaptive_volume", enabled).apply()
        if (enabled) applyAdaptiveVolume()
    }

    fun openNotificationAccess() {
        try {
            app.startActivity(
                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (_: Exception) {}
    }

    fun nowPlayingHasAccess(): Boolean = nowPlayingMonitor?.hasAccess() == true

    private fun startNowPlaying() {
        nowPlayingMonitor?.stop()
        nowPlayingMonitor = NowPlayingMonitor(app) { track -> onNowPlaying(track) }.also { it.start() }
    }

    private fun onNowPlaying(track: NowPlayingTrack) {
        _nowPlayingLabel.value = listOf(track.title, track.artist).filter { it.isNotBlank() }.joinToString(" · ")
        if (!_autoNowPlaying.value || _eqLocked.value) return
        val blob = "${track.genre} ${track.title} ${track.artist} ${track.packageName}"
        applyGenreHint(blob)
    }

    fun applyAdaptiveVolume() {
        if (!_adaptiveVolume.value) return
        val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val ratio = cur.toFloat() / max
        val target = when {
            ratio < 0.25f -> 450
            ratio < 0.45f -> 320
            ratio < 0.70f -> 220
            else -> 80
        }
        if (lastAdaptiveLoudness != target) {
            lastAdaptiveLoudness = target
            viewModel.setLoudness(target)
        }
    }

    fun applyGenreHint(genre: String) {
        val sceneId = when {
            genre.contains("hip", true) || genre.contains("rap", true) -> "sport"
            genre.contains("edm", true) || genre.contains("electro", true) -> "gym"
            genre.contains("classic", true) || genre.contains("jazz", true) -> "focus"
            genre.contains("podcast", true) || genre.contains("speech", true) -> "podcast"
            genre.contains("lofi", true) || genre.contains("chill", true) -> "night"
            genre.contains("rock", true) || genre.contains("metal", true) -> "game"
            genre.contains("pop", true) -> "party"
            else -> return
        }
        ListeningScenes.byId(sceneId)?.let { applyListeningScene(it) }
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

    private fun applyNightGuardIfNeeded() {
        if (!_nightGuard.value) return
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour >= 22 || hour < 6) {
            setSafeVolume(true)
            if (viewModel.dspManager.loudnessGain.value > 250) viewModel.setLoudness(250)
        }
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
                    if (next == 60 || next == 180) {
                        Toast.makeText(app, "Pauze: ${next} min luisteren. 5 min stilte is beter voor je oren.", Toast.LENGTH_LONG).show()
                    }
                    if (next == 120) {
                        Toast.makeText(app, "Gehoor: 2 uur luisteren vandaag. Pauze aanbevolen.", Toast.LENGTH_LONG).show()
                    }
                    if (next == 180 && !_sceneLocked.value) {
                        ListeningScenes.byId("rest")?.let { applyListeningScene(it, silent = true) }
                        Toast.makeText(app, "3 uur luisteren — scene Rust + veilig volume.", Toast.LENGTH_LONG).show()
                    }
                    applyNightGuardIfNeeded()
                }
            }
        }
    }

    private fun todayKey(): String {
        val cal = Calendar.getInstance()
        return "dose_${cal.get(Calendar.YEAR)}_${cal.get(Calendar.DAY_OF_YEAR)}"
    }
}
