package com.example.media

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import java.util.Calendar

class ActivitySceneMonitor(private val context: Context) {

    private val client = ActivityRecognition.getClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityTransitionReceiver::class.java).setAction(ACTION)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        PendingIntent.getBroadcast(context, 91, intent, flags)
    }

    fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun start() {
        if (!hasPermission()) return
        val transitions = listOf(
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.RUNNING,
            DetectedActivity.WALKING,
            DetectedActivity.ON_FOOT,
            DetectedActivity.STILL
        ).flatMap { type ->
            listOf(
                ActivityTransition.Builder()
                    .setActivityType(type)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()
            )
        }
        client.requestActivityTransitionUpdates(
            ActivityTransitionRequest(transitions),
            pendingIntent
        )
    }

    fun stop() {
        try {
            client.removeActivityTransitionUpdates(pendingIntent)
        } catch (_: Exception) {
        }
    }

    companion object {
        const val ACTION = "com.example.ACTION_ACTIVITY_SCENE"
        const val EXTRA_TYPE = "activity_type"

        fun sceneIdFor(type: Int): String? {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val weekday = dow in Calendar.MONDAY..Calendar.FRIDAY
            return when (type) {
                DetectedActivity.IN_VEHICLE -> when {
                    hour in 7..9 || hour in 16..19 -> "commute"
                    hour >= 21 || hour < 6 -> "nightdrive"
                    else -> "car"
                }
                DetectedActivity.ON_BICYCLE -> if (hour >= 19 || hour < 6) "avondfiets" else "bike"
                DetectedActivity.RUNNING -> "cardio"
                DetectedActivity.WALKING, DetectedActivity.ON_FOOT -> when {
                    hour >= 20 || hour < 6 -> "avondwandeling"
                    weekday && hour in 7..9 -> "podcastwalk"
                    else -> "walk"
                }
                DetectedActivity.STILL -> when {
                    weekday && hour in 9..17 -> "office"
                    weekday && hour in 18..21 -> "homeworkout"
                    hour >= 22 || hour < 6 -> "thuisavond"
                    else -> null
                }
                else -> null
            }
        }

        fun labelFor(type: Int): String = when (type) {
            DetectedActivity.IN_VEHICLE -> "Auto"
            DetectedActivity.ON_BICYCLE -> "Fiets"
            DetectedActivity.RUNNING -> "Hardlopen"
            DetectedActivity.WALKING, DetectedActivity.ON_FOOT -> "Lopen"
            DetectedActivity.STILL -> "Stil"
            else -> "Onbekend"
        }
    }
}
