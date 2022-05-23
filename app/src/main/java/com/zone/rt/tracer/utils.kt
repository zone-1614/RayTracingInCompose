package com.zone.rt.tracer

const val infinity = Double.MAX_VALUE
const val pi = kotlin.math.PI

fun degreesToRadians(degree: Double): Double = degree / 180 * pi

fun clamp(x: Double, min: Double, max: Double) = if (x < min) {
    min
} else if (x > max) {
    max
} else {
    x
}