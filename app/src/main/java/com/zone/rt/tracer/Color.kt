package com.zone.rt.tracer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun MakeColor(v: Vec3): Int {
    val color = Color(red = v.x.toInt(), green = v.y.toInt(), blue = v.z.toInt())
    return color.toArgb()
}
