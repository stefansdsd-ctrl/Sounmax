package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.ble.DiscoveryLogItem
import com.example.dsp.AdaptiveTrackEq
import com.example.dsp.BuiltinPresets
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.example.media.HeadsetStatusMonitor
import com.example.media.NowPlayingMonitor
import com.example.media.NowPlayingTrack
import com.example.media.SceneAutomation
import com.example.media.SleepFade
import com.example.media.WeatherAdvisor
import com.example.widget.SoundMaxWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class SceneHeadsetStatus(
    val connected: Boolean = false,
    val name: String? = null,
    val batteryPercent: Int? = null,
    val wired: Boolean = false,
    val rssiDbm: Int? = null,
    val rssiLiveGatt: Boolean = false,
    val gattReady: Boolean = false,
    val knownServices: Int = 0,
    val unknownServices: Int = 0,
    val discoveryLogs: List<DiscoveryLogItem> = emptyList()
)

class SceneController(private val viewModel: MainViewModel) {
    private val app: Application = viewModel.getApplication()
    private val prefs = app.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())

    private val _activeSceneId = MutableStateFlow(prefs.getString("last_scene_id", "focus") ?: "focus")
    val activeSceneId: StateFlow<String> = _activeSceneId.asStateFlow()
    private val _safeVolumeEnabled = MutableStateFlow(prefs.getBoolean("safe_volume", false))
    val safeVolumeEnabled: StateFlow<Boolean> = _safeVolumeEnabled.asStateFlow()
    private val _sleepTimerMinutes = MutableStateFlow(prefs.getInt(SoundMaxWidget.KEY_SLEEP_MINUTES, 0))
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()
    private val _autoSceneEnabled = MutableStateFlow(prefs.getBoolean("auto_scene", true))
    val autoSceneEnabled: StateFlow<Boolean> = _autoSceneEnabled.asStateFlow()
    private val _sceneLocked = MutableStateFlow(prefs.getBoolean("scene_locked", false))
    val sceneLocked: StateFlow<Boolean> = _sceneLocked.asStateFlow()
    private val _suggestedScene = MutableStateFlow(ListeningScenes.suggestedNow(weekDoseMinutes()))
    val suggestedScene: StateFlow<ListeningScene> = _suggestedScene.asStateFlow()
    private val _listeningMinutesToday = MutableStateFlow(todayDoseMinutes())
    val listeningMinutesToday: StateFlow<Int> = _listeningMinutesToday.asStateFlow()
    private val _listeningMinutesWeek = MutableStateFlow(weekDoseMinutes())
    val listeningMinutesWeek: StateFlow<Int> = _listeningMinutesWeek.asStateFlow()
    private val _favoriteIds = MutableStateFlow(loadIdSet("fav_scenes"))
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()
    private val _recentIds = MutableStateFlow(loadIdList("recent_scenes"))
    val recentIds: StateFlow<List<String>> = _recentIds.asStateFlow()
    private val _crossfeedEnabled = MutableStateFlow(prefs.getBoolean("crossfeed", false))
    val crossfeedEnabled: StateFlow<Boolean> = _crossfeedEnabled.asStateFlow()
    private val _eqLocked = MutableStateFlow(prefs.getBoolean("eq_locked", false))
    val eqLocked: StateFlow<Boolean> = _eqLocked.asStateFlow()
    private val _monoMix = MutableStateFlow(prefs.getBoolean("mono_mix", false))
    val monoMix: StateFlow<Boolean> = _monoMix.asStateFlow()
    private val _nightGuard = MutableStateFlow(prefs.getBoolean("night_guard", true))
    val nightGuard: StateFlow<Boolean> = _nightGuard.asStateFlow()
    private val _detectedHeadset = MutableStateFlow(viewModel.dspManager.connectedHeadsetName.value)
    val detectedHeadset: StateFlow<String?> = _detectedHeadset.asStateFlow()
    private val _weatherEnabled = MutableStateFlow(WeatherAdvisor.enabled(app))
    val weatherEnabled: StateFlow<Boolean> = _weatherEnabled.asStateFlow()
    private val _weatherLabel = MutableStateFlow(WeatherAdvisor.lastLabel(app))
    val weatherLabel: StateFlow<String?> = _weatherLabel.asStateFlow()
    private val _adaptiveEq = MutableStateFlow(prefs.getBoolean("adaptive_eq", true))
    val adaptiveEq: StateFlow<Boolean> = _adaptiveEq.asStateFlow()
    private val _adaptiveEqLabel = MutableStateFlow(prefs.getString("adaptive_eq_label", null))
    val adaptiveEqLabel: StateFlow<String?> = _adaptiveEqLabel.asStateFlow()
    private val _headsetStatus = MutableStateFlow(SceneHeadsetStatus())
    val headsetStatus: StateFlow<SceneHeadsetStatus> = _headsetStatus.asStateFlow()
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val headsetMonitor = HeadsetStatusMonitor(
        app,
        onConnectionChanged = { connected -> if (connected) onHeadsetConnected() },
        onRssi = { rssi -> viewModel.dspManager.ingestLiveRssi(rssi) }
    )

    private var nowPlaying: NowPlayingMonitor? = null
    private var doseJob: Job? = null
    private var sleepTick: Runnable? = null

    init {
        headsetMonitor.start()
        nowPlaying = NowPlayingMonitor(app) { track -> onNowPlaying(track) }.also { it.start() }
        SceneAutomation.consumePending(app) { applyListeningScene(it, fromAuto = true) }
        refreshSuggestedScene()
        startDoseTicker()
        restoreLastSceneIfNeeded()
        tickSleepLeft()
        applyPersistedToggles()
        scope.launch(Dispatchers.IO) {
            runCatching { WeatherAdvisor.refreshBlocking(app) }
            launch(Dispatchers.Main) {
                _weatherLabel.value = WeatherAdvisor.lastLabel(app)
                refreshSuggestedScene()
            }
        }
    }

    fun orderedScenes(): List<ListeningScene> {
        val q = _query.value.trim().lowercase()
        val fav = _favoriteIds.value
        val rec = _recentIds.value
        val base = ListeningScenes.ALL.filter { scene ->
            if (q.isBlank()) true
            else scene.name.lowercase().contains(q) || scene.id.contains(q) || scene.description.lowercase().contains(q)
        }
        return base.sortedWith(
            compareByDescending<ListeningScene> { it.id in fav }
                .thenBy { rec.indexOf(it.id).let { i -> if (i < 0) 99 else i } }
                .thenBy { it.name }
        )
    }

    fun setQuery(value: String) { _query.value = value }

    fun applyListeningScene(scene: ListeningScene, fromAuto: Boolean = false) {
        if (fromAuto && _sceneLocked.value) return
        val preset = BuiltinPresets.PRESETS.firstOrNull { it.name == scene.presetName } ?: BuiltinPresets.PRESETS.first()
        if (!_eqLocked.value) viewModel.applyPreset(preset)
        viewModel.setAncMode(scene.ancMode)
        scene.preferredCodec?.let { viewModel.setCodec(it) }
        if (scene.safeVolume) setSafeVolume(true)
        if (scene.id == "oneear") setMonoMix(true)
        if (scene.id == "saver") applyBatterySaver()
        _activeSceneId.value = scene.id
        prefs.edit().putString("last_scene_id", scene.id)
            .putString("scene_for_" + (_detectedHeadset.value ?: "default"), scene.id).apply()
        pushRecent(scene.id)
        SoundMaxWidget.refreshAll(app)
        Toast.makeText(app, "${scene.emoji} ${scene.name}", Toast.LENGTH_SHORT).show()
    }

    fun toggleFavorite(id: String) {
        val next = _favoriteIds.value.toMutableSet()
        if (!next.add(id)) next.remove(id)
        _favoriteIds.value = next
        prefs.edit().putString("fav_scenes", next.joinToString(",")).apply()
    }

    fun setAutoSceneEnabled(on: Boolean) {
        _autoSceneEnabled.value = on
        prefs.edit().putBoolean("auto_scene", on).apply()
        if (on) applySuggestedNow()
    }

    fun setSceneLocked(on: Boolean) {
        _sceneLocked.value = on
        prefs.edit().putBoolean("scene_locked", on).apply()
    }

    fun setWeatherEnabled(on: Boolean) {
        _weatherEnabled.value = on
        WeatherAdvisor.setEnabled(app, on)
        refreshSuggestedScene()
    }

    fun applyWeatherSuggestion() {
        val scene = WeatherAdvisor.suggest(app, _suggestedScene.value)
        applyListeningScene(scene)
        _weatherLabel.value = WeatherAdvisor.lastLabel(app)
    }

    fun setAdaptiveEq(on: Boolean) {
        _adaptiveEq.value = on
        prefs.edit().putBoolean("adaptive_eq", on).apply()
    }

    fun setSafeVolume(on: Boolean) {
        _safeVolumeEnabled.value = on
        prefs.edit().putBoolean("safe_volume", on).apply()
        if (on) {
            val am = app.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            val max = am.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
            val cap = (max * 0.7f).toInt()
            if (am.getStreamVolume(android.media.AudioManager.STREAM_MUSIC) > cap) {
                runCatching { am.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, cap, 0) }
            }
            viewModel.setLoudness(viewModel.dspManager.loudnessGain.value.coerceAtMost(400))
        }
    }

    fun setNightGuard(on: Boolean) {
        _nightGuard.value = on
        prefs.edit().putBoolean("night_guard", on).apply()
        if (on && isNight()) {
            setSafeVolume(true)
            ListeningScenes.byId("night")?.let { applyListeningScene(it, fromAuto = true) }
        }
    }

    fun setCrossfeed(on: Boolean) {
        _crossfeedEnabled.value = on
        prefs.edit().putBoolean("crossfeed", on).apply()
        viewModel.setVirtualizer(if (on) 420 else 0)
    }

    fun setMonoMix(on: Boolean) {
        _monoMix.value = on
        prefs.edit().putBoolean("mono_mix", on).apply()
        viewModel.dspManager.setMonoMix(on)
    }

    fun setEqLocked(on: Boolean) {
        _eqLocked.value = on
        prefs.edit().putBoolean("eq_locked", on).apply()
    }

    fun detectHeadset() {
        val device = viewModel.dspManager.refreshConnectedHeadset()
        val name = device?.name ?: viewModel.dspManager.readConnectedBluetoothName()
        _detectedHeadset.value = name
        val raw = headsetMonitor.status.value
        _headsetStatus.value = _headsetStatus.value.copy(
            connected = raw.connected || name != null,
            name = name ?: raw.name,
            batteryPercent = raw.batteryPercent,
            rssiDbm = raw.rssiDbm,
            rssiLiveGatt = raw.rssiLiveGatt
        )
        Toast.makeText(app, name?.let { "Headset: $it" } ?: "Geen headset gevonden", Toast.LENGTH_SHORT).show()
        name?.let { restoreSceneForHeadset(it) }
    }

    fun shareCurrentEq() { viewModel.exportCurrentEq(app) }

    fun importEqFromClipboard() {
        val cm = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = cm.primaryClip?.getItemAt(0)?.coerceToText(app)?.toString().orEmpty()
        if (text.isBlank()) {
            Toast.makeText(app, "Klembord leeg", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.importEqJson(text)
    }

    fun startSleepTimer(minutes: Int) {
        val end = System.currentTimeMillis() + minutes * 60_000L
        prefs.edit().putLong(SoundMaxWidget.KEY_SLEEP_END, end).putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, minutes).apply()
        _sleepTimerMinutes.value = minutes
        SleepFade.schedule(app, end)
        tickSleepLeft()
        Toast.makeText(app, "Slaaptimer $minutes min", Toast.LENGTH_SHORT).show()
    }

    fun cancelSleepTimer() {
        SleepFade.cancel(app)
        _sleepTimerMinutes.value = 0
        sleepTick?.let { handler.removeCallbacks(it) }
    }

    fun snapshotEq(slot: String) {
        viewModel.snapshotAb(slot)
        Toast.makeText(app, "EQ $slot opgeslagen", Toast.LENGTH_SHORT).show()
    }

    fun toggleEqAb() { viewModel.toggleAb() }

    fun shareGattDump() {
        val status = _headsetStatus.value
        val dump = buildString {
            appendLine("Sounmax GATT dump")
            appendLine("headset=${status.name}")
            appendLine("battery=${status.batteryPercent}")
            appendLine("rssi=${status.rssiDbm}")
            appendLine("known=${status.knownServices} unknown=${status.unknownServices}")
            status.discoveryLogs.forEach { appendLine("${it.title}: ${it.detail}") }
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, dump)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(Intent.createChooser(send, "Deel GATT-dump").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        val cm = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("gatt", dump))
    }

    fun refreshSuggestedScene() {
        val time = ListeningScenes.suggestedNow(weekDoseMinutes())
        val weather = if (_weatherEnabled.value) WeatherAdvisor.suggest(app, time) else time
        _suggestedScene.value = weather
        _weatherLabel.value = WeatherAdvisor.lastLabel(app)
        _listeningMinutesToday.value = todayDoseMinutes()
        _listeningMinutesWeek.value = weekDoseMinutes()
    }

    fun applySuggestedNow() { applyListeningScene(_suggestedScene.value, fromAuto = true) }

    private fun applyPersistedToggles() {
        viewModel.dspManager.setMonoMix(_monoMix.value)
        if (_crossfeedEnabled.value) viewModel.setVirtualizer(420)
        if (_safeVolumeEnabled.value) setSafeVolume(true)
        if (_nightGuard.value && isNight()) setSafeVolume(true)
    }

    private fun restoreLastSceneIfNeeded() {
        ListeningScenes.byId(prefs.getString("last_scene_id", null))?.let { _activeSceneId.value = it.id }
    }

    private fun onHeadsetConnected() {
        detectHeadset()
        val bat = headsetMonitor.status.value.batteryPercent
        if (bat != null && bat <= 12 && !_sceneLocked.value && _autoSceneEnabled.value) {
            ListeningScenes.byId("saver")?.let { applyListeningScene(it, fromAuto = true) }
        }
    }

    private fun restoreSceneForHeadset(name: String) {
        if (_sceneLocked.value) return
        ListeningScenes.byId(prefs.getString("scene_for_$name", null))?.let { applyListeningScene(it, fromAuto = true) }
    }

    private fun onNowPlaying(track: NowPlayingTrack) {
        if (_adaptiveEq.value && !_eqLocked.value) {
            val hint = AdaptiveTrackEq.hint(track.genre, track.title, track.artist)
            if (hint.label != AdaptiveTrackEq.NONE.label) {
                _adaptiveEqLabel.value = hint.label
                prefs.edit().putString("adaptive_eq_label", hint.label).apply()
            }
        }
        if (!_autoSceneEnabled.value || _sceneLocked.value) return
        val scene = ListeningScenes.fromNowPlaying(track.packageName, track.genre, track.title) ?: return
        if (scene.id != _activeSceneId.value) applyListeningScene(scene, fromAuto = true)
    }

    private fun applyBatterySaver() {
        viewModel.setLoudness(0)
        viewModel.setVirtualizer(0)
        setCrossfeed(false)
        viewModel.setAncMode(com.example.dsp.AncMode.OFF)
    }

    private fun pushRecent(id: String) {
        val next = listOf(id) + _recentIds.value.filter { it != id }
        _recentIds.value = next.take(8)
        prefs.edit().putString("recent_scenes", _recentIds.value.joinToString(",")).apply()
    }

    private fun startDoseTicker() {
        doseJob?.cancel()
        doseJob = scope.launch {
            while (true) {
                delay(60_000)
                val key = "dose_" + todayKey()
                val next = prefs.getInt(key, 0) + 1
                prefs.edit().putInt(key, next).apply()
                _listeningMinutesToday.value = next
                _listeningMinutesWeek.value = weekDoseMinutes()
                if (next == 45) Toast.makeText(app, "45 min luisteren — even pauze?", Toast.LENGTH_LONG).show()
                if (next >= 180 && !_sceneLocked.value) {
                    ListeningScenes.byId("rest")?.let { applyListeningScene(it, fromAuto = true) }
                    setSafeVolume(true)
                }
                if (_nightGuard.value && isNight() && !_safeVolumeEnabled.value) setSafeVolume(true)
                refreshSuggestedScene()
            }
        }
    }

    private fun tickSleepLeft() {
        sleepTick?.let { handler.removeCallbacks(it) }
        val end = prefs.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L)
        if (end <= 0L) { _sleepTimerMinutes.value = 0; return }
        val left = ((end - System.currentTimeMillis()) / 60_000L).toInt().coerceAtLeast(0)
        _sleepTimerMinutes.value = left
        if (left <= 0) return
        val r = Runnable { tickSleepLeft() }
        sleepTick = r
        handler.postDelayed(r, 30_000)
    }

    private fun loadIdSet(key: String): Set<String> =
        prefs.getString(key, "")!!.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    private fun loadIdList(key: String): List<String> =
        prefs.getString(key, "")!!.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    private fun todayDoseMinutes(): Int = prefs.getInt("dose_" + todayKey(), 0)
    private fun weekDoseMinutes(): Int {
        val cal = Calendar.getInstance()
        var sum = 0
        repeat(7) { sum += prefs.getInt("dose_" + dayKey(cal), 0); cal.add(Calendar.DAY_OF_YEAR, -1) }
        return sum
    }
    private fun todayKey(): String = dayKey(Calendar.getInstance())
    private fun dayKey(cal: Calendar): String =
        "%04d%02d%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    private fun isNight(): Boolean {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return h >= 22 || h < 7
    }
}
