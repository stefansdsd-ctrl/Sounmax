package com.example.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log

/**
 * Zoekt vendor-GATT chars voor headset-multipoint (dual-connect).
 * Payload blijft onbekend tot TAH6519-dump; we onthouden write-kandidaten.
 */
object VendorMultipoint {
    private const val TAG = "VendorMultipoint"

    @Volatile
    var lastCandidate: BluetoothGattCharacteristic? = null
        private set

    @Volatile
    var lastDump: String = ""
        private set

    private val hints = listOf("multi", "point", "dual", "pair", "connect", "fe", "fd")

    fun probe(gatt: BluetoothGatt) {
        val sb = StringBuilder()
        val writes = mutableListOf<BluetoothGattCharacteristic>()
        gatt.services.orEmpty().forEach { service ->
            val su = service.uuid.toString().lowercase()
            val hit = hints.any { su.contains(it) } || su.startsWith("0000fe") || su.startsWith("0000fd")
            if (!hit && service.characteristics.size < 2) return@forEach
            sb.appendLine("S ${service.uuid}")
            service.characteristics.orEmpty().forEach { c ->
                val w = (c.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ||
                    (c.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
                sb.appendLine("  C ${c.uuid} w=$w")
                if (hit && w) writes += c
            }
        }
        lastCandidate = writes.firstOrNull()
        lastDump = sb.toString().take(4000)
        Log.i(TAG, "mp candidate=${lastCandidate?.uuid}")
    }

    fun summary(): String {
        val c = lastCandidate?.uuid?.toString() ?: "geen"
        return "Multipoint-kandidaat: $c\n\n$lastDump".take(2000)
    }
}
