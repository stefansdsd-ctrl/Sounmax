package com.example.dsp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class HeadTracker(
    context: Context,
    private val onPose: (yawDeg: Float, pitchDeg: Float) -> Unit
) : SensorEventListener {
    private val TAG = "HeadTracker"
    private val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val orientation = FloatArray(3)
    private val rotationMatrix = FloatArray(9)

    private var yawOffset = prefs.getFloat("head_yaw_offset", 0f)
    private var pitchOffset = prefs.getFloat("head_pitch_offset", 0f)
    private var rawYaw = 0f
    private var rawPitch = 0f

    private val _available = MutableStateFlow(rotation != null)
    val available: StateFlow<Boolean> = _available.asStateFlow()

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _yawDeg = MutableStateFlow(0f)
    val yawDeg: StateFlow<Float> = _yawDeg.asStateFlow()

    private val _pitchDeg = MutableStateFlow(0f)
    val pitchDeg: StateFlow<Float> = _pitchDeg.asStateFlow()

    private val _calibrated = MutableStateFlow(yawOffset != 0f || pitchOffset != 0f)
    val calibrated: StateFlow<Boolean> = _calibrated.asStateFlow()

    fun start() {
        if (rotation == null) {
            _available.value = false
            return
        }
        if (_enabled.value) return
        yawOffset = prefs.getFloat("head_yaw_offset", 0f)
        pitchOffset = prefs.getFloat("head_pitch_offset", 0f)
        _calibrated.value = yawOffset != 0f || pitchOffset != 0f
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_GAME)
        _enabled.value = true
        _available.value = true
        Log.i(TAG, "head-tracking aan")
    }

    fun stop() {
        if (!_enabled.value) return
        sensorManager.unregisterListener(this)
        _enabled.value = false
        _yawDeg.value = 0f
        _pitchDeg.value = 0f
        onPose(0f, 0f)
        Log.i(TAG, "head-tracking uit")
    }

    fun calibrateNeutral() {
        yawOffset = rawYaw
        pitchOffset = rawPitch
        prefs.edit()
            .putFloat("head_yaw_offset", yawOffset)
            .putFloat("head_pitch_offset", pitchOffset)
            .apply()
        _calibrated.value = true
        emitPose()
        Log.i(TAG, "kalibratie yaw=$yawOffset pitch=$pitchOffset")
    }

    fun resetCalibration() {
        yawOffset = 0f
        pitchOffset = 0f
        prefs.edit()
            .remove("head_yaw_offset")
            .remove("head_pitch_offset")
            .apply()
        _calibrated.value = false
        emitPose()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        SensorManager.getOrientation(rotationMatrix, orientation)
        rawYaw = Math.toDegrees(orientation[0].toDouble()).toFloat()
        rawPitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
        emitPose()
    }

    private fun emitPose() {
        val yaw = wrap180(rawYaw - yawOffset)
        val pitch = wrap180(rawPitch - pitchOffset)
        _yawDeg.value = (yaw * 10f).roundToInt() / 10f
        _pitchDeg.value = (pitch * 10f).roundToInt() / 10f
        onPose(yaw, pitch)
    }

    private fun wrap180(deg: Float): Float {
        var v = deg
        while (v > 180f) v -= 360f
        while (v < -180f) v += 360f
        return v
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
