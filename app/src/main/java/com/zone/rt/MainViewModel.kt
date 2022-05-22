package com.zone.rt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var progress by mutableStateOf(0.0f)

    fun renderProgress() = progress
    fun addProgress() {
        progress += 0.2f
    }

    fun finishRender(): Boolean = progress >= 1.0f

}