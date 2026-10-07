package com.example.data

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Camera open (foto of video): muziek stapsgewijs naar 48%.
 * Deelt DuckLane. Laagste cap wint. Geen CAMERA-permissie nodig.
 * Alleen Android 5+ (availability callback).
 */
object CamDuck {
    private const val PREFS = "sounmax_cam_duck"
    private const val CAP_PCT = 48
    private val open = AtomicInteger(0)
    private val watching = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) DuckLane.release(context, "cam")
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        if (!watching.compareAndSet(false, true)) return
        val app = context.applicationContext
        val cm = app.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cm.registerAvailabilityCallback(object : CameraManager.AvailabilityCallback() {
            override fun onCameraUnavailable(cameraId: String) {
                open.incrementAndGet()
                apply(app)
            }

            override fun onCameraAvailable(cameraId: String) {
                open.updateAndGet { (it - 1).coerceAtLeast(0) }
                tick(app)
            }
        }, handler)
    }

    fun cameraOpen(): Boolean = open.get() > 0

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !cameraOpen()) return false
        if (AlarmDuck.active(context) || RingDuck.active(context) || TimerDuck.active(context) || NavDuck.active(context)) {
            return false
        }
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) return false
        if (!am.isMusicActive) return false
        return DuckLane.hold(context, "cam", CAP_PCT)
    }

    fun tick(context: Context): Boolean {
        if (!enabled(context) || cameraOpen()) return false
        if (!DuckLane.heldBy(context, "cam") && !DuckLane.restoring(context)) return false
        return DuckLane.release(context, "cam")
    }

    fun active(context: Context): Boolean =
        enabled(context) && cameraOpen() &&
            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMusicActive

    fun label(context: Context) = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP -> "Cam-duck: Android 5+"
        !enabled(context) -> "Cam-duck uit"
        active(context) -> "Camera, muziek 48%"
        DuckLane.restoring(context) -> "Volume komt terug"
        else -> "Cam-duck aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
