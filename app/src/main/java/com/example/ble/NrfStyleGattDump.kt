package com.example.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * nRF Connect-achtige GATT-dump voor TAH6519 reverse-engineering.
 *
 * Workflow:
 * 1. Headset verbonden (HeadsetStatusMonitor GATT)
 * 2. [capture] met label bv. "ANC" / "OFF" / "AWARENESS"
 * 3. Druk fysieke ANC-knop, opnieuw [capture]
 * 4. Vergelijk dumps of deel .txt
 */
object NrfStyleGattDump {
    private const val TAG = "NrfGattDump"
    private const val FILE = "nrf_gatt_dump.txt"

    @Volatile
    private var gatt: BluetoothGatt? = null

    @Volatile
    var lastText: String = ""
        private set

    private val readQueue = ConcurrentLinkedQueue<BluetoothGattCharacteristic>()
    private val reading = AtomicBoolean(false)
    private val valueCache = java.util.concurrent.ConcurrentHashMap<String, String>()

    fun attach(g: BluetoothGatt?) {
        gatt = g
        valueCache.clear()
        readQueue.clear()
        reading.set(false)
    }

    fun detach() {
        gatt = null
        readQueue.clear()
        reading.set(false)
    }

    /** Start alle READ-characteristics (async via onCharacteristicRead). */
    fun startReads(g: BluetoothGatt = gatt ?: return) {
        valueCache.clear()
        readQueue.clear()
        g.services.orEmpty().forEach { svc ->
            svc.characteristics.orEmpty().forEach { c ->
                if ((c.properties and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                    readQueue.add(c)
                }
            }
        }
        pumpRead(g)
    }

    fun onCharacteristicRead(
        characteristic: BluetoothGattCharacteristic,
        status: Int,
        value: ByteArray?
    ) {
        val key = characteristic.uuid.toString()
        if (status == BluetoothGatt.GATT_SUCCESS && value != null) {
            valueCache[key] = formatBytes(value)
        } else {
            valueCache[key] = "read_fail status=$status"
        }
        reading.set(false)
        gatt?.let { pumpRead(it) }
    }

    /**
     * Bouw volledige dump. [modeLabel] = bv. "pre-anc", "anc-on", "transparantie".
     */
    fun capture(context: Context, modeLabel: String = "snapshot"): String {
        val g = gatt
        val text = if (g == null) {
            "# Sounmax nRF-style dump\n# ERROR: geen actieve GATT-verbinding\n"
        } else {
            buildDump(g, modeLabel, context)
        }
        lastText = text
        try {
            File(context.filesDir, FILE).writeText(text)
            // append history
            File(context.filesDir, "nrf_gatt_history.txt").appendText(
                "\n\n===== ${ts()} $modeLabel =====\n$text\n"
            )
        } catch (e: Exception) {
            Log.w(TAG, "write file: ${e.message}")
        }
        Log.i(TAG, "dump ${text.length} chars label=$modeLabel")
        return text
    }

    fun share(context: Context, modeLabel: String = "snapshot") {
        val text = capture(context, modeLabel)
        try {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("sounmax-nrf-dump", text))
        } catch (_: Exception) {}
        try {
            context.startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Sounmax nRF GATT dump ($modeLabel)")
                    putExtra(Intent.EXTRA_TEXT, text)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.let {
                    Intent.createChooser(it, "Deel GATT-dump")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (_: Exception) {
            Toast.makeText(context, "Dump gekopieerd (${text.length} tekens)", Toast.LENGTH_SHORT).show()
        }
    }

    fun file(context: Context): File = File(context.filesDir, FILE)

    fun history(context: Context): String =
        runCatching { File(context.filesDir, "nrf_gatt_history.txt").readText() }.getOrDefault("")

    private fun pumpRead(g: BluetoothGatt) {
        if (!reading.compareAndSet(false, true)) return
        val next = readQueue.poll()
        if (next == null) {
            reading.set(false)
            return
        }
        val ok = runCatching { g.readCharacteristic(next) }.getOrDefault(false)
        if (!ok) {
            valueCache[next.uuid.toString()] = "read_not_queued"
            reading.set(false)
            pumpRead(g)
        }
    }

    private fun buildDump(g: BluetoothGatt, modeLabel: String, context: Context): String {
        val sb = StringBuilder()
        sb.appendLine("# Sounmax nRF-style GATT dump")
        sb.appendLine("# time: ${ts()}")
        sb.appendLine("# mode_label: $modeLabel")
        sb.appendLine("# device: ${safeName(g)}")
        sb.appendLine("# address: ${safeAddress(g)}")
        sb.appendLine("# services: ${g.services?.size ?: 0}")
        sb.appendLine("# note: druk ANC-knop tussen captures; vergelijk char-values")
        sb.appendLine()

        g.services.orEmpty().sortedBy { it.uuid.toString() }.forEach { svc ->
            appendService(sb, svc)
        }

        sb.appendLine()
        sb.appendLine("# --- VendorAncProbe summary ---")
        sb.appendLine(VendorAncProbe.dumpSummary())
        sb.appendLine()
        sb.appendLine("# --- RealAnc status ---")
        sb.appendLine(RealAncController.statusLine())
        return sb.toString()
    }

    private fun appendService(sb: StringBuilder, svc: BluetoothGattService) {
        val type = if (svc.type == BluetoothGattService.SERVICE_TYPE_PRIMARY) "PRIMARY" else "SECONDARY"
        sb.appendLine("Service: ${svc.uuid} ($type)")
        svc.characteristics.orEmpty().forEach { c ->
            val props = propsLabel(c.properties)
            val cached = valueCache[c.uuid.toString()]
            @Suppress("DEPRECATION")
            val live = c.value?.let { formatBytes(it) }
            val value = cached ?: live ?: "-"
            sb.appendLine("  Characteristic: ${c.uuid}")
            sb.appendLine("    Properties: $props (0x${c.properties.toString(16)})")
            sb.appendLine("    Value: $value")
            c.descriptors.orEmpty().forEach { d ->
                sb.appendLine("    Descriptor: ${d.uuid}")
                @Suppress("DEPRECATION")
                val dv = d.value?.let { formatBytes(it) } ?: "-"
                sb.appendLine("      Value: $dv")
                sb.appendLine("      Permissions: ${d.permissions}")
            }
        }
        sb.appendLine()
    }

    private fun propsLabel(props: Int): String {
        val parts = mutableListOf<String>()
        if (props and BluetoothGattCharacteristic.PROPERTY_BROADCAST != 0) parts += "BROADCAST"
        if (props and BluetoothGattCharacteristic.PROPERTY_READ != 0) parts += "READ"
        if (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) parts += "WRITE_NO_RESPONSE"
        if (props and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) parts += "WRITE"
        if (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) parts += "NOTIFY"
        if (props and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) parts += "INDICATE"
        if (props and BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE != 0) parts += "SIGNED_WRITE"
        if (props and BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS != 0) parts += "EXTENDED"
        return if (parts.isEmpty()) "NONE" else parts.joinToString(" | ")
    }

    private fun formatBytes(value: ByteArray): String {
        if (value.isEmpty()) return "(empty)"
        val hex = value.joinToString(" ") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
        val ascii = value.map { b ->
            val c = b.toInt() and 0xFF
            if (c in 32..126) c.toChar() else '.'
        }.joinToString("")
        return "$hex ($ascii) len=${value.size}"
    }

    private fun safeName(g: BluetoothGatt): String =
        try { g.device?.name ?: "?" } catch (_: SecurityException) { "?" }

    private fun safeAddress(g: BluetoothGatt): String =
        try { g.device?.address ?: "?" } catch (_: SecurityException) { "?" }

    private fun ts(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
}
