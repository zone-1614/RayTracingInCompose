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
import kotlin.math.cos
import kotlin.random.Random

class MainViewModel : ViewModel() {
    // Image
    val aspectRatio = 3.0f / 2.0f
    val imageWidth = 500
    val imageHeight = (imageWidth / aspectRatio).toInt()
    val samples = 50
    val maxDepth = 30

    // Camera
    val lookFrom = Point3(13.0, 2.0, 3.0)
    val lookAt = Point3(0.0, 0.0, 0.0)
    val vup = Vec3(0.0, 1.0, 0.0)
    val distToFocus = 10.0
    val aperture = 0.1
    val camera = Camera(lookFrom, lookAt, vup, 20.0, aspectRatio.toDouble(), aperture, distToFocus)

    // world
    var world = finalScene()

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
                        val ray = camera.getRay(u, v)
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

    private fun finalScene(): HittableList {
        val hittableList = HittableList()
        val groundMaterial = Lambertian(Color3(0.5, 0.5, 0.5))
        hittableList.add(Sphere(Point3(0.0, -1000.0, 0.0), 1000.0, groundMaterial))
        for (a in -3..3) {
            for (b in -3..3) {
                val chooseMat = Random.nextDouble(0.0, 1.0)
                val center = Point3(a + 0.9 * Random.nextDouble(0.0, 1.0), 0.2, b + 0.9 * Random.nextDouble(0.0, 1.0))
                if ((center - Point3(4.0, 0.2, 0.0)).length() > 0.9) {
                    if (chooseMat < 0.8) {
                        val albedo = Color3.random() * Color3.random()
                        val diffuse = Lambertian(albedo)
                        hittableList.add(Sphere(center, 0.2, diffuse))
                    } else if (chooseMat < 0.95) {
                        val albedo = Color3.random(0.5, 1.0)
                        val fuzz = Random.nextDouble(0.0, 0.5)
                        val metal = Metal(albedo, fuzz)
                        hittableList.add(Sphere(center, 0.2, metal))
                    } else {
                        val glass = Dielectric(1.5)
                        hittableList.add(Sphere(center, 0.2, glass))
                    }
                }
            }
        }
        val material1 = Dielectric(1.5)
        val material2 = Lambertian(Color3(0.4, 0.2, 0.1))
        val material3 = Metal(Color3(0.7, 0.6, 0.5), 0.0)
        hittableList.add(Sphere(Point3(0.0, 1.0, 0.0), 1.0, material1))
        hittableList.add(Sphere(Point3(-4.0, 1.0, 0.0), 1.0, material2))
        hittableList.add(Sphere(Point3(4.0, 1.0, 0.0), 1.0, material3))
        return hittableList
    }
}