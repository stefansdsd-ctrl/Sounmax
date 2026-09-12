package com.example.media

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.example.dsp.ConversationBoost
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes

/**
 * Tijdens gesprek: ConversationBoost + talk-through + gesprek-scene.
 * Na ophangen: vorige scene + boost uit.
 */
object CallModeGuard {
    @Volatile var inCall: Boolean = false
        private set
    @Volatile private var sceneBefore: String? = null
    @Volatile private var talkBefore: Boolean = false
    @Volatile private var started: Boolean = false

    fun start(context: Context, currentSceneId: () -> String?, applyScene: (ListeningScene) -> Unit) {
        if (started) return
        started = true
        val app = context.applicationContext
        val tm = app.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return
        @Suppress("DEPRECATION")
        tm.listen(object : PhoneStateListener() {
            @Deprecated("Deprecated in Java")
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK,
                    TelephonyManager.CALL_STATE_RINGING -> enterCall(app, currentSceneId, applyScene)
                    TelephonyManager.CALL_STATE_IDLE -> leaveCall(app, applyScene)
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun enterCall(
        context: Context,
        currentSceneId: () -> String?,
        applyScene: (ListeningScene) -> Unit
    ) {
        if (inCall) return
        inCall = true
        sceneBefore = currentSceneId()
        talkBefore = TalkThrough.enabled(context)
        ConversationBoost.apply(true)
        TalkThrough.setEnabled(context, true)
        TalkThrough.onRms(1f)
        val talk = ListeningScenes.byId("gesprek")
            ?: ListeningScenes.byId("transparency")
            ?: ListeningScenes.byId("conversation")
        if (talk != null) applyScene(talk)
        context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .edit().putBoolean("call_session_active", true).apply()
    }

    private fun leaveCall(context: Context, applyScene: (ListeningScene) -> Unit) {
        if (!inCall) return
        inCall = false
        TalkThrough.release()
        ConversationBoost.apply(false)
        if (!talkBefore) TalkThrough.setEnabled(context, false)
        sceneBefore?.let { id ->
            ListeningScenes.byId(id)?.let(applyScene)
        }
        sceneBefore = null
        context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            .edit().putBoolean("call_session_active", false).apply()
    }
}
