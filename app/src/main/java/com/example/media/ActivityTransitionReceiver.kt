package com.example.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityTransitionResult

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityTransitionResult.hasResult(intent)) return
        val result = ActivityTransitionResult.extractResult(intent) ?: return
        val last = result.transitionEvents.lastOrNull() ?: return
        val label = when (last.activityType) {
            com.google.android.gms.location.DetectedActivity.IN_VEHICLE -> "in_vehicle"
            com.google.android.gms.location.DetectedActivity.ON_BICYCLE -> "cycling"
            com.google.android.gms.location.DetectedActivity.WALKING,
            com.google.android.gms.location.DetectedActivity.ON_FOOT -> "walk"
            com.google.android.gms.location.DetectedActivity.RUNNING -> "walk"
            else -> null
        }
        if (label != null) {
            context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
                .edit()
                .putString("last_activity", label)
                .putLong("last_activity_at", System.currentTimeMillis())
                .apply()
        }
        context.sendBroadcast(
            Intent(ActivitySceneMonitor.ACTION)
                .setPackage(context.packageName)
                .putExtra(ActivitySceneMonitor.EXTRA_TYPE, last.activityType)
        )
    }
}
