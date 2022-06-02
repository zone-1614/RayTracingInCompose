package com.zone.rt.tracer

import kotlin.math.tan

class Camera(
    lookFrom: Point3,
    lookAt: Point3,
    vup: Vec3,
    vfov: Double,
    aspectRatio: Double,
    aperture: Double,
    focusDist: Double
) {
    var origin: Point3
    var horizontal: Vec3
    var vertical: Vec3
    var lowerLeftCorner: Point3
    var lensRadius: Double
    var u: Vec3
    var v: Vec3
    var w: Vec3

    init {
        val theta = degreesToRadians(vfov)
        val h = tan(theta / 2)
        val viewportHeight = 2.0 * h
        val viewportWidth = viewportHeight * aspectRatio

        w = (lookFrom - lookAt).normalize()
        u = (vup cross w).normalize()
        v = w cross u

        origin = lookFrom
        horizontal = focusDist * viewportWidth * u
        vertical = focusDist * viewportHeight * v
        lowerLeftCorner = origin - horizontal / 2.0 - vertical / 2.0 - focusDist * w
        lensRadius = aperture / 2
    }

    fun getRay(s: Double, t: Double): Ray {
        val rd = lensRadius * Vec3.randomInUnitDisk()
        val offset = u * rd.x + v * rd.y
        return Ray(
            origin + offset,
            lowerLeftCorner + s * horizontal + t * vertical - origin - offset
        )
    }
}