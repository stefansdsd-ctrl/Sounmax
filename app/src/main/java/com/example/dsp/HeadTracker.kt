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
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val orientation = FloatArray(3)
    private val rotationMatrix = FloatArray(9)

    private val _available = MutableStateFlow(rotation != null)
    val available: StateFlow<Boolean> = _available.asStateFlow()

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _yawDeg = MutableStateFlow(0f)
    val yawDeg: StateFlow<Float> = _yawDeg.asStateFlow()

    fun start() {
        if (rotation == null) {
            _available.value = false
            return
        }
        if (_enabled.value) return
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
        onPose(0f, 0f)
        Log.i(TAG, "head-tracking uit")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        SensorManager.getOrientation(rotationMatrix, orientation)
        val yaw = Math.toDegrees(orientation[0].toDouble()).toFloat()
        val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
        _yawDeg.value = (yaw * 10f).roundToInt() / 10f
        onPose(yaw, pitch)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
