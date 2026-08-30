package com.example.wear

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearCommandListener : WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent) {
        if (event.path != WearPaths.CMD) return
        val cmd = event.data.toString(Charsets.UTF_8)
        WearBridge.handleCommand(applicationContext, cmd)
    }
}
