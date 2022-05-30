package com.zone.rt.tracer

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

class Vec3(var x: Double, var y: Double, var z: Double) {
    constructor() : this(0.0, 0.0, 0.0)

    fun length(): Double = sqrt(lengthSquared())

    fun lengthSquared(): Double = x * x + y * y + z * z

    fun nearZero(): Boolean {
        val s = 1e-8
        return (abs(x) < s) && (abs(y) < s) && (abs(z) < s)
    }

    companion object {
        fun random(): Vec3 = Vec3(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
        fun random(min: Double, max: Double): Vec3 {
            return Vec3(
                Random.nextDouble(min, max),
                Random.nextDouble(min, max),
                Random.nextDouble(min, max)
            )
        }
        fun randomInUnitSphere(): Vec3 {
            while (true) {
                val p = random(-1.0, 1.0)
                if (p.lengthSquared() >= 1) continue
                return p
            }
        }
        fun randomUnitVector(): Vec3 {
            return randomInUnitSphere().normalize()
        }
        fun randomInHemisphere(normal: Vec3): Vec3 {
            val random = randomInUnitSphere()
            return if (normal dot random > 0.0) {
                random
            } else {
                -random
            }
        }
        fun reflect(v: Vec3, n: Vec3): Vec3 {
            return v - n * (2 * (v dot n))
        }
    }

    operator fun unaryMinus() = Vec3(-x, -y, -z)

    operator fun plus(rhs: Vec3) = Vec3(x + rhs.x, y + rhs.y, z + rhs.z)

    operator fun minus(rhs: Vec3) = Vec3(x - rhs.x, y - rhs.y, z - rhs.z)

    operator fun times(rhs: Vec3) = Vec3(x * rhs.x, y * rhs.y, z * rhs.z)

    operator fun times(t: Double) = Vec3(x * t, y * t, z * t)

    operator fun div(t: Double) = Vec3(x / t, y / t, z / t)

    infix fun dot(v: Vec3) = x * v.x + y * v.y + z * v.z

    infix fun cross(v: Vec3) = Vec3(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    fun normalize() = this / length()

    operator fun plusAssign(rhs: Vec3) {
        x += rhs.x
        y += rhs.y
        z += rhs.z
    }

    operator fun minusAssign(rhs: Vec3) {
        x -= rhs.x
        y -= rhs.y
        z -= rhs.z
    }

    operator fun timesAssign(rhs: Vec3) {
        x *= rhs.x
        y *= rhs.y
        z *= rhs.z
    }

    operator fun divAssign(rhs: Vec3) {
        x /= rhs.x
        y /= rhs.y
        z /= rhs.z
    }
}

typealias Color3 = Vec3
typealias Point3 = Vec3