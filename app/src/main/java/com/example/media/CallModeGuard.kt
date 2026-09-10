package com.example.media

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.example.dsp.ConversationBoost
import com.example.dsp.ListeningScene
import com.example.dsp.ListeningScenes

/**
 * Tijdens een gesprek: transparency/gesprek-scene + speech-EQ.
 * Na ophangen: vorige scene terug.
 */
object CallModeGuard {
    @Volatile var inCall: Boolean = false
        private set
    @Volatile private var sceneBefore: String? = null

    fun start(context: Context, currentSceneId: () -> String?, applyScene: (ListeningScene) -> Unit) {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return
        @Suppress("DEPRECATION")
        tm.listen(object : PhoneStateListener() {
            @Deprecated("Deprecated in Java")
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK,
                    TelephonyManager.CALL_STATE_RINGING -> {
                        if (!inCall) {
                            inCall = true
                            sceneBefore = currentSceneId()
                            ConversationBoost.apply(true)
                            val talk = ListeningScenes.byId("gesprek")
                                ?: ListeningScenes.byId("transparency")
                            if (talk != null) applyScene(talk)
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (inCall) {
                            inCall = false
                            ConversationBoost.apply(false)
                            sceneBefore?.let { id ->
                                ListeningScenes.byId(id)?.let(applyScene)
                            }
                            sceneBefore = null
                        }
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }
}
