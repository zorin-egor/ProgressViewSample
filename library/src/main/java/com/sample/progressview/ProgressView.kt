package com.sample.progressview

import android.content.Context
import android.graphics.Canvas
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.sample.progressview.models.Draw
import com.sample.progressview.models.cycloid.Cycloid


class ProgressView : View {

    private val cycloid: Draw

    init {
        if (id == NO_ID) throw IllegalArgumentException("You must set the id to work correctly")
        isSaveEnabled = true
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        var from = context.resources.getInteger(R.integer.progress_view_from)
        var to = context.resources.getInteger(R.integer.progress_view_to)
        var start = context.resources.getInteger(R.integer.progress_view_start)
        var backgroundColor = ContextCompat.getColor(context, R.color.progress_view_background)
        var progressColor = ContextCompat.getColor(context, R.color.progress_view_progress)
        var isBackgroundColorRandom = context.resources.getBoolean(R.bool.progress_view_color_random_background)
        var isCountDown = context.resources.getBoolean(R.bool.progress_view_count_down)
        var type = Cycloid.Type.One

        attrs?.let { context.obtainStyledAttributes(attrs, R.styleable.ProgressView) }
            ?.also {
                from = it.getInt(R.styleable.ProgressView_from_progress, from)
                to = it.getInt(R.styleable.ProgressView_to_progress, to)
                start = it.getInt(R.styleable.ProgressView_start_progress, start)
                backgroundColor = it.getInt(R.styleable.ProgressView_color_background, backgroundColor)
                progressColor = it.getInt(R.styleable.ProgressView_color_progress, progressColor)
                isBackgroundColorRandom = it.getBoolean(R.styleable.ProgressView_color_random_background, isBackgroundColorRandom)
                isCountDown = it.getBoolean(R.styleable.ProgressView_count_down, isCountDown)
                type = Cycloid.Type.values()
                    .getOrNull(it.getInt(R.styleable.ProgressView_type_progress, Cycloid.Type.One.ordinal))
                    ?: type
                it.recycle()
            }

        cycloid = Cycloid(
            type = type,
            from = from,
            to = to,
            start = start,
            isBackgroundColorRandom = isBackgroundColorRandom,
            isCountDown = isCountDown,
            backgroundColor = backgroundColor,
            progressColor = progressColor
        )
    }

    override fun onSaveInstanceState(): Parcelable {
        return ProgressViewState(super.onSaveInstanceState(), cycloid.onSave())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(when(state) {
            is ProgressViewState -> {
                cycloid.onRestore(state.state)
                state.parcelable
            }
            else -> state
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cycloid.onSizeChanged(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let(cycloid::onDraw)
        postInvalidateDelayed(10)
    }
}