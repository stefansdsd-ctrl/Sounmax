package com.example.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Ringbuffer-crashlog + klembord-export voor GATT-debug. */
object CrashLog {
    private const val FILE = "sounmax-crash.log"
    private const val MAX_BYTES = 48_000

    fun install(context: Context) {
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            append(app, "FATAL ${t.name}", e)
            previous?.uncaughtException(t, e)
        }
    }

    fun append(context: Context, title: String, error: Throwable? = null) {
        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val sw = StringWriter()
        error?.printStackTrace(PrintWriter(sw))
        val block = buildString {
            appendLine("---- $stamp $title ----")
            appendLine("sdk=${Build.VERSION.SDK_INT} ${Build.MANUFACTURER} ${Build.MODEL}")
            if (error != null) appendLine(sw.toString())
            appendLine()
        }
        runCatching {
            val f = File(context.filesDir, FILE)
            f.appendText(block)
            if (f.length() > MAX_BYTES) {
                val keep = f.readText().takeLast(MAX_BYTES / 2)
                f.writeText(keep)
            }
        }
    }

    fun read(context: Context): String {
        val f = File(context.filesDir, FILE)
        if (!f.exists()) return "Geen crash-log."
        return f.readText()
    }

    fun copyToClipboard(context: Context) {
        val text = read(context)
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Sounmax crash-log", text))
        Toast.makeText(context, "Crash-log naar klembord", Toast.LENGTH_SHORT).show()
    }
}
