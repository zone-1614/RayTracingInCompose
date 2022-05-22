package com.zone.rt.tracer

import kotlin.math.sqrt

class Vec3(var x: Double, var y: Double, var z: Double) {
    constructor() : this(0.0, 0.0, 0.0)

    fun length(): Double = sqrt(lengthSquared())

    fun lengthSquared(): Double = x * x + y * y + z * z

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