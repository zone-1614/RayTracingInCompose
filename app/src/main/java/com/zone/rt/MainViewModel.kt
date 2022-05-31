package com.zone.rt

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import com.zone.rt.tracer.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.random.Random

class MainViewModel : ViewModel() {
    // Image
    val aspectRatio = 16.0f / 9.0f
    val imageHeight = 256
    val imageWidth = (aspectRatio * imageHeight).toInt()
    val samples = 50
    val maxDepth = 30

    // Camera
    val camera = Camera()

    // material
    val materialGround = Lambertian(Color3(0.8, 0.8, 0.0))
    val materialCenter = Lambertian(Color3(0.7, 0.3, 0.3))
    val materialLeft = Dielectric(1.5)
    val materialRight = Metal(Color3(0.8, 0.6, 0.2), 1.0)

    // world
    var world = HittableList().apply {
        add(Sphere(Point3( 0.0, -100.5, -1.0), 100.0, materialGround))
        add(Sphere(Point3( 0.0,    0.0, -1.0),   0.5, materialCenter))
        add(Sphere(Point3(-1.0,    0.0, -1.0),   0.5, materialLeft))
        add(Sphere(Point3( 1.0,    0.0, -1.0),   0.5, materialRight))
    }

    var progress by mutableStateOf(imageHeight)
    private var _progress by mutableStateOf(AtomicInteger(imageHeight))

    fun finishRender(): Boolean = progress == 0

    var bitmap by mutableStateOf(Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888))

    private val threadPool = ArrayList<Thread>()
    fun draw() = thread {
        for (y in 0 until imageHeight) {
            val t = thread {
                for (x in 0 until imageWidth) {
                    val color = Color3()
                    repeat(samples) {
                        val u = (x.toDouble() + Random.nextDouble(1.0)) / (imageWidth - 1)
                        val v = 1 - ((y.toDouble() + Random.nextDouble(1.0)) / (imageHeight - 1))
                        val ray = camera.getRay(v, u)
                        color.plusAssign(rayColor(ray, world, maxDepth))
                    }
                    bitmap[x, y] = makeColor(color, samples)
                }
                progress = _progress.decrementAndGet()
            }
            threadPool.add(t)
        }
        threadPool.forEach { it.join() }
    }

    fun refresh() {
        bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        progress = imageHeight
        _progress.set(progress)
    }

    fun rayColor(ray: Ray, world: HittableList, depth: Int): Color3 {
        if (depth <= 0) return Color3()
        val pair = world.hit(ray, 0.001, infinity)
        val hit = pair.first
        val rec = pair.second
        if (hit) {
            val (scatter, scattered, attenuation) = rec.material.scatter(ray, rec, Color3(), Ray())
            if (scatter) {
                return attenuation * rayColor(scattered, world, depth - 1)
            }
            return Color3()
        }
        val dir = ray.direction.normalize()
        val t = 0.5 * (dir.y + 1)
        return Color3(1.0, 1.0, 1.0) * (1.0 - t) + Color3(0.5, 0.7, 1.0) * t
    }
}