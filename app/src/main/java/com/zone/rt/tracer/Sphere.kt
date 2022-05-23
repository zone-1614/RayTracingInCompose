package com.zone.rt.tracer

import kotlin.math.sqrt

class Sphere(val center: Point3, val radius: Double) : Hittable {
    constructor() : this(Point3(0.0, 0.0, 0.0), 0.0)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Pair<Boolean, HitRecord> {
        val record = HitRecord()
        val oc = ray.origin - center
        val a = ray.direction.lengthSquared()
        val halfB = oc dot ray.direction
        val c = oc.lengthSquared() - radius * radius

        val delta = halfB * halfB - a * c
        if (delta < 0) {
            return Pair(false, record)
        }
        val sqrtd = sqrt(delta)
        var root = (-halfB - sqrtd) / a
        if (root < tMin || tMax < root) {
            root = (-halfB + sqrtd) / a
            if (root < tMin || tMax < root)
                return Pair(false, record)
        }

        record.apply {
            t = root
            p = ray.at(t)
            val outwardNormal = (p - center) / radius
            setFaceNormal(ray, outwardNormal)
        }


        return Pair(true, record)
    }
}