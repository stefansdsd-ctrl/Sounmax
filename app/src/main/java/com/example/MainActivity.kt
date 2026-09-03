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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.media.CallTransparencyGuard
import com.example.media.DspControlService
import com.example.media.VolumeSceneCycler
import com.example.ui.MainViewModel
import com.example.ui.SoundMaxApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioDarkBackground
import com.example.widget.SoundMaxWidget

class MainActivity : ComponentActivity() {
    private var volumeCycler: VolumeSceneCycler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSceneIntent(intent)
        DspControlService.start(this)
        CallTransparencyGuard.attach(this)
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StudioDarkBackground
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
        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 42)
        }
    }

    override fun onDestroy() {
        volumeCycler?.stop()
        super.onDestroy()
    }
}
