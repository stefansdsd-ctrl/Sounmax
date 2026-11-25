package com.example.data

import android.content.Context
import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.pow

/**
 * Hearing protection: cumulative noise dose (inspired by ISO 1999 / NIOSH).
 * Tracks estimated A-weighted exposure while headphones are connected.
 * Warns at 80% / 100% of daily dose. Resets at midnight.
 */
object NoiseDoseTracker {
    private const val TAG = "NoiseDose"
    private const val PREFS = "soundmax_dose"
    private const val KEY_DOSE = "daily_dose"
    private const val KEY_DAY = "dose_day"

    // NIOSH-like: 85 dB for 8h = 100% dose. 3 dB exchange rate.
    private const val REF_DB = 85.0
    private const val REF_HOURS = 8.0

    private val _dose = MutableStateFlow(0f)
    val dose: StateFlow<Float> = _dose.asStateFlow()

    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    fun update(context: Context, estimatedDb: Float, deltaSeconds: Float) {
        if (deltaSeconds <= 0f || estimatedDb < 60f) return
        val today = todayKey()
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getString(KEY_DAY, "") != today) {
            prefs.edit().putString(KEY_DAY, today).putFloat(KEY_DOSE, 0f).apply()
            _dose.value = 0f
        }
        val current = prefs.getFloat(KEY_DOSE, 0f)
        val hours = deltaSeconds / 3600f
        val relative = (hours / REF_HOURS) * 10.0.pow((estimatedDb - REF_DB) / 3.0)
        val newDose = (current + relative.toFloat()).coerceAtMost(2f)
        prefs.edit().putFloat(KEY_DOSE, newDose).apply()
        _dose.value = newDose

        _warning.value = when {
            newDose >= 1.0f -> "Dagelijkse dosis bereikt – volume verlagen"
            newDose >= 0.8f -> "80% dosis – let op volume"
            else -> null
        }
        Log.d(TAG, "dose=$newDose db=$estimatedDb")
    }

    fun estimatedDb(context: Context): Float {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val vol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        // Rough mapping: 0-100% → ~50-95 dB (headphones closed-back)
        return 50f + (vol.toFloat() / max) * 45f
    }

    fun reset(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        _dose.value = 0f
        _warning.value = null
    }

    private fun todayKey(): String {
        val c = java.util.Calendar.getInstance()
        return "${c.get(java.util.Calendar.YEAR)}-${c.get(java.util.Calendar.DAY_OF_YEAR)}"
    }
}
