package com.sample.progressview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.sample.progressview.models.Cycloid

class ProgressView : View {

    private val particles = Cycloid()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            particles.width = width
            particles.height = height
            particles.onDraw(it)
//            invalidate()
            postInvalidateDelayed(10)
        }
    }
}