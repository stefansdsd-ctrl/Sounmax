package com.example.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.UUID

/**
 * Leert ANC-payloads door GATT-notifies te vangen terwijl de gebruiker
 * de hardware-ANC-knop indrukt. Geen payload-gokken.
 */
object AncNotifyLearner {
    private const val TAG = "AncLearn"
    private val CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    private val handler = Handler(Looper.getMainLooper())

    @Volatile var listening: Boolean = false
        private set
    @Volatile var lastHint: String = "druk ANC-knop op headset"
        private set

    private var armedUntil = 0L
    private var expected: RealAncController.HwMode? = null
    private var baseline = mapOf<String, String>()

    fun subscribeAll(g: BluetoothGatt) {
        for (svc in g.services.orEmpty()) {
            for (c in svc.characteristics.orEmpty()) {
                val props = c.properties
                if (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY == 0 &&
                    props and BluetoothGattCharacteristic.PROPERTY_INDICATE == 0
                ) continue
                runCatching {
                    g.setCharacteristicNotification(c, true)
                    c.getDescriptor(CCCD)?.let { d ->
                        val enable = if (props and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {
                            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        } else {
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        }
                        @Suppress("DEPRECATION")
                        d.value = enable
                        @Suppress("DEPRECATION")
                        g.writeDescriptor(d)
                    }
                }
            }
        }
    }

    fun start(context: Context, g: BluetoothGatt?, mode: RealAncController.HwMode, seconds: Int = 12) {
        listening = true
        expected = mode
        baseline = snapshot()
        armedUntil = System.currentTimeMillis() + seconds * 1000L
        lastHint = "druk knop → ${mode.label} (${seconds}s)"
        g?.let { subscribeAll(it) }
        handler.postDelayed({
            if (listening) {
                listening = false
                lastHint = "geen notify in ${seconds}s"
            }
        }, seconds * 1000L)
        Log.i(TAG, lastHint)
    }

    fun stop() {
        listening = false
        expected = null
        lastHint = "leren gestopt"
    }

    fun onNotify(context: Context, characteristic: BluetoothGattCharacteristic, value: ByteArray?) {
        val bytes = value ?: return
        if (bytes.isEmpty()) return
        val key = characteristic.uuid.toString()
        val hex = bytes.joinToString("") { "%02X".format(it) }
        if (!listening || System.currentTimeMillis() > armedUntil) return
        val prev = baseline[key]
        if (prev == hex) return
        val mode = expected ?: return
        val svc = characteristic.service?.uuid?.toString() ?: ""
        RealAncController.learnSuccess(context, svc, key, mode, bytes)
        lastHint = "geleerd ${mode.label}: ${hex.take(16)}"
        listening = false
        Log.i(TAG, lastHint)
    }

    private fun snapshot(): Map<String, String> = emptyMap()
}
