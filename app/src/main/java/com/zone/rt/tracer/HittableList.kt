package com.zone.rt.tracer

class HittableList() : Hittable {
    constructor(hittable: Hittable) : this() {
        add(hittable)
    }

    private val objects: ArrayList<Hittable> = ArrayList()

    fun add(hittable: Hittable) {
        objects.add(hittable)
    }

    fun clear() {
        objects.clear()
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Pair<Boolean, HitRecord> {
        var record = HitRecord()
        var hitAnything = false
        var closestSoFar = tMax

        objects.forEach { obj ->
            val pair = obj.hit(ray, tMin, closestSoFar)
            if (pair.first) {
                hitAnything = true
                closestSoFar = pair.second.t
                record = pair.second
            }
        }
        return Pair(hitAnything, record)
    }
}