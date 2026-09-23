package com.example.data

import android.content.Context
import android.media.AudioManager

/** Onthoudt mediavolume per luister-scene. */
object SceneVolumeMemory {
    private const val PREFS = "sounmax_scene_vol"
    private const val ENABLED = "enabled"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(ENABLED, on).apply()
    }

    fun save(context: Context, sceneId: String?) {
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val vol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putInt("v_$id", vol).apply()
    }

    fun restore(context: Context, sceneId: String?): Boolean {
        if (!enabled(context)) return false
        val id = sceneId?.takeIf { it.isNotBlank() } ?: return false
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains("v_$id")) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val vol = prefs.getInt("v_$id", am.getStreamVolume(AudioManager.STREAM_MUSIC))
            .coerceIn(0, max)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0)
        return true
    }
}
