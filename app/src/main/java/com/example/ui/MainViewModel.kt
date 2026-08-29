package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAcousticRecommendation
import com.example.ai.GeminiAudioTuner
import com.example.data.EqPresetEntity
import com.example.data.HeadsetMemory
import com.example.data.HearingProfileEntity
import com.example.data.SoundMaxDatabase
import com.example.dsp.AncMode
import com.example.dsp.AudioDspManager
import com.example.dsp.BluetoothCodec
import com.example.dsp.EqPreset
import com.example.dsp.HeadphoneDevice
import com.example.media.DspControlService
import com.example.media.FindHeadsetHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AiTunerState {
    data object Idle : AiTunerState
    data object Loading : AiTunerState
    data class Success(val recommendation: AiAcousticRecommendation) : AiTunerState
    data class Error(val message: String) : AiTunerState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val dspManager = AudioDspManager(application.applicationContext)
    private val database = SoundMaxDatabase.getDatabase(application.applicationContext)
    private val eqPresetDao = database.eqPresetDao()
    private val hearingDao = database.hearingProfileDao()
    private val geminiTuner = GeminiAudioTuner()
    private val findHeadsetHelper = FindHeadsetHelper()
    val headsetMemory = HeadsetMemory(application.applicationContext)

    val customDbPresets: StateFlow<List<EqPresetEntity>> = eqPresetDao.getAllPresets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritePresets: StateFlow<List<EqPresetEntity>> = eqPresetDao.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentPresets: StateFlow<List<EqPresetEntity>> = eqPresetDao.getRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestHearingProfile: StateFlow<HearingProfileEntity?> = hearingDao.getLatestProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentWebUrl = MutableStateFlow("https://music.youtube.com")
    val currentWebUrl: StateFlow<String> = _currentWebUrl.asStateFlow()

    private val _isWebPlayerExpanded = MutableStateFlow(false)
    val isWebPlayerExpanded: StateFlow<Boolean> = _isWebPlayerExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _aiTunerState = MutableStateFlow<AiTunerState>(AiTunerState.Idle)
    val aiTunerState: StateFlow<AiTunerState> = _aiTunerState.asStateFlow()

    private val _hearingTestStep = MutableStateFlow(0)
    val hearingTestStep: StateFlow<Int> = _hearingTestStep.asStateFlow()

    private val _hearingTestActive = MutableStateFlow(false)
    val hearingTestActive: StateFlow<Boolean> = _hearingTestActive.asStateFlow()

    private val _leftEarLossMap = MutableStateFlow(mutableMapOf<Int, Int>())
    val leftEarLossMap: StateFlow<Map<Int, Int>> = _leftEarLossMap.asStateFlow()

    private val _rightEarLossMap = MutableStateFlow(mutableMapOf<Int, Int>())
    val rightEarLossMap: StateFlow<Map<Int, Int>> = _rightEarLossMap.asStateFlow()

    private val testFrequencies = listOf(125, 250, 500, 1000, 2000, 4000, 8000)

    val currentTestFreq: Int
        get() {
            val step = _hearingTestStep.value
            return if (step < 7) testFrequencies[step] else testFrequencies[step - 7]
        }

    val isTestingLeftEar: Boolean
        get() = _hearingTestStep.value < 7

    private var slotA: EqPreset? = null
    private var slotB: EqPreset? = null
    private val _abActive = MutableStateFlow("A")
    val abActive: StateFlow<String> = _abActive.asStateFlow()

    fun setDspEnabled(enabled: Boolean) {
        dspManager.setDspEnabled(enabled)
        val app = getApplication<Application>()
        app.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(DspControlService.KEY_DSP, enabled).apply()
        DspControlService.start(app)
    }

    fun applyPreset(preset: EqPreset) {
        dspManager.applyPreset(preset)
        val headset = dspManager.connectedHeadsetName.value ?: dspManager.activeHeadphone.value.name
        headsetMemory.save(headset, preset, dspManager.bandGains.value)
        Toast.makeText(getApplication(), "Profiel geactiveerd: ${preset.name}", Toast.LENGTH_SHORT).show()
    }

    fun restoreHeadsetMemory(headsetName: String?): Boolean {
        val stored = headsetMemory.load(headsetName) ?: return false
        dspManager.applyPreset(stored)
        return true
    }

    fun setRememberPerHeadset(enabled: Boolean) {
        headsetMemory.enabled = enabled
        Toast.makeText(
            getApplication(),
            if (enabled) "EQ wordt per headset onthouden" else "Headset-geheugen uit",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun updateBandGain(index: Int, gainDb: Float) {
        dspManager.updateBandGain(index, gainDb)
    }

    fun setBassBoost(strength: Int) {
        dspManager.setBassBoost(strength)
    }

    fun setVirtualizer(strength: Int) {
        dspManager.setVirtualizer(strength)
    }

    fun setLoudness(gain: Int) {
        dspManager.setLoudness(gain)
    }

    fun setClarity(clarity: Float) {
        dspManager.setClarity(clarity)
    }

    fun setAncMode(mode: AncMode) {
        dspManager.setAncMode(mode)
    }

    fun selectHeadphone(device: HeadphoneDevice) {
        dspManager.selectHeadphone(device)
        restoreHeadsetMemory(device.name)
    }

    fun setCodec(codec: BluetoothCodec) {
        dspManager.setCodec(codec)
    }

    fun setLatencySync(offsetMs: Int) {
        dspManager.setLatencySync(offsetMs)
    }

    fun setBalance(balance: Int) {
        dspManager.setBalance(balance)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playInWebPlayer(url: String) {
        _currentWebUrl.value = url
        _isWebPlayerExpanded.value = true
    }

    fun toggleWebPlayerExpanded() {
        _isWebPlayerExpanded.value = !_isWebPlayerExpanded.value
    }

    fun launchYouTubeMusicNative(context: Context, queryOrUrl: String? = null) {
        val targetUrl = if (queryOrUrl.isNullOrBlank()) {
            "https://music.youtube.com"
        } else if (queryOrUrl.startsWith("http")) {
            queryOrUrl
        } else {
            "https://music.youtube.com/search?q=${Uri.encode(queryOrUrl)}"
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                setPackage("com.google.android.apps.youtube.music")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                Toast.makeText(context, "Kan YouTube Music niet openen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openBluetoothSettings(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Bluetooth instellingen niet beschikbaar", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDeveloperOptions(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(context, "Instellingen niet beschikbaar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun forceLdacCodec(mode: com.example.dsp.LdacQualityMode = com.example.dsp.LdacQualityMode.QUALITY_990) {
        dspManager.forceLdacCodec(mode)
        Toast.makeText(getApplication(), "LDAC geforceerd op ${mode.modeName}", Toast.LENGTH_SHORT).show()
    }

    fun optimizeLdacStreaming() {
        dspManager.optimizeLdacStreaming()
        Toast.makeText(getApplication(), "LDAC & A2DP buffer geoptimaliseerd", Toast.LENGTH_SHORT).show()
    }

    fun findHeadset() {
        findHeadsetHelper.start()
        Toast.makeText(getApplication(), "Zoeken: L/R pieptonen 12s", Toast.LENGTH_SHORT).show()
    }

    fun stopFindHeadset() {
        findHeadsetHelper.stop()
    }

    fun saveCurrentAsCustomPreset(name: String, category: String = "Aangepast") {
        if (name.isBlank()) return
        viewModelScope.launch {
            val gains = dspManager.bandGains.value.joinToString(",")
            val entity = EqPresetEntity(
                name = name.trim(),
                isCustom = true,
                category = category.trim().ifBlank { "Aangepast" },
                bandGains = gains,
                bassBoost = dspManager.bassBoostStrength.value,
                virtualizer = dspManager.virtualizerStrength.value,
                loudness = dspManager.loudnessGain.value,
                clarity = dspManager.clarityGain.value,
                lastUsedAt = System.currentTimeMillis()
            )
            val insertedId = eqPresetDao.insertPreset(entity)
            val gainsList = dspManager.bandGains.value
            val savedPreset = EqPreset(
                id = insertedId,
                name = name.trim(),
                bandGains = gainsList,
                bassBoost = dspManager.bassBoostStrength.value,
                virtualizer = dspManager.virtualizerStrength.value,
                loudness = dspManager.loudnessGain.value,
                clarity = dspManager.clarityGain.value,
                isCustom = true,
                category = category,
                description = "Opgeslagen aangepast equalizer profiel."
            )
            dspManager.applyPreset(savedPreset)
            val headset = dspManager.connectedHeadsetName.value ?: dspManager.activeHeadphone.value.name
            headsetMemory.save(headset, savedPreset, gainsList)
            Toast.makeText(getApplication(), "Opgeslagen als '$name'", Toast.LENGTH_SHORT).show()
        }
    }

    fun applyCustomDbPreset(dbPreset: EqPresetEntity) {
        viewModelScope.launch {
            eqPresetDao.touchUsed(dbPreset.id, System.currentTimeMillis())
        }
        val gainsList = dbPreset.bandGains.split(",").mapNotNull { it.toFloatOrNull() }
        val eqPreset = EqPreset(
            id = dbPreset.id,
            name = dbPreset.name,
            bandGains = if (gainsList.size == 10) gainsList else List(10) { 0f },
            bassBoost = dbPreset.bassBoost,
            virtualizer = dbPreset.virtualizer,
            loudness = dbPreset.loudness,
            clarity = dbPreset.clarity,
            isCustom = true,
            category = dbPreset.category,
            description = "Opgeslagen gebruikersprofiel"
        )
        applyPreset(eqPreset)
    }

    fun toggleFavorite(id: Long, favorite: Boolean) {
        viewModelScope.launch { eqPresetDao.setFavorite(id, favorite) }
    }

    fun exportCurrentEq(context: Context) {
        val name = dspManager.currentPreset.value?.name ?: "Sounmax EQ"
        val bands = dspManager.bandGains.value.joinToString(",")
        val json = """{"app":"sounmax","name":"$name","bands":[$bands],"bass":${dspManager.bassBoostStrength.value},"virt":${dspManager.virtualizerStrength.value},"loud":${dspManager.loudnessGain.value},"clarity":${dspManager.clarityGain.value}}"""
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("sounmax-eq", json))
        Toast.makeText(context, "EQ-JSON gekopieerd", Toast.LENGTH_SHORT).show()
    }

    fun importEqJson(raw: String) {
        val bandsPart = raw.substringAfter("\"bands\":[").substringBefore("]")
        val gains = bandsPart.split(",").mapNotNull { it.trim().toFloatOrNull() }
        if (gains.size != 10) {
            Toast.makeText(getApplication(), "Ongeldige EQ-JSON", Toast.LENGTH_SHORT).show()
            return
        }
        val name = raw.substringAfter("\"name\":").substringAfter('"').substringBefore('"')
        applyPreset(
            EqPreset(
                name = name.ifBlank { "Geïmporteerd" },
                bandGains = gains,
                isCustom = true,
                description = "Geïmporteerd via JSON"
            )
        )
    }

    fun stepBandGain(bandIndex: Int, deltaDb: Float) {
        val current = dspManager.bandGains.value.getOrElse(bandIndex) { 0f }
        val newGain = (current + deltaDb).coerceIn(-12f, 12f)
        dspManager.updateBandGain(bandIndex, newGain)
    }

    fun snapshotAb(slot: String) {
        val snap = EqPreset(
            name = "Slot $slot",
            bandGains = dspManager.bandGains.value,
            bassBoost = dspManager.bassBoostStrength.value,
            virtualizer = dspManager.virtualizerStrength.value,
            loudness = dspManager.loudnessGain.value,
            clarity = dspManager.clarityGain.value,
            isCustom = true,
            description = "A/B vergelijking"
        )
        if (slot == "B") slotB = snap else slotA = snap
        _abActive.value = slot
        Toast.makeText(getApplication(), "EQ opgeslagen in $slot", Toast.LENGTH_SHORT).show()
    }

    fun toggleAb() {
        val next = if (_abActive.value == "A") "B" else "A"
        val preset = if (next == "B") slotB else slotA
        if (preset == null) {
            snapshotAb(next)
            return
        }
        dspManager.applyPreset(preset)
        _abActive.value = next
        Toast.makeText(getApplication(), "EQ $next actief", Toast.LENGTH_SHORT).show()
    }

    fun resetBandsToFlat() {
        for (i in 0 until 10) {
            dspManager.updateBandGain(i, 0f)
        }
        val flatPreset = EqPreset(
            id = 999,
            name = "Vlak (Flat 0 dB)",
            bandGains = List(10) { 0f },
            bassBoost = 0,
            virtualizer = 0,
            loudness = 0,
            clarity = 0f,
            isCustom = false,
            description = "Volledig vlakke neutrale curve."
        )
        dspManager.applyPreset(flatPreset)
        Toast.makeText(getApplication(), "Equalizer gereset naar Vlak (0 dB)", Toast.LENGTH_SHORT).show()
    }

    fun deleteCustomPreset(id: Long) {
        viewModelScope.launch {
            eqPresetDao.deleteById(id)
        }
    }

    fun askAiTuner(prompt: String) {
        if (prompt.isBlank()) return
        _aiTunerState.value = AiTunerState.Loading
        viewModelScope.launch {
            val result = geminiTuner.generateAcousticProfile(
                userPrompt = prompt,
                headphoneModel = dspManager.activeHeadphone.value.name,
                musicGenre = "YouTube Music Audio Stream"
            )
            result.onSuccess { rec ->
                _aiTunerState.value = AiTunerState.Success(rec)
            }.onFailure { err ->
                _aiTunerState.value = AiTunerState.Error(err.message ?: "Onbekende akoestische fout")
            }
        }
    }

    fun resetAiTuner() {
        _aiTunerState.value = AiTunerState.Idle
    }

    fun startHearingTest() {
        _hearingTestStep.value = 0
        _leftEarLossMap.value = mutableMapOf()
        _rightEarLossMap.value = mutableMapOf()
        _hearingTestActive.value = true
        playCurrentStepTone(30f)
    }

    fun cancelHearingTest() {
        _hearingTestActive.value = false
        dspManager.stopTestTone()
    }

    fun playCurrentStepTone(volumePercent: Float) {
        val freq = currentTestFreq
        val isLeft = isTestingLeftEar
        dspManager.playTestTone(freq, volumePercent, isLeft)
    }

    fun recordHearingResponse(canHearLevelDb: Int) {
        dspManager.stopTestTone()
        val freq = currentTestFreq
        val step = _hearingTestStep.value
        if (step < 7) {
            val updated = _leftEarLossMap.value.toMutableMap()
            updated[freq] = canHearLevelDb
            _leftEarLossMap.value = updated
        } else {
            val updated = _rightEarLossMap.value.toMutableMap()
            updated[freq] = canHearLevelDb
            _rightEarLossMap.value = updated
        }
        if (step < 13) {
            _hearingTestStep.value = step + 1
            playCurrentStepTone(30f)
        } else {
            _hearingTestActive.value = false
            calculateAndSaveAudiogram()
        }
    }

    private fun computeHearingScore(left: Map<Int, Int>, right: Map<Int, Int>): Int {
        val values = testFrequencies.flatMap { f ->
            listOf(left[f] ?: 25, right[f] ?: 25)
        }
        val penalty = values.map { (it - 20).coerceAtLeast(0) }.average()
        return (100.0 - penalty * 1.6).toInt().coerceIn(40, 100)
    }

    private fun calculateAndSaveAudiogram() {
        viewModelScope.launch {
            val left = _leftEarLossMap.value
            val right = _rightEarLossMap.value
            val leftGainsStr = testFrequencies.map { freq ->
                val threshold = left[freq] ?: 25
                ((threshold - 20) * 0.18f).coerceIn(-2.0f, 6.0f)
            }.joinToString(",")
            val rightGainsStr = testFrequencies.map { freq ->
                val threshold = right[freq] ?: 25
                ((threshold - 20) * 0.18f).coerceIn(-2.0f, 6.0f)
            }.joinToString(",")
            val score = computeHearingScore(left, right)
            val entity = HearingProfileEntity(
                profileName = "Gepersonaliseerd Gehoorprofiel ${dspManager.activeHeadphone.value.brand}",
                leftGains = leftGainsStr,
                rightGains = rightGainsStr,
                scorePercent = score
            )
            hearingDao.insertProfile(entity)
            Toast.makeText(getApplication(), "Gehoortest voltooid · score $score%", Toast.LENGTH_LONG).show()
        }
    }

    fun applyHearingCorrection(profile: HearingProfileEntity) {
        val leftGains = profile.leftGains.split(",").mapNotNull { it.toFloatOrNull() }
        val rightGains = profile.rightGains.split(",").mapNotNull { it.toFloatOrNull() }
        fun avg(i: Int): Float {
            val l = leftGains.getOrElse(i) { 0f }
            val r = rightGains.getOrElse(i) { l }
            return ((l + r) / 2f).coerceIn(-6f, 8f)
        }
        val tenBands = listOf(
            avg(0), avg(0), avg(1), avg(2), avg(3),
            avg(4), avg(5), avg(5), avg(6), avg(6)
        )
        val imbalance = run {
            val l = leftGains.average().takeIf { !it.isNaN() } ?: 0.0
            val r = rightGains.average().takeIf { !it.isNaN() } ?: 0.0
            ((r - l) * 8).toInt().coerceIn(-20, 20)
        }
        if (imbalance != 0) dspManager.setBalance(50 + imbalance)
        val customEq = EqPreset(
            name = "Gepersonaliseerde Gehoorcompensatie",
            bandGains = tenBands,
            bassBoost = 250,
            virtualizer = 200,
            loudness = 200,
            clarity = 4.5f,
            isCustom = true,
            description = "L+R gemiddelde uit gehoortest, met lichte balanscorrectie."
        )
        applyPreset(customEq)
    }

    override fun onCleared() {
        super.onCleared()
        findHeadsetHelper.stop()
        dspManager.release()
    }
}
