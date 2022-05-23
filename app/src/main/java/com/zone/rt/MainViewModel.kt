package com.zone.rt

import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import com.zone.rt.tracer.*
import java.io.File
import java.io.FileOutputStream
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

    var world = HittableList().apply {
        add(Sphere(Point3(0.0, 0.0, -1.0), 0.5))
        add(Sphere(Point3(0.0, -100.5, -1.0), 100.0))
    }

    var progress by mutableStateOf(imageHeight)

    fun finishRender(): Boolean = progress == 0

    var bitmap by mutableStateOf(Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888))

    fun draw() = thread {
        for (y in 0 until imageHeight) {
            progress = imageHeight - y - 1
            Thread.sleep(1)
            for (x in 0 until imageWidth) {
                val u = x.toDouble() / (imageWidth - 1)
                val v = 1.0 - y.toDouble() / (imageHeight - 1)
                val ray = Ray(origin, lowerLeftCorner + horizontal * u + vertical * v - origin)
                val color = rayColor(ray, world)
                bitmap[x, y] = MakeColor(color)
            }
        }
    }

    fun refresh() {
        bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
    }

    fun rayColor(ray: Ray, world: HittableList): Color3 {
        val pair = world.hit(ray, 0.0, infinity)
        if (pair.first) {
            return (pair.second.normal + Color3(1.0, 1.0, 1.0)) * 0.5
        }
        val dir = ray.direction.normalize()
        val t = 0.5 * (dir.y + 1)
        return Color3(1.0, 1.0, 1.0) * (1.0 - t) + Color3(0.5, 0.7, 1.0) * t
    }

    fun hitSphere(center: Point3, radius: Double, ray: Ray): Double {
        val oc = ray.origin - center
        val a = ray.direction dot ray.direction
        val halfB = oc dot ray.direction
        val c = (oc dot oc) - radius * radius
        val delta = halfB * halfB - a * c
        return if (delta < 0) {
            -1.0
        } else {
            (-halfB - sqrt(delta)) / a
        }
    }
}