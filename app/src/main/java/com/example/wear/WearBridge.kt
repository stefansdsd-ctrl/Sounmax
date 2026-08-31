package com.example.wear

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.dsp.AncMode
import com.example.dsp.ListeningScenes
import com.example.media.DspControlService
import com.example.media.FindHeadsetHelper
import com.example.media.MediaRemote
import com.example.media.QuietHours
import com.example.qs.AncQuickTileService
import com.example.qs.SpatialQuickTileService
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
                dataMap.putString(
                    WearPaths.KEY_ANC,
                    wellness.getString("last_anc", AncMode.STRONG.name) ?: AncMode.STRONG.name
                )
                dataMap.putBoolean(WearPaths.KEY_SPATIAL, wellness.getBoolean("spatializer_on", false))
                dataMap.putBoolean(WearPaths.KEY_HEAD_TRACK, wellness.getBoolean("head_tracking", false))
                dataMap.putBoolean(WearPaths.KEY_QUIET, QuietHours.isQuietNow(context) && QuietHours.enabled(context))
                dataMap.putInt(WearPaths.KEY_DOSE, todayDose(wellness))
                dataMap.putInt(WearPaths.KEY_VOLUME, MediaRemote.musicVolumePercent(context))
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
            WearPaths.CMD_CYCLE_ANC -> cycleAnc(context)
            WearPaths.CMD_CYCLE_SPATIAL -> cycleSpatial(context)
            WearPaths.CMD_FIND_HEADSET -> FindHeadsetHelper.ping()
            WearPaths.CMD_PLAY_PAUSE -> MediaRemote.playPause(context)
            WearPaths.CMD_VOL_UP -> MediaRemote.volume(context, raise = true)
            WearPaths.CMD_VOL_DOWN -> MediaRemote.volume(context, raise = false)
        }
        DspControlService.start(context)
        publishStatus(context)
    }

    private fun todayDose(wellness: android.content.SharedPreferences): Int {
        val cal = java.util.Calendar.getInstance()
        val key = "dose_${cal.get(java.util.Calendar.YEAR)}_${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
        return wellness.getInt(key, 0)
    }

    private fun cycleAnc(context: Context) {
        val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        val current = runCatching {
            AncMode.valueOf(prefs.getString("last_anc", AncMode.STRONG.name) ?: AncMode.STRONG.name)
        }.getOrDefault(AncMode.STRONG)
        val modes = AncMode.values()
        val next = modes[(modes.indexOf(current) + 1) % modes.size]
        prefs.edit().putString("last_anc", next.name).apply()
        context.sendBroadcast(
            Intent(AncQuickTileService.ACTION_CYCLE_ANC)
                .setPackage(context.packageName)
                .putExtra("anc", next.name)
        )
    }

    private fun cycleSpatial(context: Context) {
        val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        val spatial = prefs.getBoolean("spatializer_on", false)
        val track = prefs.getBoolean("head_tracking", false)
        val nextSpatial: Boolean
        val nextTrack: Boolean
        when {
            !spatial -> { nextSpatial = true; nextTrack = false }
            spatial && !track -> { nextSpatial = true; nextTrack = true }
            else -> { nextSpatial = false; nextTrack = false }
        }
        prefs.edit()
            .putBoolean("spatializer_on", nextSpatial)
            .putBoolean("head_tracking", nextTrack)
            .apply()
        context.sendBroadcast(
            Intent(SpatialQuickTileService.ACTION_CYCLE_SPATIAL)
                .setPackage(context.packageName)
                .putExtra("enabled", nextSpatial)
                .putExtra("head_tracking", nextTrack)
        )
    }
}
