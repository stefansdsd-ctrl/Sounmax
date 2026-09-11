package com.example.media

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Now-playing toegang + tijdens Focus: sociale meldingen wegdrukken.
 * Zet aan via: Instellingen → Meldingen → Meldingsstoegang → SoundMax.
 */
class SoundMaxNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val n = sbn ?: return
        if (!FocusSession.isActive(this)) return
        if (!DndFocusFilter.enabled(this)) return
        if (n.isOngoing) return
        if (n.packageName == packageName) return
        if (!DndFocusFilter.isNoisyPackage(n.packageName)) return
        runCatching { cancelNotification(n.key) }
    }
}
