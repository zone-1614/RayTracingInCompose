package com.zone.rt.tracer

class Ray(var origin: Point3, var direction: Vec3) {
    constructor() : this(Point3(), Vec3())

    fun at(t: Double): Point3 {
        return origin + direction * t
    }
}