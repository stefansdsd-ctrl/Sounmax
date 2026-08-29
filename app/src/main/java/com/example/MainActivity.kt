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
import com.example.ui.MainViewModel
import com.example.ui.SoundMaxApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioDarkBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DspControlService.start(this)
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
}
