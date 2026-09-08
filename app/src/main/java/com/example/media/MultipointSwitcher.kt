package com.example.media

import android.annotation.SuppressLint
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Lijst gekoppelde audio-apparaten en wissel de actieve A2DP-sink.
 * Hardware-multipoint zelf blijft bij de headset; dit stuurt de telefoon-kant.
 */
object MultipointSwitcher {
    data class Sink(
        val address: String,
        val name: String,
        val connected: Boolean,
        val batteryPercent: Int?
    )

    private val _sinks = MutableStateFlow<List<Sink>>(emptyList())
    val sinks: StateFlow<List<Sink>> = _sinks.asStateFlow()

    @SuppressLint("MissingPermission")
    fun refresh(context: Context): List<Sink> {
        val manager = context.getSystemService(BluetoothManager::class.java) ?: return emptyList()
        val adapter = manager.adapter ?: BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        val connected = try {
            manager.getConnectedDevices(BluetoothProfile.A2DP).map { it.address }.toSet()
        } catch (_: Exception) {
            emptySet()
        }
        val list = try {
            adapter.bondedDevices.orEmpty()
                .filter { it.isAudioSink() }
                .map { device ->
                    Sink(
                        address = device.address,
                        name = try { device.name ?: device.address } catch (_: SecurityException) { device.address },
                        connected = device.address in connected,
                        batteryPercent = readBattery(device)
                    )
                }
                .sortedWith(compareByDescending<Sink> { it.connected }.thenBy { it.name.lowercase() })
        } catch (_: Exception) {
            emptyList()
        }
        _sinks.value = list
        return list
    }

    @SuppressLint("MissingPermission")
    fun switchTo(context: Context, address: String): Boolean {
        val manager = context.getSystemService(BluetoothManager::class.java) ?: return false
        val adapter = manager.adapter ?: return false
        val target = try {
            adapter.bondedDevices?.firstOrNull { it.address.equals(address, ignoreCase = true) }
        } catch (_: Exception) {
            null
        } ?: return false

        return try {
            adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    if (profile != BluetoothProfile.A2DP) return
                    val a2dp = proxy as BluetoothA2dp
                    try {
                        a2dp.connectedDevices.filter { it.address != target.address }.forEach { other ->
                            invokeA2dp(a2dp, "disconnect", other)
                        }
                    } catch (_: Exception) {}
                    invokeA2dp(a2dp, "connect", target)
                    try { adapter.closeProfileProxy(BluetoothProfile.A2DP, a2dp) } catch (_: Exception) {}
                    refresh(context)
                }

                override fun onServiceDisconnected(profile: Int) {}
            }, BluetoothProfile.A2DP)
            true
        } catch (_: Exception) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    private fun BluetoothDevice.isAudioSink(): Boolean {
        val major = bluetoothClass?.majorDeviceClass ?: return false
        return major == BluetoothClass.Device.Major.AUDIO_VIDEO ||
            major == BluetoothClass.Device.Major.WEARABLE
    }

    private fun invokeA2dp(a2dp: BluetoothA2dp, method: String, device: BluetoothDevice) {
        try {
            BluetoothA2dp::class.java.getMethod(method, BluetoothDevice::class.java).invoke(a2dp, device)
        } catch (_: Exception) {}
    }

    private fun readBattery(device: BluetoothDevice): Int? {
        return try {
            val level = BluetoothDevice::class.java.getMethod("getBatteryLevel").invoke(device) as? Int
            if (level != null && level in 0..100) level else null
        } catch (_: Exception) {
            null
        }
    }
}
