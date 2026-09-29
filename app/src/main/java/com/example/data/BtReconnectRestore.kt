package com.example.data

import android.content.Context
import android.media.AudioManager

/**
 * Onthoudt scene + volume bij BT-disconnect; zet terug bij reconnect (max 2 uur).
 */
object BtReconnectRestore {
    private const val PREFS = "sounmax_bt_restore"
    private const val KEY_ON = "enabled"
    private const val KEY_SCENE = "scene_id"
    private const val KEY_VOL = "vol"
    private const val KEY_AT = "saved_at"
    private const val TTL_MS = 2 * 60 * 60 * 1000L

    fun enabled(context: Context) = prefs(context).getBoolean(KEY_ON, true)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ON, on).apply()
    }

    fun toggle(context: Context): Boolean {
        val next = !enabled(context)
        setEnabled(context, next)
        return next
    }

    fun onDisconnect(context: Context) {
        if (!enabled(context)) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val scene = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .getString("last_scene_id", null)
        prefs(context).edit()
            .putString(KEY_SCENE, scene)
            .putInt(KEY_VOL, am.getStreamVolume(AudioManager.STREAM_MUSIC))
            .putLong(KEY_AT, System.currentTimeMillis())
            .apply()
    }

    fun onReconnect(context: Context): Boolean {
        if (!enabled(context)) return false
        val at = prefs(context).getLong(KEY_AT, 0L)
        if (at <= 0L || System.currentTimeMillis() - at > TTL_MS) return false
        val vol = prefs(context).getInt(KEY_VOL, -1)
        val scene = prefs(context).getString(KEY_SCENE, null)
        if (vol >= 0) {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, vol.coerceIn(0, max), 0)
        }
        if (!scene.isNullOrBlank()) {
            context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                .edit()
                .putString("last_scene_id", scene)
                .putBoolean("pending_widget_scene", true)
                .apply()
        }
        prefs(context).edit().putLong(KEY_AT, 0L).apply()
        return true
    }

    fun label(context: Context): String =
        if (enabled(context)) "BT-herstel aan" else "BT-herstel uit"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
