package com.example.media

import android.annotation.SuppressLint
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.example.dsp.BluetoothCodec

object BtCodecProbe {
    fun label(context: Context, device: BluetoothDevice?): String? {
        probeFromAudioManager(context)?.let { return it }
        probeFromA2dp(device)?.let { return it }
        return null
    }

    fun codec(context: Context, device: BluetoothDevice?): BluetoothCodec? {
        val raw = label(context, device)?.uppercase() ?: return null
        return when {
            raw.contains("LDAC") -> BluetoothCodec.LDAC
            raw.contains("LC3") || raw.contains("LE AUDIO") -> BluetoothCodec.LE_AUDIO
            raw.contains("APTX ADAPT") || raw.contains("APTX_ADAPTIVE") -> BluetoothCodec.APTX_ADAPTIVE
            raw.contains("APTX HD") || raw.contains("APTX_HD") -> BluetoothCodec.APTX_HD
            raw.contains("APTX") -> BluetoothCodec.APTX_ADAPTIVE
            raw.contains("AAC") -> BluetoothCodec.AAC
            raw.contains("SBC") -> BluetoothCodec.SBC
            else -> null
        }
    }

    private fun probeFromAudioManager(context: Context): String? {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val devices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val bt = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                it.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
                it.type == AudioDeviceInfo.TYPE_BLE_SPEAKER
        } ?: return null
        if (Build.VERSION.SDK_INT >= 31) {
            try {
                val profiles = bt.audioProfiles
                val name = profiles.firstOrNull()?.name
                if (!name.isNullOrBlank()) return name
            } catch (_: Exception) {
            }
        }
        return try {
            am.getProperty("bluetooth.a2dp.codec")
        } catch (_: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun probeFromA2dp(device: BluetoothDevice?): String? {
        if (device == null) return null
        return try {
            val statusMethod = BluetoothA2dp::class.java.methods.firstOrNull { it.name == "getCodecStatus" }
            if (statusMethod == null) return null
            null
        } catch (_: Exception) {
            null
        }
    }

    fun qualityFromRssi(rssi: Int?): String? = when {
        rssi == null -> null
        rssi >= -55 -> "signaal sterk"
        rssi >= -70 -> "signaal ok"
        rssi >= -85 -> "signaal zwak"
        else -> "signaal slecht"
    }
}
