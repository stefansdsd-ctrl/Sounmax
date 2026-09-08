package com.example.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log

/**
 * Discovery van Philips/vendor FE-services voor ANC.
 * Gebruikt door [RealAncController] als kandidaat-bron.
 */
object VendorAncProbe {
    private const val TAG = "VendorAncProbe"

    @Volatile
    var lastDump: String = ""
        private set

    @Volatile
    var lastAncCandidate: BluetoothGattCharacteristic? = null
        private set

    private val candidateServiceHints = listOf(
        "fe", "fd", "ff", "anc", "noise", "ambient", "hear"
    )

    fun probe(gatt: BluetoothGatt): List<DiscoveryLogItem> {
        val logs = mutableListOf<DiscoveryLogItem>()
        val sb = StringBuilder()
        lastAncCandidate = null
        val writeCandidates = mutableListOf<BluetoothGattCharacteristic>()

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
                if (vendorish && canWrite(c.properties)) {
                    writeCandidates += c
                    logs += DiscoveryLogItem("ANC write-kandidaat", c.uuid.toString())
                }
            }
        }

        // Voorkeur: char met WRITE (niet alleen NO_RESPONSE) in FE-service
        lastAncCandidate = writeCandidates.firstOrNull {
            (it.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
        } ?: writeCandidates.firstOrNull()

        lastDump = sb.toString().take(8000)
        Log.i(TAG, "GATT dump (${lastDump.length} chars), candidate=${lastAncCandidate?.uuid}")
        return logs
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
