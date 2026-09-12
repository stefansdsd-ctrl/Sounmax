package com.example.wearapp

import android.content.ComponentName
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService

/** Koppelt phone status-datapad aan de watchface-complication. */
class WearStatusListener : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        var hit = false
        for (ev in dataEvents) {
            if (ev.type == DataEvent.TYPE_CHANGED && ev.dataItem.uri.path == WearPaths.STATUS) {
                hit = true
            }
        }
        dataEvents.release()
        if (!hit) return
        ComplicationDataSourceUpdateRequester
            .create(this, ComponentName(this, SounmaxComplicationService::class.java))
            .requestUpdateAll()
    }
}
