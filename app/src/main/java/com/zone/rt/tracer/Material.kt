package com.zone.rt.tracer

import kotlin.math.min
import kotlin.math.sqrt

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

/**
 * @param f index of fuzz, will be clamp to leq 1.0
 */
class Metal(val albedo: Color3, f: Double) : Material() {
    var fuzz: Double = if (f < 1) { f } else { 1.0 }
    override fun scatter(
        rayIn: Ray,
        rec: HitRecord,
        attenuation: Color3,
        scattered: Ray
    ): ScatterResult {
        val reflected = Vec3.reflect(rayIn.direction.normalize(), rec.normal)
        val _scattered = Ray(rec.p, reflected + Vec3.randomInUnitSphere() * fuzz)
        return ScatterResult(
            (_scattered.direction dot rec.normal) > 0,
            _scattered,
            albedo
        )
    }
}

/**
 * @param ir index of refraction
 */
class Dielectric(val ir: Double) : Material() {
    override fun scatter(
        rayIn: Ray,
        rec: HitRecord,
        attenuation: Color3,
        scattered: Ray
    ): ScatterResult {
        val refractionRatio = if (rec.frontFace) { 1.0 / ir } else { ir }
        val unitVec = rayIn.direction.normalize()
        val cos_theta = min(-unitVec dot rec.normal, 1.0)
        val sin_theta = sqrt(1 - cos_theta * cos_theta)
        val cannotRefract = refractionRatio * sin_theta > 1.0
        val direction = if (cannotRefract) {
            Vec3.reflect(unitVec, rec.normal)
        } else {
            Vec3.refract(unitVec, rec.normal, refractionRatio)
        }
        return ScatterResult(true, Ray(rec.p, direction), Color3(1.0, 1.0, 1.0))
    }
}