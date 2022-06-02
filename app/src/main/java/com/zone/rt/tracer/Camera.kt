package com.zone.rt.tracer

import kotlin.math.tan

class Camera(
    lookFrom: Point3,
    lookAt: Point3,
    vup: Vec3,
    vfov: Double,
    aspectRatio: Double
) {
    var origin: Point3
    var horizontal: Vec3
    var vertical: Vec3
    var lowerLeftCorner: Point3

    init {
        val theta = degreesToRadians(vfov)
        val h = tan(theta / 2)
        val viewportHeight = 2.0 * h
        val viewportWidth = viewportHeight * aspectRatio

        val w = (lookFrom - lookAt).normalize()
        val u = (vup cross w).normalize()
        val v = w cross u

        origin = lookFrom
        horizontal = viewportWidth * u
        vertical = viewportHeight * v
        lowerLeftCorner = origin - horizontal / 2.0 - vertical / 2.0 - w
    }

    fun getRay(u: Double, v: Double): Ray = Ray(origin, lowerLeftCorner + horizontal * u + vertical * v - origin)
}