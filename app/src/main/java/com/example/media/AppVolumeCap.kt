package com.example.media

import android.content.Context
import android.media.AudioManager
import com.example.data.NowPlayingApp

/**
 * Per-app STREAM_MUSIC-plafond (0.40–1.00). 1.00 = geen cap.
 * Wordt toegepast bij now-playing wissel.
 */
object AppVolumeCap {
    private const val PREFS = "soundmax_wellness"
    private const val KEY_ON = "app_volume_cap"
    private const val KEY_PREFIX = "app_vol_frac_"
    private const val KEY_SAVED = "app_vol_saved"
    const val DEFAULT_FRACTION = 1.00f

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ON, on).apply()
        if (on) apply(context) else restore(context)
    }

    fun fraction(context: Context, pkg: String?): Float {
        if (pkg.isNullOrBlank()) return DEFAULT_FRACTION
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getFloat(KEY_PREFIX + pkg, DEFAULT_FRACTION)
            .coerceIn(0.40f, 1.00f)
    }

    fun setFraction(context: Context, pkg: String?, value: Float) {
        if (pkg.isNullOrBlank()) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_PREFIX + pkg, value.coerceIn(0.40f, 1.00f)).apply()
        apply(context)
    }

    fun cycleFraction(context: Context, pkg: String?): Float {
        val steps = floatArrayOf(0.50f, 0.60f, 0.75f, 0.90f, 1.00f)
        val cur = fraction(context, pkg)
        val next = steps.firstOrNull { it > cur + 0.01f } ?: steps.first()
        setFraction(context, pkg, next)
        return next
    }

    fun apply(context: Context) {
        if (!enabled(context)) return
        val pkg = NowPlayingApp.packageName ?: return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max <= 0) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val frac = fraction(context, pkg)
        val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (frac >= 0.99f) {
            restore(context)
            return
        }
        if (prefs.getInt(KEY_SAVED, -1) < 0) {
            prefs.edit().putInt(KEY_SAVED, current).apply()
        }
        val cap = (max * frac).toInt().coerceAtLeast(1)
        if (current > cap) {
            try { am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0) } catch (_: Exception) {}
        }
    }

    private fun restore(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getInt(KEY_SAVED, -1)
        if (saved < 0) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        try { am.setStreamVolume(AudioManager.STREAM_MUSIC, saved.coerceIn(0, max), 0) } catch (_: Exception) {}
        prefs.edit().remove(KEY_SAVED).apply()
    }
}
