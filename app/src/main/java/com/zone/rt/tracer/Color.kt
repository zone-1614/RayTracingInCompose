package com.zone.rt.tracer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun MakeColor(v: Vec3, samples: Int): Int {
    var r = v.x
    var g = v.y
    var b = v.z
    val scale = 1.0 / samples
    r *= scale
    g *= scale
    b *= scale
    val color = Color(r.toFloat(), g.toFloat(), b.toFloat())
    return color.toArgb()
}
