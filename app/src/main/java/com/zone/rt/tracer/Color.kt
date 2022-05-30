package com.zone.rt.tracer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.sqrt

fun makeColor(v: Vec3, samples: Int): Int {
    var r = v.x
    var g = v.y
    var b = v.z
    val scale = 1.0 / samples
    r = sqrt(scale * r)
    g = sqrt(scale * g)
    b = sqrt(scale * b)
    val color = Color(r.toFloat(), g.toFloat(), b.toFloat())
    return color.toArgb()
}
