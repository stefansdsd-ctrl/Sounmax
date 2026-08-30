package com.example.wear

import android.content.Context
import android.util.Log
import com.example.dsp.ListeningScenes
import com.example.media.DspControlService
import com.example.widget.SoundMaxWidget
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

object WearBridge {
    private const val TAG = "WearBridge"

    fun publishStatus(context: Context) {
        try {
            val ui = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
            val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            val scene = ListeningScenes.byId(wellness.getString("last_scene_id", null))
                ?: ListeningScenes.ALL.first()
            val req = PutDataMapRequest.create(WearPaths.STATUS).apply {
                dataMap.putBoolean(WearPaths.KEY_DSP, ui.getBoolean(DspControlService.KEY_DSP, true))
                dataMap.putString(WearPaths.KEY_SCENE_ID, scene.id)
                dataMap.putString(WearPaths.KEY_SCENE_NAME, scene.name)
                dataMap.putString(WearPaths.KEY_SCENE_EMOJI, scene.emoji)
                dataMap.putInt(WearPaths.KEY_BATTERY, wellness.getInt(SoundMaxWidget.KEY_BATTERY, -1))
                dataMap.putInt(
                    WearPaths.KEY_SLEEP,
                    SoundMaxWidget.remainingSleepMinutes(wellness.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L))
                )
                dataMap.putString(
                    WearPaths.KEY_HEADSET,
                    wellness.getString(SoundMaxWidget.KEY_HEADSET_NAME, "Headset") ?: "Headset"
                )
                dataMap.putLong("ts", System.currentTimeMillis())
            }
            Wearable.getDataClient(context).putDataItem(req.asPutDataRequest().setUrgent())
        } catch (e: Exception) {
            Log.w(TAG, "publishStatus failed", e)
        }
    }

    fun handleCommand(context: Context, cmd: String) {
        when (cmd) {
            WearPaths.CMD_TOGGLE_DSP -> {
                val prefs = context.getSharedPreferences(DspControlService.PREFS, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(DspControlService.KEY_DSP, !prefs.getBoolean(DspControlService.KEY_DSP, true)).apply()
            }
            WearPaths.CMD_NEXT_SCENE -> SoundMaxWidget.cycleScene(context, +1)
            WearPaths.CMD_PREV_SCENE -> SoundMaxWidget.cycleScene(context, -1)
            WearPaths.CMD_CYCLE_SLEEP -> SoundMaxWidget.cycleSleep(context)
            WearPaths.CMD_SUGGEST -> SoundMaxWidget.applySuggested(context)
        }
        DspControlService.start(context)
        publishStatus(context)
    }
}
