package com.zone.rt.tracer

data class ScatterResult(val scatter: Boolean, val scattered: Ray, val attenuation: Color3)

abstract class Material {
    abstract fun scatter(rayIn: Ray, rec: HitRecord, attenuation: Color3, scattered: Ray): ScatterResult
}

class Lambertian(val albedo: Color3) : Material() {
    override fun scatter(
        rayIn: Ray,
        rec: HitRecord,
        attenuation: Color3,
        scattered: Ray
    ): ScatterResult {
        var scatterDirection = rec.normal + Vec3.randomUnitVector()
        if (scatterDirection.nearZero()) {
            scatterDirection = rec.normal
        }
        return ScatterResult(true, Ray(rec.p, scatterDirection), albedo)
    }
}

class Metal(val albedo: Color3) : Material() {
    override fun scatter(
        rayIn: Ray,
        rec: HitRecord,
        attenuation: Color3,
        scattered: Ray
    ): ScatterResult {
        val reflected = Vec3.reflect(rayIn.direction.normalize(), rec.normal)
        val _scattered = Ray(rec.p, reflected)
        return ScatterResult(
            (_scattered.direction dot rec.normal) > 0,
            _scattered,
            albedo
        )
    }
}