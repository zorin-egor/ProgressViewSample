package com.sample.progressview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.sample.progressview.models.Cycloid


class ProgressView : View {

    companion object {
        private const val PROGRESS_FROM = 0
        private const val PROGRESS_TO = 100
        private const val PROGRESS_START = 0
        private const val PROGRESS_COLOR = Color.BLACK
    }

    private val particles = Cycloid()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.let { context.obtainStyledAttributes(attrs, R.styleable.ProgressView) }
             ?.let {
                particles.from = it.getInt(R.styleable.ProgressView_fromProgress, PROGRESS_FROM)
                particles.to = it.getInt(R.styleable.ProgressView_toProgress, PROGRESS_TO)
                particles.start = it.getInt(R.styleable.ProgressView_startProgress, PROGRESS_START)
                particles.color = it.getInt(R.styleable.ProgressView_colorProgress, PROGRESS_COLOR)
             }
    }

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