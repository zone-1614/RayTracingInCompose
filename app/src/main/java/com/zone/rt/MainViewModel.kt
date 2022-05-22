package com.zone.rt

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import com.zone.rt.tracer.*
import kotlin.concurrent.thread
import kotlin.math.sqrt

class MainViewModel : ViewModel() {
    // Image
    var aspectRatio = 16.0f / 9.0f
    var imageHeight = 256
    var imageWidth = (aspectRatio * imageHeight).toInt()

    // Camera
    var viewportHeight = 2.0
    var viewportWidth = aspectRatio * viewportHeight
    var focalLength = 1.0

    var origin = Point3()
    var horizontal = Vec3(viewportWidth, 0.0, 0.0)
    var vertical = Vec3(0.0, viewportHeight, 0.0)
    var lowerLeftCorner = origin - horizontal / 2.0 - vertical / 2.0 - Vec3(0.0, 0.0, focalLength)

    var progress by mutableStateOf(imageHeight)

    fun renderProgress() = progress

    fun finishRender(): Boolean = progress == 0

    var bitmap by mutableStateOf(Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888))

    fun draw() = thread {
        for (y in 0 until imageHeight) {
            progress = imageHeight - y - 1
            Thread.sleep(10)
            for (x in 0 until imageWidth) {
                val u = x.toDouble() / (imageWidth - 1)
                val v = 1.0 - y.toDouble() / (imageHeight - 1)
                val ray = Ray(origin, lowerLeftCorner + horizontal * u + vertical * v - origin)
                val color = rayColor(ray)
                bitmap[x, y] = MakeColor(color)
            }
        }
    }

    fun scale(x: Int): Int {
        return x * 256 / imageWidth
    }

    fun refresh() {
        bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
    }

    fun rayColor(ray: Ray): Color3 {
        var t = hitSphere(Point3(0.0, 0.0, -1.0), 0.5, ray)
        if (t > 0.0) {
            val n = (ray.at(t) - Vec3(0.0, 0.0, -1.0)).normalize()
            return Color3(n.x + 1, n.y + 1, n.z + 1) * 0.5
        }
        val normal = ray.direction.normalize()
        t = 0.5 * (normal.y + 1.0)
        return Color3(1.0, 1.0, 1.0) * (1.0 - t) + Color3(0.5, 0.7, 1.0) * t
    }

    fun hitSphere(center: Point3, radius: Double, ray: Ray): Double {
        val oc = ray.origin - center
        val a = ray.direction dot ray.direction
        val b = oc dot ray.direction * 2.0
        val c = (oc dot oc) - radius * radius
        val delta = b * b - 4 * a * c
        return if (delta < 0) {
            -1.0
        } else {
            (-b - sqrt(delta)) / (2.0 * a)
        }
    }

}