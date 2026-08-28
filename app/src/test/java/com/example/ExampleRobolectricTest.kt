package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.dsp.BuiltinPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SoundMax", appName)
    }

    @Test
    fun `verify default presets and philips tah6519 profile`() {
        assertTrue(BuiltinPresets.PRESETS.isNotEmpty())
        val philipsDevice = BuiltinPresets.HEADPHONE_DEVICES.find { it.id == "philips_tah6519" }
        assertNotNull(philipsDevice)
        assertEquals("Philips TAH6519 ANC Over-Ear", philipsDevice?.name)
        assertEquals(10, BuiltinPresets.PRESETS[0].bandGains.size)
        
        val ytPreset = BuiltinPresets.PRESETS.find { it.name == "YouTube Music Bass Monster" }
        assertNotNull(ytPreset)
        assertEquals("Muziekgenres", ytPreset?.category)
    }

    @Test
    fun `verify ldac quality modes and force ldac dsp functionality`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dspManager = com.example.dsp.AudioDspManager(context)
        
        // Force LDAC 990 kbps
        dspManager.forceLdacCodec(com.example.dsp.LdacQualityMode.QUALITY_990)
        
        assertEquals(com.example.dsp.BluetoothCodec.LDAC, dspManager.selectedCodec.value)
        val metrics = dspManager.diagnosticMetrics.value
        assertEquals(990, metrics.currentBitrateKbps)
        assertEquals(96000, metrics.sampleRateHz)
        assertEquals(24, metrics.bitDepth)
        assertTrue(metrics.isLdacForced)
        
        // Optimize LDAC
        dspManager.optimizeLdacStreaming()
        assertTrue(dspManager.diagnosticMetrics.value.isOptimized)
        assertTrue(dspManager.diagnosticLogs.value.isNotEmpty())
        
        dspManager.release()
    }
}
