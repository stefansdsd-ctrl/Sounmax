package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
        DspControlService.start(this)
        val wellness = getSharedPreferences("soundmax_wellness", MODE_PRIVATE)
        volumeCycler = VolumeSceneCycler(this) {
            SoundMaxWidget.cycleScene(this, +1)
        }.also {
            it.setEnabled(wellness.getBoolean("volume_scene", true))
            it.start()
        }
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

    override fun onDestroy() {
        volumeCycler?.stop()
        super.onDestroy()
    }
}
