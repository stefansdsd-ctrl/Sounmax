package com.example.dsp

enum class AncMode(val displayName: String, val description: String, val noiseReductionDb: Int) {
    OFF("ANC Uit", "Standaard passieve isolatie", 0),
    STRONG("ANC Maximaal", "Maximale actieve ruisonderdrukking voor pendelen & kantoor", 32),
    ADAPTIVE("ANC Adaptief", "Past intensiteit dynamisch aan omgevingsgeluid aan", 24),
    AMBIENT("Transparantie / Alert", "Versterkt stemmen en verkeersgeluiden via microfoons", -15),
    WIND_GUARD("Windruis Filter", "Onderdrukt turbulentie bij buiten wandelen of fietsen", 18)
}

enum class BluetoothCodec(
    val codecName: String,
    val bitrateInfo: String,
    val sampleRateInfo: String,
    val isHiRes: Boolean,
    val latencyMs: Int
) {
    LDAC("Sony / Philips LDAC", "990 kbps (Hi-Res Master)", "96 kHz / 24-bit", true, 180),
    AAC("Apple / Android AAC", "320 kbps (VBR HQ)", "48 kHz / 16-bit", false, 120),
    APTX_HD("Qualcomm aptX HD", "576 kbps (High Def)", "48 kHz / 24-bit", true, 130),
    APTX_ADAPTIVE("aptX Adaptive", "279 - 420 kbps (Ultra-Low Latency)", "96 kHz / 24-bit", true, 80),
    SBC("Standard SBC", "328 kbps (Standaard)", "44.1 kHz / 16-bit", false, 220),
    LE_AUDIO("LC3 / LE Audio", "384 kbps (Next-Gen Bluetooth)", "48 kHz / 32-bit", true, 60)
}

enum class LdacQualityMode(
    val modeName: String,
    val bitrateKbps: Int,
    val sampleRateInfo: String,
    val description: String,
    val isBestQuality: Boolean
) {
    QUALITY_990("990 kbps (Hi-Res Master)", 990, "96 kHz / 24-bit", "Hoogste studiokwaliteit. Maximale dynamiek en transparant hoog.", true),
    BALANCED_660("660 kbps (Gebalanceerd)", 660, "96 kHz / 24-bit", "Ideale balans tussen detail en Bluetooth RF-signaalstabiliteit.", false),
    CONNECTION_330("330 kbps (Verbindingsprioriteit)", 330, "48 kHz / 24-bit", "Optimale storingsvrije stream in drukke draadloze omgevingen.", false),
    ADAPTIVE("Adaptief (Auto-Bitrate)", 990, "96 kHz / 24-bit", "Schakelt automatisch tussen 990, 660 en 330 kbps op basis van signaal.", false)
}

data class BluetoothDiagnosticMetrics(
    val connectionQualityPercent: Int = 98,
    val rssiDbm: Int = -42,
    val currentBitrateKbps: Int = 990,
    val sampleRateHz: Int = 96000,
    val bitDepth: Int = 24,
    val packetLossPercent: Float = 0.0f,
    val jitterMs: Float = 0.8f,
    val bufferHealthPercent: Int = 100,
    val codec: BluetoothCodec = BluetoothCodec.LDAC,
    val ldacMode: LdacQualityMode = LdacQualityMode.QUALITY_990,
    val isLdacForced: Boolean = true,
    val audioTrackActive: Boolean = true,
    val bluetoothMtuSize: Int = 1024,
    val a2dpProfileConnected: Boolean = true,
    val isOptimized: Boolean = true,
    val lastOptimizedMessage: String = "LDAC 990 kbps Master Stream actief"
)

data class HeadphoneDevice(
    val id: String,
    val name: String,
    val brand: String,
    val hasAnc: Boolean,
    val supportedCodecs: List<BluetoothCodec>,
    val batteryPercent: Int,
    val impedanceOhms: Int,
    val soundProfileSummary: String,
    val defaultPresetName: String
)

data class EqBand(
    val index: Int,
    val centerFreqHz: Int,
    val label: String,
    val gainDb: Float
)

data class EqPreset(
    val id: Long = 0,
    val name: String,
    val bandGains: List<Float>,
    val bassBoost: Int = 0,
    val virtualizer: Int = 0,
    val loudness: Int = 0,
    val clarity: Float = 0f,
    val isCustom: Boolean = false,
    val category: String = "Muziekgenres",
    val description: String = ""
) {
    fun toGainsString(): String = bandGains.joinToString(",")
}

data class ListeningScene(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val presetName: String,
    val ancMode: AncMode,
    val safeVolume: Boolean = false
)
