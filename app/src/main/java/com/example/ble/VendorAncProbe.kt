package com.example.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
import java.util.UUID

/**
 * Probeert Philips/vendor FE-services voor ANC-achtige characteristics.
 * Zonder officiële dump: leest/schrijft niets gevaarlijks; alleen discovery + log.
 * Slaat dump-string op in prefs via [lastDump] voor later reverse-engineeren.
 */
object VendorAncProbe {
    private const val TAG = "VendorAncProbe"

    @Volatile
    var lastDump: String = ""
        private set

    @Volatile
    var lastAncCandidate: BluetoothGattCharacteristic? = null
        private set

    /** UUIDs die vaak bij TWS/ANC vendors voorkomen (gok + FE-range). */
    private val candidateServiceHints = listOf(
        "fe", "fd", "ff", "anc", "noise", "ambient", "hear"
    )

    fun probe(gatt: BluetoothGatt): List<DiscoveryLogItem> {
        val logs = mutableListOf<DiscoveryLogItem>()
        val sb = StringBuilder()
        lastAncCandidate = null

        gatt.services.orEmpty().forEach { service ->
            val su = service.uuid.toString().lowercase()
            val vendorish = su.startsWith("0000fe") || su.startsWith("0000fd") ||
                su.startsWith("0000ff") || candidateServiceHints.any { su.contains(it) }
            if (!vendorish && service.characteristics.size < 2) return@forEach

            sb.appendLine("S ${service.uuid} chars=${service.characteristics.size}")
            logs += DiscoveryLogItem(
                if (vendorish) "Vendor/ANC-kandidaat" else "Service",
                "${service.uuid} · ${service.characteristics.size} char"
            )

            service.characteristics.orEmpty().forEach { c ->
                val props = propsLabel(c.properties)
                sb.appendLine("  C ${c.uuid} props=$props")
                logs += DiscoveryLogItem("Char", "${c.uuid} · $props")
                if (vendorish && canWrite(c.properties) && lastAncCandidate == null) {
                    lastAncCandidate = c
                    logs += DiscoveryLogItem("ANC write-kandidaat", c.uuid.toString())
                }
            }
        }

        lastDump = sb.toString().take(8000)
        Log.i(TAG, "GATT dump (${lastDump.length} chars), candidate=${lastAncCandidate?.uuid}")
        return logs
    }

    /**
     * Probeert een 1-byte mode write op de laatste kandidaat.
     * Modes: 0=off, 1=strong, 2=adaptive, 3=ambient, 4=wind — gok, veilig no-op bij fail.
     */
    fun tryWriteMode(gatt: BluetoothGatt, mode: Int): Boolean {
        val c = lastAncCandidate ?: return false
        return runCatching {
            val value = byteArrayOf((mode.coerceIn(0, 4) and 0xFF).toByte())
            @Suppress("DEPRECATION")
            c.value = value
            @Suppress("DEPRECATION")
            gatt.writeCharacteristic(c)
        }.getOrDefault(false)
    }

    fun dumpSummary(): String {
        val c = lastAncCandidate?.uuid?.toString() ?: "geen"
        return "ANC-kandidaat: $c\n\n$lastDump".take(4000)
    }

    private fun canWrite(props: Int): Boolean {
        val w = BluetoothGattCharacteristic.PROPERTY_WRITE
        val wn = BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
        return (props and w) != 0 || (props and wn) != 0
    }

    private fun propsLabel(props: Int): String {
        val parts = mutableListOf<String>()
        if (props and BluetoothGattCharacteristic.PROPERTY_READ != 0) parts += "R"
        if (props and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) parts += "W"
        if (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) parts += "Wn"
        if (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) parts += "N"
        if (props and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) parts += "I"
        return parts.joinToString("|")
    }
}
