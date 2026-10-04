package com.example.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.BikeWind
import com.example.data.RunWind
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityTransitionResult.hasResult(intent)) return
        val result = ActivityTransitionResult.extractResult(intent) ?: return
        val last = result.transitionEvents.lastOrNull() ?: return
        val label = when (last.activityType) {
            DetectedActivity.IN_VEHICLE -> "in_vehicle"
            DetectedActivity.ON_BICYCLE -> "cycling"
            DetectedActivity.WALKING,
            DetectedActivity.ON_FOOT -> "walk"
            DetectedActivity.RUNNING -> "run"
            else -> null
        }
        if (label != null) {
            context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                .edit()
                .putString("last_activity", label)
                .putLong("last_activity_at", System.currentTimeMillis())
                .apply()
        }
        if (last.activityType == DetectedActivity.RUNNING) {
            RunWind.apply(context)
        }
        if (last.activityType == DetectedActivity.ON_BICYCLE) {
            BikeWind.apply(context)
        }
        context.sendBroadcast(
            Intent(ActivitySceneMonitor.ACTION)
                .setPackage(context.packageName)
                .putExtra(ActivitySceneMonitor.EXTRA_TYPE, last.activityType)
        )
    }
}
