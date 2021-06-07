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

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        attrs?.let { context.obtainStyledAttributes(attrs, R.styleable.ProgressView) }
            ?.let {
                particles.from = it.getInt(R.styleable.ProgressView_fromProgress, PROGRESS_FROM)
                particles.to = it.getInt(R.styleable.ProgressView_toProgress, PROGRESS_TO)
                particles.start = it.getInt(R.styleable.ProgressView_startProgress, PROGRESS_START)
                particles.color = it.getInt(R.styleable.ProgressView_colorProgress, PROGRESS_COLOR)
            }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        particles.onSizeChanged(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            particles.onDraw(it)
//            invalidate()
            postInvalidateDelayed(10)
        }
    }
}