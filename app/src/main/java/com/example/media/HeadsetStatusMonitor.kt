package com.example.media

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.widget.SoundMaxWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HeadsetStatus(
    val connected: Boolean = false,
    val name: String? = null,
    val batteryPercent: Int? = null,
    val wired: Boolean = false,
    val rssiDbm: Int? = null,
    val rssiLiveGatt: Boolean = false
)

class HeadsetStatusMonitor(
    private val context: Context,
    private val onConnectionChanged: (connected: Boolean) -> Unit = {},
    private val onRssi: (rssiDbm: Int) -> Unit = {}
) {
    private val _status = MutableStateFlow(HeadsetStatus())
    val status: StateFlow<HeadsetStatus> = _status.asStateFlow()
    private val handler = Handler(Looper.getMainLooper())
    private var gatt: BluetoothGatt? = null
    private var gattAddress: String? = null
    private var lowBatteryWarned = false

    private val pollRssi = object : Runnable {
        override fun run() {
            try {
                gatt?.readRemoteRssi()
            } catch (_: Exception) {
            }
            handler.postDelayed(this, 8_000L)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                try {
                    g.readRemoteRssi()
                } catch (_: Exception) {
                }
                handler.removeCallbacks(pollRssi)
                handler.postDelayed(pollRssi, 8_000L)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                handler.removeCallbacks(pollRssi)
            }
        }

        override fun onReadRemoteRssi(g: BluetoothGatt, rssi: Int, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS && rssi in -120..0) {
                _status.value = _status.value.copy(rssiDbm = rssi, rssiLiveGatt = true)
                onRssi(rssi)
                HeadsetLocator.rememberIfConnected(context, _status.value)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    refresh()
                    onConnectionChanged(true)
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED,
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                    HeadsetLocator.rememberIfConnected(context, _status.value.copy(connected = true))
                    closeGatt()
                    refresh()
                    onConnectionChanged(false)
                }
                Intent.ACTION_HEADSET_PLUG -> {
                    refresh()
                    val state = intent.getIntExtra("state", 0)
                    onConnectionChanged(state == 1)
                }
                "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED" -> refresh()
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                    if (rssi != Short.MIN_VALUE.toInt() && device != null && !_status.value.rssiLiveGatt) {
                        val current = _status.value.name
                        val foundName = try { device.name } catch (_: SecurityException) { null }
                        if (current != null && foundName == current) {
                            _status.value = _status.value.copy(rssiDbm = rssi)
                            onRssi(rssi)
                            HeadsetLocator.rememberIfConnected(context, _status.value)
                        }
                    }
                }
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
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        try {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } catch (_: Exception) {
            try {
                context.registerReceiver(receiver, filter)
            } catch (_: Exception) {
            }
        }
        HeadsetLocator.load(context)
        refresh()
    }

    fun stop() {
        handler.removeCallbacks(pollRssi)
        closeGatt()
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
        if (device != null) attachGatt(device)
        val rssi = _status.value.rssiDbm ?: device?.let { tryReadRssi(it) }
        if (rssi != null) onRssi(rssi)
        _status.value = HeadsetStatus(
            connected = wired || btAudio || device != null,
            name = name,
            batteryPercent = battery,
            wired = wired,
            rssiDbm = rssi,
            rssiLiveGatt = _status.value.rssiLiveGatt && gatt != null
        )
        persist(name, battery)
        HeadsetLocator.rememberIfConnected(context, _status.value)
        if (battery != null && battery <= 15 && _status.value.connected) {
            if (!lowBatteryWarned) {
                lowBatteryWarned = true
                Toast.makeText(context, "Headset-accu ${battery}% — spaarstand aangeraden", Toast.LENGTH_SHORT).show()
            }
        } else {
            lowBatteryWarned = false
        }
    }

    @SuppressLint("MissingPermission")
    private fun attachGatt(device: BluetoothDevice) {
        val address = try { device.address } catch (_: Exception) { null }
        if (address == null || address == gattAddress) return
        closeGatt()
        try {
            gatt = device.connectGatt(context, true, gattCallback, BluetoothDevice.TRANSPORT_LE)
            gattAddress = address
        } catch (_: Exception) {
            try {
                gatt = device.connectGatt(context, true, gattCallback)
                gattAddress = address
            } catch (_: Exception) {
                gatt = null
                gattAddress = null
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt() {
        handler.removeCallbacks(pollRssi)
        try {
            gatt?.disconnect()
        } catch (_: Exception) {
        }
        try {
            gatt?.close()
        } catch (_: Exception) {
        }
        gatt = null
        gattAddress = null
    }

    private fun persist(name: String?, battery: Int?) {
        try {
            val prefs = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
            prefs.edit()
                .putString(SoundMaxWidget.KEY_HEADSET_NAME, name)
                .putInt(SoundMaxWidget.KEY_BATTERY, battery ?: -1)
                .apply()
            SoundMaxWidget.refreshAll(context)
        } catch (_: Exception) {
        }
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

    private fun tryReadRssi(device: BluetoothDevice): Int? {
        val names = listOf("getRssi", "getRssiValue", "readRssi")
        for (n in names) {
            try {
                val method = device.javaClass.getMethod(n)
                when (val value = method.invoke(device)) {
                    is Int -> if (value in -120..0) return value
                    is Short -> if (value.toInt() in -120..0) return value.toInt()
                }
            } catch (_: Exception) {
            }
        }
        return _status.value.rssiDbm
    }
}
