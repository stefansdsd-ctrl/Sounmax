package com.example.ui

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.dsp.GenreEqStrength
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Vol-scene + headset-scene toggles, los van SceneController. */
class SceneGesturePrefs(private val app: Application) {
    private val prefs = app.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)

    private val _volumeScene = MutableStateFlow(prefs.getBoolean("volume_scene", true))
    val volumeScene: StateFlow<Boolean> = _volumeScene.asStateFlow()

    private val _mediaScene = MutableStateFlow(prefs.getBoolean("media_scene", true))
    val mediaScene: StateFlow<Boolean> = _mediaScene.asStateFlow()

    private val _autoGenreEq = MutableStateFlow(prefs.getBoolean("auto_genre_eq", false))
    val autoGenreEq: StateFlow<Boolean> = _autoGenreEq.asStateFlow()

    private val _genreEqPercent = MutableStateFlow(prefs.getInt("genre_eq_percent", 100).coerceIn(25, 100))
    val genreEqPercent: StateFlow<Int> = _genreEqPercent.asStateFlow()

    private val _noiseSuggest = MutableStateFlow(prefs.getBoolean("scene_noise_suggest", true))
    val noiseSuggest: StateFlow<Boolean> = _noiseSuggest.asStateFlow()

    init {
        GenreEqStrength.setPercent(_genreEqPercent.value)
    }

    fun setVolumeScene(enabled: Boolean) {
        _volumeScene.value = enabled
        prefs.edit().putBoolean("volume_scene", enabled).apply()
        Toast.makeText(
            app,
            if (enabled) "Dubbel volume-omhoog wisselt scene" else "Vol-scene uit",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setMediaScene(enabled: Boolean) {
        _mediaScene.value = enabled
        prefs.edit().putBoolean("media_scene", enabled).apply()
        Toast.makeText(
            app,
            if (enabled) "Dubbel play/pause op headset wisselt scene" else "Headset-scene uit",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setAutoGenreEq(enabled: Boolean) {
        _autoGenreEq.value = enabled
        prefs.edit().putBoolean("auto_genre_eq", enabled).apply()
        Toast.makeText(
            app,
            if (enabled) "Genre-EQ volgt elk nieuw nummer" else "Auto genre-EQ uit",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setGenreEqPercent(percent: Int) {
        val p = percent.coerceIn(25, 100)
        _genreEqPercent.value = p
        GenreEqStrength.setPercent(p)
        prefs.edit().putInt("genre_eq_percent", p).apply()
    }

    fun setNoiseSuggest(enabled: Boolean) {
        _noiseSuggest.value = enabled
        prefs.edit().putBoolean("scene_noise_suggest", enabled).apply()
        Toast.makeText(
            app,
            if (enabled) "Ruis-suggestie aan" else "Ruis-suggestie uit",
            Toast.LENGTH_SHORT
        ).show()
    }
}
