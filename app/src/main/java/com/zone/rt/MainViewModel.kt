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
import kotlin.random.Random

class MainViewModel : ViewModel() {
    // Image
    val aspectRatio = 16.0f / 9.0f
    val imageHeight = 256
    val imageWidth = (aspectRatio * imageHeight).toInt()
    val samples = 10
    val maxDepth = 30

    // Camera
    val camera = Camera()

    // world
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
            for (x in 0 until imageWidth) {
                val color = Color3()
                repeat(samples) {
                    val u = (x.toDouble() + Random.nextDouble(1.0)) / (imageWidth - 1)
                    val v = 1 - ((y.toDouble() + Random.nextDouble(1.0)) / (imageHeight - 1))
                    val ray = camera.getRay(v, u)
                    color.plusAssign(rayColor(ray, world, maxDepth))
                }
                bitmap[x, y] = MakeColor(color, samples)
            }
        }
    }

    fun refresh() {
        bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
    }

    fun rayColor(ray: Ray, world: HittableList, depth: Int): Color3 {
        if (depth <= 0) return Color3()
        val pair = world.hit(ray, 0.0, infinity)
        val rec = pair.second
        if (pair.first) {
            val target = rec.p + rec.normal + Vec3.randomInUnitSphere()
            return rayColor(Ray(rec.p, target - rec.p), world, depth - 1) * 0.5
        }
        val dir = ray.direction.normalize()
        val t = 0.5 * (dir.y + 1)
        return Color3(1.0, 1.0, 1.0) * (1.0 - t) + Color3(0.5, 0.7, 1.0) * t
    }
}