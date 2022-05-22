package com.zone.rt.tracer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun MakeColor(v: Vec3): Int {
    val color = Color(v.x.toFloat(), v.y.toFloat(), v.z.toFloat())
    return color.toArgb()
}
