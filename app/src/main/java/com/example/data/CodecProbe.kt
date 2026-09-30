package com.example.data

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build

/**
 * Leest de actieve A2DP-codec (LDAC/AAC/SBC/aptX) via BluetoothA2dp.
 * Geen vendor-GATT nodig; werkt op standaard Android 8+.
 */
object CodecProbe {
    private const val PREFS = "sounmax_codec_probe"

    fun cycle(context: Context): String {
        val snap = snapshot(context)
        prefs(context).edit().putString("last", snap).apply()
        return snap
    }

    fun last(context: Context): String =
        prefs(context).getString("last", "")?.takeIf { it.isNotBlank() } ?: snapshot(context)

    fun label(context: Context): String = last(context)

    fun snapshot(context: Context): String {
        return runCatching {
            val mgr = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            val adapter = mgr?.adapter ?: BluetoothAdapter.getDefaultAdapter()
            if (adapter == null || !adapter.isEnabled) return "BT uit"
            val name = adapter.getProfileProxyName(context)
            name
        }.getOrElse { "Codec onbekend" }
    }

    private fun BluetoothAdapter.getProfileProxyName(context: Context): String {
        var result = "A2DP wacht"
        val done = java.util.concurrent.CountDownLatch(1)
        val listener = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                try {
                    result = readCodec(proxy)
                } finally {
                    runCatching { closeProfileProxy(BluetoothProfile.A2DP, proxy) }
                    done.countDown()
                }
            }
            override fun onServiceDisconnected(profile: Int) {
                done.countDown()
            }
        }
        val ok = getProfileProxy(context, listener, BluetoothProfile.A2DP)
        if (!ok) return "Geen A2DP"
        done.await(1500, java.util.concurrent.TimeUnit.MILLISECONDS)
        return result
    }

    private fun readCodec(proxy: BluetoothProfile): String {
        val devices = proxy.connectedDevices
        if (devices.isEmpty()) return "Geen headset"
        val dev = devices.first()
        val name = dev.name ?: "headset"
        if (proxy !is BluetoothA2dp) return "A2DP · $name"
        if (Build.VERSION.SDK_INT < 26) return "SBC? · $name"
        val status = runCatching {
            val m = BluetoothA2dp::class.java.getMethod("getCodecStatus", android.bluetooth.BluetoothDevice::class.java)
            m.invoke(proxy, dev)
        }.getOrNull() ?: return "A2DP · $name"
        val codecName = runCatching {
            val cfg = status.javaClass.getMethod("getCodecConfig").invoke(status)
            val type = cfg.javaClass.getMethod("getCodecType").invoke(cfg) as Int
            when (type) {
                0 -> "SBC"
                1 -> "AAC"
                2 -> "aptX"
                3 -> "aptX HD"
                4 -> "LDAC"
                5 -> "LC3"
                6 -> "Opus"
                else -> "codec $type"
            }
        }.getOrDefault("A2DP")
        return "$codecName · $name"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
