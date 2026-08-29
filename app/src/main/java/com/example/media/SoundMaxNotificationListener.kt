package com.example.media

import android.service.notification.NotificationListenerService

/**
 * Lege listener zodat MediaSessionManager.getActiveSessions() mag.
 * Zet aan via: Instellingen → Meldingen → Meldingsstoegang → SoundMax.
 */
class SoundMaxNotificationListener : NotificationListenerService()
