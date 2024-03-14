package com.sample.progressview.models.particle

import android.graphics.Canvas
import android.graphics.Paint
import com.sample.progressview.models.Draw

internal data class Particle(
    var x: Float,
    var y: Float,
    var radius: Float,
    val delta: Float,
    var paint: Paint
) : Draw {

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }

}