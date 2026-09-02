package com.example.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.data.SavedTrackEntity
import com.example.data.SoundMaxDatabase
import com.example.dsp.AdaptiveTrackEq
import com.example.dsp.AncMode
import com.example.dsp.BuiltinPresets
import com.example.dsp.LdacQualityMode
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.example.media.HeadsetStatusMonitor
import com.example.media.NowPlayingMonitor
import com.example.media.NowPlayingTrack
import com.example.media.SceneAutomation
import com.example.media.SleepFade
import com.example.media.WeatherAdvisor
import com.example.qs.AncQuickTileService
import com.example.qs.DspQuickTileService
import com.example.qs.SceneQuickTileService
import com.example.qs.SleepTimerQuickTileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SceneController(private val viewModel: MainViewModel) {
    private val app = viewModel.getApplication<Application>()
    private val scope = viewModel.viewModelScope
    private val db = SoundMaxDatabase.getDatabase(app)
    private val savedTrackDao = db.savedTrackDao()
    private val prefs = app.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)

    val savedTracks: StateFlow<List<SavedTrackEntity>> =
        savedTrackDao.getAllSavedTracks().stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSceneId = MutableStateFlow(prefs.getString("last_scene_id", null))
    val activeSceneId: StateFlow<String?> = _activeSceneId.asStateFlow()
    private val _favoriteIds = MutableStateFlow(prefs.getStringSet("favorite_scenes", emptySet())?.toSet() ?: emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()
    private val _safeVolumeEnabled = MutableStateFlow(prefs.getBoolean("safe_volume", false))
    val safeVolumeEnabled: StateFlow<Boolean> = _safeVolumeEnabled.asStateFlow()
    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()
    private val _listeningMinutesToday = MutableStateFlow(prefs.getInt(todayKey(), 0))
    val listeningMinutesToday: StateFlow<Int> = _listeningMinutesToday.asStateFlow()
    private val _listeningMinutesWeek = MutableStateFlow(prefs.getInt(weekKey(), 0))
    val listeningMinutesWeek: StateFlow<Int> = _listeningMinutesWeek.asStateFlow()
    private val _autoSceneEnabled = MutableStateFlow(prefs.getBoolean("auto_scene", true))
    val autoSceneEnabled: StateFlow<Boolean> = _autoSceneEnabled.asStateFlow()
    private val _sceneLocked = MutableStateFlow(prefs.getBoolean("scene_locked", false))
    val sceneLocked: StateFlow<Boolean> = _sceneLocked.asStateFlow()
    val headsetMonitor = HeadsetStatusMonitor(app, onConnectionChanged = { connected ->
        if (connected) {
            val last = ListeningScenes.byId(_activeSceneId.value)
            if (last != null && (_sceneLocked.value || !_autoSceneEnabled.value)) applyListeningScene(last, silent = true)
        }
    })
    val headsetStatus = headsetMonitor.status
    private val _suggestedScene = MutableStateFlow(ListeningScenes.suggestedNow())
    val suggestedScene: StateFlow<ListeningScene> = _suggestedScene.asStateFlow()
    private val _weatherEnabled = MutableStateFlow(WeatherAdvisor.enabled(app))
    val weatherEnabled: StateFlow<Boolean> = _weatherEnabled.asStateFlow()
    private val _weatherLabel = MutableStateFlow(WeatherAdvisor.lastLabel(app))
    val weatherLabel: StateFlow<String?> = _weatherLabel.asStateFlow()
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
    private val _adaptiveEq = MutableStateFlow(prefs.getBoolean("adaptive_eq", true))
    val adaptiveEq: StateFlow<Boolean> = _adaptiveEq.asStateFlow()
    private val _adaptiveEqLabel = MutableStateFlow<String?>(null)
    val adaptiveEqLabel: StateFlow<String?> = _adaptiveEqLabel.asStateFlow()
    private val _adaptiveVolume = MutableStateFlow(prefs.getBoolean("adaptive_volume", true))
    val adaptiveVolume: StateFlow<Boolean> = _adaptiveVolume.asStateFlow()
    private val _nowPlayingLabel = MutableStateFlow<String?>(null)
    val nowPlayingLabel: StateFlow<String?> = _nowPlayingLabel.asStateFlow()

    private var sleepJob: Job? = null
    private var doseJob: Job? = null
    private var savedVirtualizerForCrossfeed: Int? = null
    private var nowPlayingMonitor: NowPlayingMonitor? = null
    private var lastAdaptiveLoudness: Int? = null
    private var lastAppSceneId: String? = null
    private var lastAdaptiveOffsets: List<Float>? = null
    private var lastAdaptiveBass = 0
    private var lastAdaptiveClarity = 0f
    private var lastAdaptiveKey: String? = null

    private val dspTileReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                DspQuickTileService.ACTION_TOGGLE_DSP -> viewModel.setDspEnabled(intent.getBooleanExtra("enabled", true))
                SceneQuickTileService.ACTION_CYCLE_SCENE -> {
                    if (_sceneLocked.value) return
                    ListeningScenes.byId(intent.getStringExtra("scene_id"))?.let { applyListeningScene(it) }
                }
                SleepTimerQuickTileService.ACTION_SLEEP_CHANGED -> startSleepTimer(intent.getIntExtra("minutes", 0))
                AncQuickTileService.ACTION_CYCLE_ANC -> {
                    val mode = runCatching { AncMode.valueOf(intent.getStringExtra("anc") ?: return) }.getOrNull() ?: return
                    viewModel.setAncMode(mode)
                }
            }
        }
    }

    init {
        if (_safeVolumeEnabled.value) setSafeVolume(true)
        if (_crossfeedEnabled.value) applyCrossfeedInternal(true)
        detectHeadset(silent = true)
        headsetMonitor.start()
        SceneAutomation.scheduleHourly(app)
        refreshSuggestedScene()
        SceneAutomation.consumePending(app) { applyListeningScene(it, silent = true) }
        scope.launch {
            while (true) {
                delay(8_000)
                SceneAutomation.consumePending(app) { applyListeningScene(it, silent = true) }
                refreshSuggestedScene()
            }
        }
        startDoseTracker()
        val filter = IntentFilter().apply {
            addAction(DspQuickTileService.ACTION_TOGGLE_DSP)
            addAction(SceneQuickTileService.ACTION_CYCLE_SCENE)
            addAction(SleepTimerQuickTileService.ACTION_SLEEP_CHANGED)
            addAction(AncQuickTileService.ACTION_CYCLE_ANC)
        }
        try {
            app.registerReceiver(dspTileReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } catch (_: Exception) {
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
            maybeSaveBatteryLdac()
            return
        }
        val preset = BuiltinPresets.PRESETS.firstOrNull { it.name == scene.presetName } ?: BuiltinPresets.PRESETS.last()
        viewModel.applyPreset(preset)
        viewModel.setAncMode(scene.ancMode)
        scene.preferredCodec?.let { viewModel.setCodec(it) }
        setSafeVolume(scene.safeVolume)
        _activeSceneId.value = scene.id
        prefs.edit().putString("last_scene_id", scene.id).putString("last_anc", scene.ancMode.name).apply()
        if (_crossfeedEnabled.value) applyCrossfeedInternal(true)
        lastAdaptiveOffsets = null; lastAdaptiveBass = 0; lastAdaptiveClarity = 0f; lastAdaptiveKey = null
        if (!silent) Toast.makeText(app, "Scene: ${scene.name}", Toast.LENGTH_SHORT).show()
        maybeSaveBatteryLdac()
    }

    private fun maybeSaveBatteryLdac() {
        val bat = headsetStatus.value.batteryPercent ?: return
        if (bat <= 20) viewModel.dspManager.forceLdacCodec(LdacQualityMode.CONNECTION_330)
    }

    fun toggleFavorite(sceneId: String) {
        val next = _favoriteIds.value.toMutableSet()
        if (!next.add(sceneId)) next.remove(sceneId)
        _favoriteIds.value = next
        prefs.edit().putStringSet("favorite_scenes", next).apply()
    }

    fun orderedScenes(): List<ListeningScene> =
        ListeningScenes.ALL.sortedByDescending { it.id in _favoriteIds.value }

    fun filteredScenes(query: String): List<ListeningScene> {
        val q = query.trim().lowercase()
        val base = orderedScenes()
        if (q.isEmpty()) return base
        return base.filter {
            it.name.lowercase().contains(q) || it.id.contains(q) ||
                it.description.lowercase().contains(q) || it.presetName.lowercase().contains(q)
        }
    }

    fun shareGattDump() {
        val s = headsetStatus.value
        val text = buildString {
            appendLine("Sounmax GATT-dump")
            appendLine("Headset: ${s.name ?: "-"}")
            appendLine("Accu: ${s.batteryPercent ?: "-"}%")
            appendLine("RSSI: ${s.rssiDbm ?: "-"} dBm live=${s.rssiLiveGatt}")
            appendLine("GATT ready=${s.gattReady} known=${s.knownServices} unknown=${s.unknownServices}")
            s.discoveryLogs.forEach { appendLine("${it.title}: ${it.detail}") }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Sounmax GATT-dump")
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(Intent.createChooser(intent, "Deel GATT-dump").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun setSceneLocked(locked: Boolean) {
        _sceneLocked.value = locked
        prefs.edit().putBoolean("scene_locked", locked).apply()
        if (locked) {
            _autoSceneEnabled.value = false
            prefs.edit().putBoolean("auto_scene", false).apply()
        }
        Toast.makeText(app, if (locked) "Scene vergrendeld" else "Scene ontgrendeld", Toast.LENGTH_SHORT).show()
    }

    fun setAutoSceneEnabled(enabled: Boolean) {
        if (enabled && _sceneLocked.value) {
            Toast.makeText(app, "Eerst scene-slot uit", Toast.LENGTH_SHORT).show(); return
        }
        _autoSceneEnabled.value = enabled
        prefs.edit().putBoolean("auto_scene", enabled).apply()
        if (enabled) refreshSuggestedScene(applyIfAuto = true)
    }

    fun setWeatherEnabled(on: Boolean) {
        WeatherAdvisor.setEnabled(app, on)
        _weatherEnabled.value = on
        refreshSuggestedScene(applyIfAuto = on && _autoSceneEnabled.value && !_sceneLocked.value)
    }

    fun applyWeatherSuggestion() {
        if (_sceneLocked.value) return
        applyListeningScene(_suggestedScene.value)
    }

    fun refreshSuggestedScene(applyIfAuto: Boolean = false) {
        scope.launch {
            val base = ListeningScenes.suggestedNow(_listeningMinutesWeek.value)
            val scene = withContext(Dispatchers.IO) { WeatherAdvisor.suggest(app, base) }
            _suggestedScene.value = scene
            _weatherLabel.value = WeatherAdvisor.lastLabel(app)
            _weatherEnabled.value = WeatherAdvisor.enabled(app)
            if (applyIfAuto && _autoSceneEnabled.value && !_sceneLocked.value) applyListeningScene(scene, silent = true)
        }
    }

    fun setSafeVolume(enabled: Boolean) {
        _safeVolumeEnabled.value = enabled
        prefs.edit().putBoolean("safe_volume", enabled).apply()
        if (enabled && viewModel.dspManager.loudnessGain.value > 400) viewModel.setLoudness(400)
        val am = app.getSystemService(AudioManager::class.java)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * 0.7f).toInt().coerceAtLeast(1)
        if (enabled && am.getStreamVolume(AudioManager.STREAM_MUSIC) > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
        }
    }

    fun setEqLocked(locked: Boolean) {
        _eqLocked.value = locked
        prefs.edit().putBoolean("eq_locked", locked).apply()
    }

    fun setCrossfeed(enabled: Boolean) {
        _crossfeedEnabled.value = enabled
        prefs.edit().putBoolean("crossfeed", enabled).apply()
        applyCrossfeedInternal(enabled)
    }

    private fun applyCrossfeedInternal(enabled: Boolean) {
        if (enabled) {
            if (savedVirtualizerForCrossfeed == null) savedVirtualizerForCrossfeed = viewModel.dspManager.virtualizerStrength.value
            viewModel.setVirtualizer(280)
        } else {
            savedVirtualizerForCrossfeed?.let { viewModel.setVirtualizer(it) }
            savedVirtualizerForCrossfeed = null
        }
    }

    fun shareCurrentEq() {
        val bands = viewModel.dspManager.bandGains.value
        val preset = viewModel.dspManager.currentPreset.value
        val text = "Sounmax EQ: ${preset?.name}\nBands: ${bands.joinToString()}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(Intent.createChooser(intent, "Deel EQ").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun startSleepTimer(minutes: Int) {
        sleepJob?.cancel()
        _sleepTimerMinutes.value = minutes
        if (minutes <= 0) return
        SleepFade.schedule(app, System.currentTimeMillis() + minutes * 60_000L)
        sleepJob = scope.launch {
            var left = minutes
            while (left > 1) {
                delay(60_000)
                left -= 1
                _sleepTimerMinutes.value = left
            }
            SleepFade.run(app, viewModel.dspManager)
            _sleepTimerMinutes.value = 0
        }
        Toast.makeText(app, "Slaaptimer: $minutes min", Toast.LENGTH_SHORT).show()
    }

    fun cancelSleepTimer() {
        sleepJob?.cancel()
        _sleepTimerMinutes.value = 0
        SleepFade.cancel(app)
    }

    fun snapshotEq(slot: String) = viewModel.snapshotAb(slot)
    fun toggleEqAb() = viewModel.toggleAb()

    fun setMonoMix(enabled: Boolean) {
        _monoMix.value = enabled
        prefs.edit().putBoolean("mono_mix", enabled).apply()
    }

    fun setNightGuard(enabled: Boolean) {
        _nightGuard.value = enabled
        prefs.edit().putBoolean("night_guard", enabled).apply()
        if (enabled) applyNightGuardIfNeeded()
    }

    fun detectHeadset(silent: Boolean = false) {
        val raw = viewModel.dspManager.connectedHeadsetName.value
        _detectedHeadset.value = raw
        if (!silent) Toast.makeText(app, raw ?: "Geen headset", Toast.LENGTH_SHORT).show()
    }

    fun importEqFromClipboard() {
        val clip = app.getSystemService(android.content.ClipboardManager::class.java)
            ?.primaryClip?.getItemAt(0)?.coerceToText(app)?.toString().orEmpty()
        viewModel.importEqJson(clip)
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
            app.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {}
    }

    fun nowPlayingHasAccess(): Boolean = nowPlayingMonitor?.hasAccess() == true

    private fun startNowPlaying() {
        nowPlayingMonitor?.stop()
        nowPlayingMonitor = NowPlayingMonitor(app) { onNowPlaying(it) }.also { it.start() }
    }

    private fun onNowPlaying(track: NowPlayingTrack) {
        _nowPlayingLabel.value = listOf(track.title, track.artist).filter { it.isNotBlank() }.joinToString(" · ")
        applyAdaptiveTrackEq(track)
        if (!_autoNowPlaying.value || _sceneLocked.value) return
        val scene = ListeningScenes.fromNowPlaying(track.packageName, track.genre, "${track.title} ${track.artist}") ?: return
        if (scene.id == lastAppSceneId || scene.id == _activeSceneId.value) return
        lastAppSceneId = scene.id
        applyListeningScene(scene, silent = true)
        applyAdaptiveTrackEq(track, force = true)
    }

    fun setAdaptiveEq(enabled: Boolean) {
        _adaptiveEq.value = enabled
        prefs.edit().putBoolean("adaptive_eq", enabled).apply()
        if (!enabled) {
            revertAdaptiveEq()
            _adaptiveEqLabel.value = null
        }
    }

    private fun applyAdaptiveTrackEq(track: NowPlayingTrack, force: Boolean = false) {
        if (!_adaptiveEq.value || _eqLocked.value) return
        val key = "${track.packageName}|${track.title}|${track.artist}|${track.genre}"
        if (!force && key == lastAdaptiveKey) return
        val hint = AdaptiveTrackEq.hint(track.genre, track.title, track.artist)
        revertAdaptiveEq()
        lastAdaptiveKey = key
        if (hint.label == AdaptiveTrackEq.NONE.label) {
            _adaptiveEqLabel.value = null; return
        }
        val dsp = viewModel.dspManager
        hint.offsetsDb.forEachIndexed { i, delta ->
            if (delta != 0f) dsp.updateBandGain(i, dsp.bandGains.value.getOrElse(i) { 0f } + delta)
        }
        lastAdaptiveOffsets = hint.offsetsDb
        lastAdaptiveBass = hint.bassDelta
        lastAdaptiveClarity = hint.clarityDelta
        _adaptiveEqLabel.value = hint.label
    }

    private fun revertAdaptiveEq() {
        val dsp = viewModel.dspManager
        lastAdaptiveOffsets?.forEachIndexed { i, delta ->
            if (delta != 0f) dsp.updateBandGain(i, dsp.bandGains.value.getOrElse(i) { 0f } - delta)
        }
        lastAdaptiveOffsets = null; lastAdaptiveBass = 0; lastAdaptiveClarity = 0f
    }

    fun applyAdaptiveVolume() {
        if (!_adaptiveVolume.value) return
        val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val ratio = am.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / max
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
        if (_sceneLocked.value) return
        ListeningScenes.fromNowPlaying("", genre, genre)?.let {
            if (it.id != _activeSceneId.value) applyListeningScene(it)
        }
    }

    fun saveTrackFavorite(videoId: String, title: String, artist: String, genre: String = "") {
        scope.launch {
            savedTrackDao.insertTrack(
                SavedTrackEntity(
                    videoId = videoId, title = title, artist = artist, genre = genre,
                    recommendedPreset = viewModel.dspManager.currentPreset.value?.name ?: ""
                )
            )
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
                    val weekNext = prefs.getInt(weekKey(), 0) + 1
                    prefs.edit().putInt(key, next).putInt(weekKey(), weekNext).apply()
                    _listeningMinutesToday.value = next
                    _listeningMinutesWeek.value = weekNext
                    if ((next == 180 || weekNext == 600) && !_sceneLocked.value) {
                        ListeningScenes.byId("rest")?.let { applyListeningScene(it, silent = true) }
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

    private fun weekKey(): String {
        val cal = Calendar.getInstance()
        return "dose_week_${cal.get(Calendar.YEAR)}_${cal.get(Calendar.WEEK_OF_YEAR)}"
    }
}
