package com.sample.progressview.models

import android.graphics.Canvas
import android.graphics.Paint

data class Particle(
    var x: Float = 0.0f,
    var y: Float = 0.0f,
    var r: Float = 10.0f,
    val t: Float = 0.0f,
    val paint: Paint
) : Draw {

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(x, y, r, paint)
    }
}