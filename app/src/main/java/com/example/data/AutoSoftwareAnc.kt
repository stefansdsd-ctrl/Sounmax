package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Automatically enables Software ANC when ambient noise exceeds threshold.
 * Uses SoftwareAncController noiseLevelDb. Disables in quiet environments to save battery/mic.
 * Requires RECORD_AUDIO if Software ANC is used.
 */
class AutoSoftwareAnc(
    private val context: Context,
    private val controller: SoftwareAncController
) {
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val noiseThresholdDb = 65f // enable above this
    private val quietThresholdDb = 55f // disable below this (hysteresis)

    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            controller.noiseLevelDb.collectLatest { db ->
                val currentlyOn = controller.enabled.value
                if (!currentlyOn && db > noiseThresholdDb) {
                    controller.setEnabled(true)
                } else if (currentlyOn && db < quietThresholdDb) {
                    controller.setEnabled(false)
                }
                delay(2000) // avoid rapid toggling
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
