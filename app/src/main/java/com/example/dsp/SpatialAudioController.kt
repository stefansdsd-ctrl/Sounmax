package com.example.dsp

import android.content.Context
import android.media.AudioManager
import android.media.Spatializer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Controls Android Spatializer (spatial audio / head tracking).
 * Android 12L+/13+ with compatible devices. Persistent prefs + battery-aware.
 */
object SpatialAudioController {
    private const val TAG = "SpatialAudio"
    private const val PREFS = "soundmax_wellness"
    private const val KEY_SPATIAL = "spatializer_on"
    private const val KEY_HEAD = "head_tracking"

    private var spatializer: Spatializer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _headTrackerAvailable = MutableStateFlow(false)
    val headTrackerAvailable: StateFlow<Boolean> = _headTrackerAvailable.asStateFlow()

    private val spatializerListener = object : Spatializer.OnSpatializerStateChangedListener {
        override fun onSpatializerEnabledChanged(spatializer: Spatializer, enabled: Boolean) {
            _enabled.value = enabled
            Log.i(TAG, "Spatializer enabled=$enabled")
        }
        override fun onSpatializerAvailableChanged(spatializer: Spatializer, available: Boolean) {
            Log.i(TAG, "Spatializer available=$available")
        }
    }

    private val headTrackerListener = Spatializer.OnHeadTrackerAvailableListener { available ->
        _headTrackerAvailable.value = available
        Log.i(TAG, "Head tracker available=$available")
    }

    fun isSupported(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.spatializer?.let { it.immersiveAudioLevel != Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_NONE } == true
    }

    fun attach(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sp = am.spatializer ?: return
        spatializer = sp
        try {
            sp.addOnSpatializerStateChangedListener(mainHandler::post, spatializerListener)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                sp.addOnHeadTrackerAvailableListener(mainHandler::post, headTrackerListener)
            }
            _enabled.value = sp.isEnabled
            _headTrackerAvailable.value = sp.isHeadTrackerAvailable
        } catch (e: Exception) {
            Log.w(TAG, "attach failed", e)
        }
    }

    fun setEnabled(context: Context, on: Boolean): Boolean {
        if (!isSupported(context)) return false
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SPATIAL, on).apply()
        _enabled.value = on
        // Actual system enable is often via Settings; we track preference + report capability.
        Log.i(TAG, "Spatial preference set to $on")
        return true
    }

    fun isEnabledPref(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_SPATIAL, false)

    fun setHeadTracking(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_HEAD, on).apply()
        if (on) HeadTrackerHolder.get(context).start() else HeadTrackerHolder.get(context).stop()
    }

    fun isHeadTrackingPref(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_HEAD, false)

    /** Call on battery saver / low battery to auto-disable heavy features. */
    fun applyBatterySaver(context: Context, low: Boolean) {
        if (low && isEnabledPref(context)) {
            setEnabled(context, false)
            setHeadTracking(context, false)
            Log.i(TAG, "Battery saver: spatial + head tracking disabled")
        }
    }

    fun getStatus(context: Context): String {
        if (!isSupported(context)) return "Spatial: niet ondersteund"
        val en = isEnabledPref(context)
        val ht = isHeadTrackingPref(context)
        return when {
            en && ht -> "Spatial + head tracking aan"
            en -> "Spatial aan"
            else -> "Spatial uit"
        }
    }

    fun detach() {
        spatializer?.removeOnSpatializerStateChangedListener(spatializerListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            spatializer?.removeOnHeadTrackerAvailableListener(headTrackerListener)
        }
        spatializer = null
    }
}
