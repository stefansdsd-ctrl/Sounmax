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
        context.sendBroadcast(
            Intent(ActivitySceneMonitor.ACTION)
                .setPackage(context.packageName)
                .putExtra(ActivitySceneMonitor.EXTRA_TYPE, last.activityType)
        )
    }
}
