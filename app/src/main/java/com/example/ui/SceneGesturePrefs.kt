package com.example.ui

import android.app.Application
import android.content.Context
import android.widget.Toast
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
}
