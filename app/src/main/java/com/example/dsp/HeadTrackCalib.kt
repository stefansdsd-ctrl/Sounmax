package com.example.dsp

import android.content.Context

/**
 * Eenvoudige 3-stappen kalibratie voor spatial / head-tracking.
 * Slaat yaw-offset op; HeadTracker kan dit later aftrekken.
 */
object HeadTrackCalib {
    private const val PREFS = "sounmax_ht"
    private const val KEY_YAW = "yaw_offset"
    private const val KEY_DONE = "calib_done"

    fun isDone(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_DONE, false)

    fun yawOffset(context: Context): Float =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getFloat(KEY_YAW, 0f)

    fun saveYaw(context: Context, yaw: Float) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_YAW, yaw)
            .putBoolean(KEY_DONE, true)
            .apply()
    }

    fun reset(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
