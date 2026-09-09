package com.example.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import com.example.dsp.AncMode
import com.example.dsp.SoftwareAnc
import com.example.media.AncHaptics
import java.util.UUID

/**
 * Echte ANC voor Philips TAH6519-achtige headsets.
 *
 * Hardware-knop (handleiding): ANC ↔ Normaal ↔ Transparantie (3 modi).
 * App-modi mappen daarop; ADAPTIVE/WIND gebruiken hardware-ANC + soft-EQ.
 *
 * Zonder bekende dump: probeert geleerde UUID + payloads, daarna kandidaten.
 * Altijd soft-ANC als fallback zodat UI nooit "dood" is.
 */
object RealAncController {
    private const val TAG = "RealAnc"
    private const val PREFS = "soundmax_anc_hw"
    private const val KEY_SVC = "learned_svc"
    private const val KEY_CHAR = "learned_char"
    private const val KEY_PAYLOAD_PREFIX = "payload_"

    @Volatile
    private var gatt: BluetoothGatt? = null

    @Volatile
    var lastHwOk: Boolean = false
        private set

    @Volatile
    var lastMessage: String = "soft-ANC"
        private set

    fun attachGatt(g: BluetoothGatt?) {
        gatt = g
        if (g != null) {
            VendorAncProbe.probe(g)
        }
    }

    fun detachGatt() {
        gatt = null
        lastHwOk = false
    }

    /** Hardware-modus die de TAH6519-knop kent. */
    enum class HwMode(val code: Int, val label: String) {
        OFF(0, "Normaal"),
        ANC(1, "ANC"),
        AWARENESS(2, "Transparantie")
    }

    fun hwModeFor(app: AncMode): HwMode = when (app) {
        AncMode.OFF -> HwMode.OFF
        AncMode.AMBIENT -> HwMode.AWARENESS
        AncMode.STRONG, AncMode.ADAPTIVE, AncMode.WIND_GUARD -> HwMode.ANC
    }

    /**
     * Zet ANC: probeer hardware write, altijd soft-EQ erna.
     * @return true als hardware-write gestart is
     */
    fun apply(context: Context, mode: AncMode, noiseIntensity: Float = 0.5f): Boolean {
        val hw = hwModeFor(mode)
        val g = gatt
        var hwOk = false
        if (g != null) {
            hwOk = writeHw(context, g, hw)
        }
        SoftwareAnc.apply(mode, noiseIntensity)
        AncHaptics.pulse(context, mode)
        lastHwOk = hwOk
        lastMessage = if (hwOk) {
            "hardware ${hw.label} + soft ${mode.displayName}"
        } else {
            "soft ${mode.displayName} (geen GATT-write)"
        }
        Log.i(TAG, lastMessage)
        return hwOk
    }

    fun learnSuccess(context: Context, serviceUuid: String, charUuid: String, hw: HwMode, payload: ByteArray) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_SVC, serviceUuid)
            .putString(KEY_CHAR, charUuid)
            .putString(KEY_PAYLOAD_PREFIX + hw.code, payload.joinToString(",") { (it.toInt() and 0xFF).toString() })
            .apply()
        Log.i(TAG, "geleerd ${hw.label}: $charUuid ${payload.toHex()}")
    }

    fun clearLearned(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun statusLine(): String = lastMessage

    private fun writeHw(context: Context, g: BluetoothGatt, hw: HwMode): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val learnedChar = prefs.getString(KEY_CHAR, null)
        val learnedSvc = prefs.getString(KEY_SVC, null)
        val learnedPayload = prefs.getString(KEY_PAYLOAD_PREFIX + hw.code, null)?.let { parsePayload(it) }

        if (learnedChar != null && learnedPayload != null) {
            val c = findChar(g, learnedSvc, learnedChar)
            if (c != null && writeBytes(g, c, learnedPayload)) return true
        }

        val candidate = VendorAncProbe.lastAncCandidate
        if (candidate != null) {
            for (payload in payloadsFor(hw)) {
                if (writeBytes(g, candidate, payload)) {
                    learnSuccess(
                        context,
                        candidate.service?.uuid?.toString() ?: "",
                        candidate.uuid.toString(),
                        hw,
                        payload
                    )
                    return true
                }
            }
        }

        for (svc in g.services.orEmpty()) {
            val su = svc.uuid.toString().lowercase()
            if (!su.startsWith("0000fe") && !su.startsWith("0000fd") && !su.startsWith("0000ff")) continue
            for (c in svc.characteristics.orEmpty()) {
                if (!canWrite(c)) continue
                for (payload in payloadsFor(hw)) {
                    if (writeBytes(g, c, payload)) {
                        learnSuccess(context, svc.uuid.toString(), c.uuid.toString(), hw, payload)
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun payloadsFor(hw: HwMode): List<ByteArray> {
        val v = hw.code.toByte()
        return listOf(
            byteArrayOf(v),
            byteArrayOf(0x01, v),
            byteArrayOf(0x02, v),
            byteArrayOf(0x10, v),
            byteArrayOf(0x55, v),
            byteArrayOf(0xAA.toByte(), v),
            byteArrayOf(0x00, 0x01, v),
            byteArrayOf(0x01, 0x00, v),
            byteArrayOf(0xFE.toByte(), v),
            when (hw) {
                HwMode.OFF -> byteArrayOf(0x00)
                HwMode.ANC -> byteArrayOf(0x01)
                HwMode.AWARENESS -> byteArrayOf(0x03)
            },
            when (hw) {
                HwMode.OFF -> byteArrayOf(0x01, 0x00)
                HwMode.ANC -> byteArrayOf(0x01, 0x01)
                HwMode.AWARENESS -> byteArrayOf(0x01, 0x02)
            }
        ).distinctBy { it.toHex() }
    }

    private fun findChar(g: BluetoothGatt, svcUuid: String?, charUuid: String): BluetoothGattCharacteristic? {
        val cu = runCatching { UUID.fromString(charUuid) }.getOrNull() ?: return null
        if (svcUuid != null) {
            val su = runCatching { UUID.fromString(svcUuid) }.getOrNull()
            if (su != null) {
                g.getService(su)?.getCharacteristic(cu)?.let { return it }
            }
        }
        g.services.orEmpty().forEach { s ->
            s.getCharacteristic(cu)?.let { return it }
        }
        return null
    }

    private fun writeBytes(g: BluetoothGatt, c: BluetoothGattCharacteristic, value: ByteArray): Boolean {
        return runCatching {
            @Suppress("DEPRECATION")
            c.value = value
            @Suppress("DEPRECATION")
            g.writeCharacteristic(c)
        }.getOrDefault(false)
    }

    private fun canWrite(c: BluetoothGattCharacteristic): Boolean {
        val p = c.properties
        return (p and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ||
            (p and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
    }

    private fun parsePayload(csv: String): ByteArray? {
        val parts = csv.split(",").mapNotNull { it.trim().toIntOrNull()?.and(0xFF)?.toByte() }
        return if (parts.isEmpty()) null else parts.toByteArray()
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
}
