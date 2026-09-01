package com.example.dsp

object DspHolder {
    @Volatile
    var instance: AudioDspManager? = null
}
