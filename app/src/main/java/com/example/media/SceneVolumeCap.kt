package com.example.media

import android.content.Context
import android.media.AudioManager
import com.example.dsp.ListeningScene

/**
 * Beperkt STREAM_MUSIC tot 60% als de scene safeVolume heeft.
 * Onthoudt vorig volume om terug te zetten bij een scene zonder cap.
 */
object SceneVolumeCap {
    const val KEY_ENABLED = "scene_volume_cap"
    private const val KEY_SAVED = "scene_volume_cap_saved"
    private const val CAP_FRACTION = 0.60f

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun apply(context: Context, scene: ListeningScene) {
        if (!enabled(context)) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max <= 0) return
        val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (scene.safeVolume) {
            if (prefs.getInt(KEY_SAVED, -1) < 0) {
                prefs.edit().putInt(KEY_SAVED, current).apply()
            }
            val cap = (max * CAP_FRACTION).toInt().coerceAtLeast(1)
            if (current > cap) {
                try { am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0) } catch (_: Exception) {}
            }
        } else {
            val saved = prefs.getInt(KEY_SAVED, -1)
            if (saved >= 0) {
                try { am.setStreamVolume(AudioManager.STREAM_MUSIC, saved.coerceIn(0, max), 0) } catch (_: Exception) {}
                prefs.edit().remove(KEY_SAVED).apply()
            }
        }
    }
}
