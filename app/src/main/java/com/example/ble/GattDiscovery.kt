package com.example.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import java.util.UUID

data class DiscoveryLogItem(
    val title: String,
    val detail: String
)

data class ServiceDiscoveryResult(
    val knownServices: List<BluetoothGattService> = emptyList(),
    val unknownServices: List<BluetoothGattService> = emptyList(),
    val logs: List<DiscoveryLogItem> = emptyList()
)

data class CharacteristicDiscoveryResult(
    val knownCharacteristics: List<BluetoothGattCharacteristic> = emptyList(),
    val unknownCharacteristics: List<BluetoothGattCharacteristic> = emptyList(),
    val logs: List<DiscoveryLogItem> = emptyList()
)

object BleUuids {
    val BATTERY_SERVICE: UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
    val BATTERY_LEVEL: UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    val DEVICE_INFO: UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
    val GENERIC_ACCESS: UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")
    val GENERIC_ATTRIBUTE: UUID = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")
    val HEARING_AID: UUID = UUID.fromString("0000184e-0000-1000-8000-00805f9b34fb")
    val AUDIO_INPUT_CONTROL: UUID = UUID.fromString("00001844-0000-1000-8000-00805f9b34fb")
    val VOLUME_CONTROL: UUID = UUID.fromString("00001844-0000-1000-8000-00805f9b34fb")
    val AUDIO_STREAM_CONTROL: UUID = UUID.fromString("0000184e-0000-1000-8000-00805f9b34fb")
    val MICROPHONE_CONTROL: UUID = UUID.fromString("0000184d-0000-1000-8000-00805f9b34fb")
    val PUBLISHED_AUDIO_CAP: UUID = UUID.fromString("00001850-0000-1000-8000-00805f9b34fb")
    val COORDINATED_SET: UUID = UUID.fromString("00001846-0000-1000-8000-00805f9b34fb")
    val COMMON_AUDIO: UUID = UUID.fromString("00001853-0000-1000-8000-00805f9b34fb")
    val HEARING_ACCESS: UUID = UUID.fromString("00001854-0000-1000-8000-00805f9b34fb")
    val TELEPHONY_MEDIA_AUDIO: UUID = UUID.fromString("00001855-0000-1000-8000-00805f9b34fb")
}

class ServiceDiscoveryMapper {
    private val known = mapOf(
        BleUuids.BATTERY_SERVICE to "Battery",
        BleUuids.DEVICE_INFO to "Device Information",
        BleUuids.GENERIC_ACCESS to "Generic Access",
        BleUuids.GENERIC_ATTRIBUTE to "Generic Attribute",
        BleUuids.HEARING_AID to "Hearing Aid",
        BleUuids.AUDIO_INPUT_CONTROL to "Audio Input Control / Volume",
        BleUuids.MICROPHONE_CONTROL to "Microphone Control",
        BleUuids.PUBLISHED_AUDIO_CAP to "Published Audio Capabilities",
        BleUuids.COORDINATED_SET to "Coordinated Set",
        BleUuids.COMMON_AUDIO to "Common Audio",
        BleUuids.HEARING_ACCESS to "Hearing Access",
        BleUuids.TELEPHONY_MEDIA_AUDIO to "Telephony & Media Audio"
    )

    fun map(gatt: BluetoothGatt): ServiceDiscoveryResult {
        val knownList = mutableListOf<BluetoothGattService>()
        val unknownList = mutableListOf<BluetoothGattService>()
        val logs = mutableListOf<DiscoveryLogItem>()
        gatt.services.orEmpty().forEach { service ->
            val label = known[service.uuid] ?: guessVendor(service.uuid)
            if (label != null) {
                knownList += service
                logs += DiscoveryLogItem(label, "${service.uuid} · ${service.characteristics.size} char")
            } else {
                unknownList += service
                logs += DiscoveryLogItem(
                    "Onbekende service",
                    "${service.uuid} · ${service.characteristics.size} char"
                )
            }
        }
        return ServiceDiscoveryResult(knownList, unknownList, logs)
    }

    private fun guessVendor(uuid: UUID): String? {
        val s = uuid.toString().lowercase()
        return when {
            s.startsWith("0000fe") -> "Vendor (Philips/Sound-FE)"
            s.startsWith("0000fd") -> "Vendor (FD-custom)"
            s.contains("0000180f") -> "Battery"
            else -> null
        }
    }
}

class CharacteristicDiscoveryMapper {
    fun map(service: BluetoothGattService): CharacteristicDiscoveryResult {
        val knownList = mutableListOf<BluetoothGattCharacteristic>()
        val unknownList = mutableListOf<BluetoothGattCharacteristic>()
        val logs = mutableListOf<DiscoveryLogItem>()
        service.characteristics.orEmpty().forEach { c ->
            when (c.uuid) {
                BleUuids.BATTERY_LEVEL -> {
                    knownList += c
                    logs += DiscoveryLogItem("Battery Level", "props=${c.properties}")
                }
                else -> {
                    unknownList += c
                    logs += DiscoveryLogItem("Onbekende characteristic", "${c.uuid} props=${c.properties}")
                }
            }
        }
        return CharacteristicDiscoveryResult(knownList, unknownList, logs)
    }
}

object BatteryServiceReader {
    fun findLevelCharacteristic(gatt: BluetoothGatt): BluetoothGattCharacteristic? {
        val service = gatt.getService(BleUuids.BATTERY_SERVICE) ?: return null
        return service.getCharacteristic(BleUuids.BATTERY_LEVEL)
    }

    fun parseLevel(characteristic: BluetoothGattCharacteristic): Int? {
        val raw = characteristic.value ?: return null
        if (raw.isEmpty()) return null
        val level = raw[0].toInt() and 0xFF
        return level.takeIf { it in 0..100 }
    }
}
