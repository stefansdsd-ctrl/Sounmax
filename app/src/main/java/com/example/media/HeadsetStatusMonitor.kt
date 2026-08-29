package com.example.media

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HeadsetStatus(
    val connected: Boolean = false,
    val name: String? = null,
    val batteryPercent: Int? = null,
    val wired: Boolean = false
)

class HeadsetStatusMonitor(
    private val context: Context,
    private val onConnectionChanged: (connected: Boolean) -> Unit = {}
) {
    private val _status = MutableStateFlow(HeadsetStatus())
    val status: StateFlow<HeadsetStatus> = _status.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    refresh()
                    onConnectionChanged(true)
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED,
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                    refresh()
                    onConnectionChanged(false)
                }
                Intent.ACTION_HEADSET_PLUG -> {
                    refresh()
                    val state = intent.getIntExtra("state", 0)
                    onConnectionChanged(state == 1)
                }
                "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED" -> refresh()
            }
        }
    }

    fun start() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED")
        }
        try {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } catch (_: Exception) {
            try {
                context.registerReceiver(receiver, filter)
            } catch (_: Exception) {
            }
        }
        refresh()
    }

    fun stop() {
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
    }

    @SuppressLint("MissingPermission")
    fun refresh() {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val wired = am.isWiredHeadsetOn
        val btAudio = am.isBluetoothA2dpOn || am.isBluetoothScoOn
        val device = firstConnectedA2dp()
        val name = try {
            device?.name
        } catch (_: SecurityException) {
            null
        }
        val battery = device?.let { readBattery(it) }?.takeIf { it in 0..100 }
        _status.value = HeadsetStatus(
            connected = wired || btAudio || device != null,
            name = name,
            batteryPercent = battery,
            wired = wired
        )
    }

    @SuppressLint("MissingPermission")
    private fun firstConnectedA2dp(): BluetoothDevice? {
        return try {
            val manager = context.getSystemService(BluetoothManager::class.java) ?: return null
            val adapter = manager.adapter ?: BluetoothAdapter.getDefaultAdapter() ?: return null
            val connected = manager.getConnectedDevices(BluetoothProfile.A2DP)
            connected.firstOrNull()
                ?: adapter.bondedDevices?.firstOrNull { device ->
                    device.bluetoothClass?.majorDeviceClass ==
                        android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO
                }
        } catch (_: Exception) {
            null
        }
    }

    private fun readBattery(device: BluetoothDevice): Int? {
        return try {
            val method = BluetoothDevice::class.java.getMethod("getBatteryLevel")
            val level = method.invoke(device) as? Int
            if (level != null && level >= 0) level else null
        } catch (_: Exception) {
            null
        }
    }
}
