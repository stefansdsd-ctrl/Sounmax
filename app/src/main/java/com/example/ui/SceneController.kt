package com.example.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes
import com.example.dsp.SceneGroups
import com.example.dsp.SceneLookup
import com.example.media.BatteryPowerAdvisor
import com.example.media.CallTransparencyGuard
import com.example.media.FocusSession
import com.example.media.HeadsetStatus
import com.example.media.HeadsetStatusMonitor
import com.example.media.RecentScenes
import com.example.media.SceneAutomation
import com.example.media.SceneReason
import com.example.media.SceneScheduleAdvisor
import com.example.media.SleepFade
import com.example.media.WeatherAdvisor
import com.example.widget.SoundMaxWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val _sceneReason = MutableStateFlow(SceneReason.read(prefs))
    val sceneReason: StateFlow<String> = _sceneReason.asStateFlow()
    private val _scheduleLabel = MutableStateFlow(SceneScheduleAdvisor.label(prefs))
    val scheduleLabel: StateFlow<String> = _scheduleLabel.asStateFlow()
    private val _focusActive = MutableStateFlow(FocusSession.isActive(app))
    val focusActive: StateFlow<Boolean> = _focusActive.asStateFlow()
    val suggestedScene: StateFlow<ListeningScene> = MutableStateFlow(currentSuggested())
    val listeningMinutesToday: StateFlow<Int> = MutableStateFlow(doseToday())
    val listeningMinutesWeek: StateFlow<Int> = MutableStateFlow(doseWeek())
    val doseWarning: StateFlow<String?> = MutableStateFlow(null)

    fun pinScheduleSlot() {
        val id = _activeSceneId.value ?: return
        val msg = SceneScheduleAdvisor.pinCurrent(prefs, id)
        _scheduleLabel.value = SceneScheduleAdvisor.label(prefs)
        Toast.makeText(app, msg, Toast.LENGTH_SHORT).show()
    }

    fun cycleScheduleHours() {
        val msg = SceneScheduleAdvisor.cycleHours(prefs)
        _scheduleLabel.value = msg
        Toast.makeText(app, "Uren: $msg", Toast.LENGTH_SHORT).show()
    }

    fun startFocusSession() {
        FocusSession.toggle(app)
        _focusActive.value = FocusSession.isActive(app)
        _locked.value = prefs.getBoolean("scene_locked", false)
        _activeSceneId.value = prefs.getString("last_scene_id", _activeSceneId.value)
    }

    fun applyListeningScene(scene: ListeningScene) {
        _activeSceneId.value = scene.id
        prefs.edit().putString("last_scene_id", scene.id).apply()
        viewModel.applyListeningScene(scene)
        SoundMaxWidget.refresh(app)
    }

    fun applySuggestedScene() {
        applyListeningScene(currentSuggested())
    }

    fun undoLastScene() {
        val prev = prefs.getString("prev_scene_id", null) ?: return
        SceneLookup.byId(prev)?.let { applyListeningScene(it) }
    }

    fun setAutoSceneEnabled(on: Boolean) {
        prefs.edit().putBoolean("auto_scene", on).apply()
        _autoScene.value = on
    }

    fun setSceneLocked(on: Boolean) {
        prefs.edit().putBoolean("scene_locked", on).apply()
        _locked.value = on
    }

    fun setSafeVolume(on: Boolean) {
        prefs.edit().putBoolean("safe_volume", on).apply()
        _safeVolume.value = on
    }

    fun setCallTransparency(enabled: Boolean) {
        CallTransparencyGuard.setEnabled(app, enabled)
        _callTransparency.value = enabled
        if (enabled) CallTransparencyGuard.attach(app) else CallTransparencyGuard.detach(app)
    }

    fun setSceneGroup(group: String) {
        prefs.edit().putString("scene_group", group).apply()
        _sceneGroup.value = group
    }

    fun toggleFavoriteScene(id: String) {
        val next = favoriteIds().toMutableSet()
        if (id in next) next.remove(id) else next.add(id)
        prefs.edit().putString("fav_scenes", next.joinToString(",")).apply()
        _favoriteSceneIds.value = next
    }

    fun startSleepTimer(mins: Int) {
        SleepFade.start(app, mins)
        _sleepLeft.value = mins
    }

    fun cancelSleepTimer() {
        SleepFade.cancel(app)
        _sleepLeft.value = 0
    }

    fun applyEarBreak() {
        SceneLookup.byId("rust")?.let { applyListeningScene(it) }
    }

    fun swapAbScene() {
        val a = prefs.getString("ab_a", _activeSceneId.value)
        val b = prefs.getString("ab_b", "focus")
        val next = if (_activeSceneId.value == a) b else a
        SceneLookup.byId(next ?: return)?.let { applyListeningScene(it) }
    }

    fun shareCurrentScene() {
        val id = _activeSceneId.value ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Sounmax scene: $id")
        }
        app.startActivity(Intent.createChooser(intent, "Deel scene").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun shareGattDump() {
        Toast.makeText(app, "GATT-dump: koppel headset en deel via issue", Toast.LENGTH_SHORT).show()
    }

    fun filteredScenes(query: String, group: String): List<ListeningScene> {
        val all = SceneGroups.allScenes()
        val q = query.trim().lowercase()
        return all.filter {
            val inGroup = group == "Alles" || it.id in SceneGroups.idsFor(group)
            val match = q.isBlank() || it.name.lowercase().contains(q) || it.id.contains(q)
            inGroup && match
        }
    }

    fun recentScenes(): List<ListeningScene> = RecentScenes.load(prefs).mapNotNull { SceneLookup.byId(it) }

    private fun favoriteIds(): Set<String> =
        prefs.getString("fav_scenes", "")?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()

    private fun remainingSleep(): Int = SleepFade.remainingMinutes(app)
    private fun doseToday(): Int = prefs.getInt("dose_today", 0)
    private fun doseWeek(): Int = prefs.getInt("dose_week", 0)
    private fun currentSuggested(): ListeningScene =
        SceneLookup.byId(prefs.getString("suggested_scene", "focus")) ?: ListeningScenes.defaultScene()
}
