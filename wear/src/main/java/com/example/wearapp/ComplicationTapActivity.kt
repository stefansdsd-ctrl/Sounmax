package com.example.wearapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ComplicationTapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            runCatching { WearClient.send(this@ComplicationTapActivity, WearPaths.CMD_NEXT_SCENE) }
            finish()
        }
    }
}
