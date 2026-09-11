package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.media.AncHaptics
import com.example.media.BtDisconnectPause
import com.example.media.CallModeGuard
import com.example.media.CallTransparencyGuard
import com.example.media.DspControlService
import com.example.media.ListenDoseTicker
import com.example.media.VolumeSceneCycler
import com.example.ui.MainViewModel
import com.example.ui.SoundMaxApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.appBackground
import com.example.widget.SoundMaxWidget
import com.example.data.CrashLog

class MainActivity : ComponentActivity() {
    private var volumeCycler: VolumeSceneCycler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrashLog.install(this)
        handleSceneIntent(intent)
        DspControlService.start(this)
        CallTransparencyGuard.attach(this)
        ListenDoseTicker.start(this)
        val wellnessPrefs = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        CallModeGuard.start(
            this,
            currentSceneId = { wellnessPrefs.getString("last_scene_id", "focus") },
            applyScene = { scene ->
                wellnessPrefs.edit()
                    .putString("last_scene_id", scene.id)
                    .putBoolean("pending_widget_scene", true)
                    .putBoolean("auto_scene", false)
                    .apply()
                SoundMaxWidget.applyScene(this, scene.id)
            }
        )
        BtDisconnectPause.register(this, wellnessPrefs.getString("headset_address", null))
        val wellness = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        volumeCycler = VolumeSceneCycler(
            this,
            onNextScene = { SoundMaxWidget.cycleScene(this, +1) },
            onPrevScene = { SoundMaxWidget.cycleScene(this, -1) }
        ).also {
            it.setEnabled(wellness.getBoolean("volume_scene", true))
            it.start()
        }
        requestRuntimePermissions()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val oled = AncHaptics.oledEnabled(LocalContext.current)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = appBackground(oled)
                ) {
                    val viewModel: MainViewModel = viewModel()
                    SoundMaxApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSceneIntent(intent)
    }

    private fun handleSceneIntent(intent: Intent?) {
        val sceneId = intent?.getStringExtra("scene_id") ?: return
        getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
            .edit()
            .putString("last_scene_id", sceneId)
            .putBoolean("pending_widget_scene", true)
            .putBoolean("auto_scene", false)
            .apply()
    }

    private fun requestRuntimePermissions() {
        val needed = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            needed += Manifest.permission.ACTIVITY_RECOGNITION
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            needed += Manifest.permission.ACCESS_COARSE_LOCATION
            needed += Manifest.permission.ACCESS_FINE_LOCATION
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            needed += Manifest.permission.READ_CALENDAR
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            needed += Manifest.permission.READ_PHONE_STATE
        }
        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 42)
        }
    }

    override fun onDestroy() {
        volumeCycler?.stop()
        super.onDestroy()
    }
}
