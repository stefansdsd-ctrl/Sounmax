package com.example.dsp

/**
 * Sidetone-sim / gespreksboost zonder headset-GATT.
 * Tilt 1–4 kHz omhoog, dempt rumble, lichte limiter.
 */
object ConversationBoost {
    private val SIDETONE = listOf(-3.0f, -2.4f, -1.2f, 0.6f, 2.0f, 3.2f, 3.6f, 2.4f, 0.6f, -0.8f)

    fun apply(on: Boolean) {
        StereoDynamics.init()
        if (on) {
            StereoDynamics.applyBands(SIDETONE, SIDETONE)
            StereoDynamics.speechBoost(true)
            StereoDynamics.safeLimiter(true)
        } else {
            StereoDynamics.speechBoost(false)
        }
    }
}
