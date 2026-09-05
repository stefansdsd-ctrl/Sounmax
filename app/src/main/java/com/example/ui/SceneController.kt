package com.example.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dsp.BuiltinPresets
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.example.dsp.SceneGroups
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc
import com.example.media.BatteryPowerAdvisor
import com.example.media.CallTransparencyGuard
import com.example.media.HeadsetStatus
import com.example.media.HeadsetStatusMonitor
import com.example.media.SceneAutomation
import com.example.media.SleepFade
import com.example.media.WeatherAdvisor
import com.example.widget.SoundMaxWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class SceneController(private val viewModel: MainViewModel) {
    private val app = viewModel.getApplication<android.app.Application>()
    private val prefs = app.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
    private val monitor = HeadsetStatusMonitor(app)

    val headsetStatus: StateFlow<HeadsetStatus> = monitor.status

    private val _activeSceneId = MutableStateFlow(prefs.getString("last_scene_id", "focus"))
    val activeSceneId: StateFlow<String?> = _activeSceneId.asStateFlow()

    private val _safeVolume = MutableStateFlow(prefs.getBoolean("safe_volume", false))
    val safeVolumeEnabled: StateFlow<Boolean> = _safeVolume.asStateFlow()

    private val _autoScene = MutableStateFlow(prefs.getBoolean("auto_scene", true))
    val autoSceneEnabled: StateFlow<Boolean> = _autoScene.asStateFlow()

    private val _locked = MutableStateFlow(prefs.getBoolean("scene_locked", false))
    val sceneLocked: StateFlow<Boolean> = _locked.asStateFlow()

    private val _callTransparency = MutableStateFlow(CallTransparencyGuard.enabled(app))
    val callTransparency: StateFlow<Boolean> = _callTransparency.asStateFlow()

    private val _sceneGroup = MutableStateFlow(prefs.getString("scene_group", "Alles") ?: "Alles")
    val sceneGroup: StateFlow<String> = _sceneGroup.asStateFlow()

    private val _favoriteSceneIds = MutableStateFlow(favoriteIds())
    val favoriteSceneIds: StateFlow<Set<String>> = _favoriteSceneIds.asStateFlow()

    private val _sleepLeft = MutableStateFlow(remainingSleep())
    val sleepTimerMinutes: StateFlow<Int> = _sleepLeft.asStateFlow()

    val suggestedScene: StateFlow<ListeningScene> = MutableStateFlow(currentSuggested())
    val listeningMinutesToday: StateFlow<Int> = MutableStateFlow(doseToday())
    val listeningMinutesWeek: StateFlow<Int> = MutableStateFlow(doseWeek())
    val doseWarning: StateFlow<String?> = MutableStateFlow(
        when {
            doseWeek() >= 600 -> "Weekdosis hoog — rustscene aangeraden"
            doseToday() >= 180 -> "Vandaag al ${doseToday()} min — volume checken"
            else -> null
        }
    )

    init {
        try { monitor.refresh() } catch (_: Exception) {}
        SceneAutomation.consumePending(app) { applyListeningScene(it) }
    }

    fun filteredScenes(query: String, group: String): List<ListeningScene> {
        val q = query.trim().lowercase()
        val ids = SceneGroups.ids(group)
        val favs = favoriteIds()
        return SceneLookup.ALL.filter { scene ->
            val inGroup = when (group) {
                "Alles", "" -> true
                "Favorieten" -> scene.id in favs
                else -> ids.isNullOrEmpty() || scene.id in ids
            }
            if (!inGroup) return@filter false
            if (q.isBlank()) true
            else scene.name.lowercase().contains(q) ||
                scene.description.lowercase().contains(q) ||
                scene.id.contains(q) ||
                scene.emoji.contains(q) ||
                scene.presetName.lowercase().contains(q)
        }
    }

    fun recentScenes(): List<ListeningScene> {
        val ids = prefs.getString("recent_scenes", "")
            ?.split(',')
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        return ids.mapNotNull { SceneLookup.byId(it) }.distinctBy { it.id }.take(8)
    }

    fun setSceneGroup(label: String) {
        _sceneGroup.value = label
        prefs.edit().putString("scene_group", label).apply()
    }

    fun toggleFavoriteScene(id: String) {
        val next = favoriteIds().toMutableSet()
        val added = next.add(id)
        if (!added) next.remove(id)
        prefs.edit().putString("fav_scenes", next.joinToString(",")).apply()
        _favoriteSceneIds.value = next
        val name = SceneLookup.byId(id)?.name ?: id
        Toast.makeText(app, if (added) "★ $name" else "☆ $name", Toast.LENGTH_SHORT).show()
    }

    fun isFavoriteScene(id: String): Boolean = id in _favoriteSceneIds.value

    fun setAutoSceneEnabled(enabled: Boolean) {
        _autoScene.value = enabled
        prefs.edit().putBoolean("auto_scene", enabled).apply()
        Toast.makeText(app, if (enabled) "Auto-scene aan" else "Auto-scene uit", Toast.LENGTH_SHORT).show()
    }

    fun setSceneLocked(locked: Boolean) {
        _locked.value = locked
        prefs.edit().putBoolean("scene_locked", locked).apply()
        Toast.makeText(app, if (locked) "Scene vergrendeld" else "Scene vrij", Toast.LENGTH_SHORT).show()
    }

    fun setSafeVolume(enabled: Boolean) {
        _safeVolume.value = enabled
        prefs.edit().putBoolean("safe_volume", enabled).apply()
        if (enabled) viewModel.setLoudness((-6).coerceAtLeast(-15))
        Toast.makeText(app, if (enabled) "Veilig volume aan" else "Veilig volume uit", Toast.LENGTH_SHORT).show()
    }

    fun setCallTransparency(enabled: Boolean) {
        CallTransparencyGuard.setEnabled(app, enabled)
        _callTransparency.value = enabled
        if (enabled) CallTransparencyGuard.attach(app) else CallTransparencyGuard.detach(app)
        Toast.makeText(app, if (enabled) "Call-transparantie aan" else "Call-transparantie uit", Toast.LENGTH_SHORT).show()
    }

    fun applyListeningScene(scene: ListeningScene) {
        val previous = prefs.getString("last_scene_id", null)
        val battery = monitor.status.value.batteryPercent
        val adjusted = BatteryPowerAdvisor.adjust(app, scene, battery)
        val weather = WeatherAdvisor.suggest(app, adjusted)
        _activeSceneId.value = weather.id
        prefs.edit()
            .putString("last_scene_id", weather.id)
            .putString(
                "recent_scenes",
                (listOf(weather.id) + (prefs.getString("recent_scenes", "")?.split(',') ?: emptyList()))
                    .filter { it.isNotBlank() }
                    .distinct()
                    .take(12)
                    .joinToString(",")
            )
            .apply()
        BuiltinPresets.PRESETS.firstOrNull { it.name == weather.presetName }?.let { viewModel.dspManager.applyPreset(it) }
        viewModel.setAncMode(weather.ancMode)
        SoftwareAnc.apply(weather.ancMode)
        weather.preferredCodec?.let { viewModel.setCodec(it) }
        weather.preferredLdac?.let { viewModel.forceLdacCodec(it) }
        viewModel.dspManager.setMonoMix(weather.id == "oneear")
        if (weather.safeVolume) setSafeVolume(true)
        prefs.edit()
            .putString("ab_scene_id", previous)
            .putLong("session_started_at", System.currentTimeMillis())
            .apply()
        SoundMaxWidget.refreshAll(app)
        Toast.makeText(app, "${weather.emoji} ${weather.name}", Toast.LENGTH_SHORT).show()
    }

    fun swapAbScene() {
        val other = prefs.getString("ab_scene_id", null) ?: recentScenes().getOrNull(1)?.id
        val scene = SceneLookup.byId(other)
        if (scene == null) {
            Toast.makeText(app, "Nog geen A/B-scene", Toast.LENGTH_SHORT).show()
            return
        }
        applyListeningScene(scene)
    }

    fun shareCurrentScene() {
        val scene = SceneLookup.byId(_activeSceneId.value)
        val text = buildString {
            appendLine("Sounmax scene")
            appendLine("${scene?.emoji ?: ""} ${scene?.name ?: _activeSceneId.value}")
            appendLine(scene?.description.orEmpty())
            appendLine("preset=${scene?.presetName} anc=${scene?.ancMode?.displayName}")
            appendLine("veilig=${_safeVolume.value} slot=${_locked.value} auto=${_autoScene.value}")
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Sounmax scene")
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(Intent.createChooser(send, "Deel scene").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun applyEarBreak() {
        val rest = SceneLookup.byId("rest") ?: ListeningScenes.byId("rest") ?: return
        val wasLocked = _locked.value
        if (wasLocked) setSceneLocked(false)
        applyListeningScene(rest)
        setSafeVolume(true)
        prefs.edit().putLong("last_ear_break", System.currentTimeMillis()).apply()
        Toast.makeText(app, "Oorpauze — 5 min zachter", Toast.LENGTH_SHORT).show()
    }

    fun startSleepTimer(minutes: Int) {
        val end = System.currentTimeMillis() + minutes * 60_000L
        prefs.edit()
            .putLong(SoundMaxWidget.KEY_SLEEP_END, end)
            .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, minutes)
            .apply()
        SleepFade.schedule(app, end)
        _sleepLeft.value = minutes
        Toast.makeText(app, "Sleep-timer ${minutes}m", Toast.LENGTH_SHORT).show()
    }

    fun cancelSleepTimer() {
        SleepFade.cancel(app)
        _sleepLeft.value = 0
        Toast.makeText(app, "Sleep-timer uit", Toast.LENGTH_SHORT).show()
    }

    fun shareGattDump() {
        val status = monitor.status.value
        val text = buildString {
            appendLine("Sounmax GATT-dump")
            appendLine("headset=${status.name} accu=${status.batteryPercent}% rssi=${status.rssiDbm}")
            appendLine("known=${status.knownServices} unknown=${status.unknownServices}")
            status.discoveryLogs.forEach { appendLine("${it.title}: ${it.detail}") }
            appendLine()
            appendLine("Plak dit in GitHub issue #1 voor echte Philips ANC-UUIDs.")
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Sounmax GATT-dump")
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(Intent.createChooser(send, "Deel GATT-dump").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun remainingSleep(): Int {
        val end = prefs.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L)
        return SoundMaxWidget.remainingSleepMinutes(end)
    }

    private fun favoriteIds(): Set<String> =
        prefs.getString("fav_scenes", "")
            ?.split(',')
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()

    private fun doseToday(): Int {
        val cal = Calendar.getInstance()
        return prefs.getInt("dose_${cal.get(Calendar.YEAR)}_${cal.get(Calendar.DAY_OF_YEAR)}", 0)
    }

    private fun doseWeek(): Int {
        val cal = Calendar.getInstance()
        var total = 0
        repeat(7) {
            total += prefs.getInt("dose_${cal.get(Calendar.YEAR)}_${cal.get(Calendar.DAY_OF_YEAR)}", 0)
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return total
    }

    private fun currentSuggested(): ListeningScene {
        val base = ListeningScenes.suggestedNow(doseWeek())
        return WeatherAdvisor.suggest(app, base)
    }
}
