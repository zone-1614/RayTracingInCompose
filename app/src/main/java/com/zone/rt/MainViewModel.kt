package com.zone.rt

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {
    var aspectRatio = 16.0f / 9.0f
    var imageHeight = 256
    var imageWidth = (aspectRatio * imageHeight).toInt()
    var progress by mutableStateOf(imageHeight)

    fun renderProgress() = progress

    fun finishRender(): Boolean = progress == 0

    var bitmap by mutableStateOf(Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888))

    fun draw() {
        thread {
            for (y in 0 until imageHeight) {
                progress = imageHeight - y - 1
                Thread.sleep(10)
                for (x in 0 until imageWidth) {
                    val color = Color(scale(x), y, y).toArgb()
                    bitmap[x, y] = color
                }
            }
        }
    }

    fun scale(x: Int): Int {
        return x * 256 / imageWidth
    }

    fun refresh() {
        bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
    }

}