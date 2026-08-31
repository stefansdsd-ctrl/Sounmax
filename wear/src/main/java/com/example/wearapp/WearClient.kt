package com.example.wearapp

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

data class WearStatus(
    val dsp: Boolean = true,
    val sceneName: String = "Scene",
    val sceneEmoji: String = "🎧",
    val battery: Int = -1,
    val sleepMin: Int = 0,
    val headset: String = "Headset",
    val anc: String = "STRONG",
    val spatial: Boolean = false,
    val headTrack: Boolean = false,
    val quiet: Boolean = false,
    val doseMin: Int = 0,
    val volume: Int = -1
)

object WearClient {
    suspend fun readStatus(context: Context): WearStatus {
        val items = Wearable.getDataClient(context).dataItems.await()
        val match = items.find { it.uri.path == WearPaths.STATUS }
        items.release()
        if (match == null) return WearStatus()
        val map = DataMapItem.fromDataItem(match).dataMap
        return WearStatus(
            dsp = map.getBoolean(WearPaths.KEY_DSP, true),
            sceneName = map.getString(WearPaths.KEY_SCENE_NAME) ?: "Scene",
            sceneEmoji = map.getString(WearPaths.KEY_SCENE_EMOJI) ?: "🎧",
            battery = map.getInt(WearPaths.KEY_BATTERY, -1),
            sleepMin = map.getInt(WearPaths.KEY_SLEEP, 0),
            headset = map.getString(WearPaths.KEY_HEADSET) ?: "Headset",
            anc = map.getString(WearPaths.KEY_ANC) ?: "STRONG",
            spatial = map.getBoolean(WearPaths.KEY_SPATIAL, false),
            headTrack = map.getBoolean(WearPaths.KEY_HEAD_TRACK, false),
            quiet = map.getBoolean(WearPaths.KEY_QUIET, false),
            doseMin = map.getInt(WearPaths.KEY_DOSE, 0),
            volume = map.getInt(WearPaths.KEY_VOLUME, -1)
        )
    }

    suspend fun send(context: Context, cmd: String) {
        haptic(context)
        val nodes = Wearable.getNodeClient(context).connectedNodes.await()
        val client = Wearable.getMessageClient(context)
        nodes.forEach { node ->
            client.sendMessage(node.id, WearPaths.CMD, cmd.toByteArray()).await()
        }
    }

    fun haptic(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
