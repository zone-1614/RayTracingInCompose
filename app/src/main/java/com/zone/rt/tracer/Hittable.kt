package com.zone.rt.tracer

class HitRecord {
    var p: Point3 = Point3(0.0, 0.0, 0.0)
    var normal: Vec3 = Vec3(0.0, 0.0, 0.0)
    lateinit var material: Material
    var t: Double = 0.0
    var frontFace = false

    fun setFaceNormal(ray: Ray, outwardNormal: Vec3) {
        frontFace = (ray.direction dot outwardNormal) < 0
        normal = if (frontFace) { outwardNormal } else { -outwardNormal }
    }
}

interface Hittable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Pair<Boolean, HitRecord>
}
