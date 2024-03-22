package com.sample.progressview

import android.content.Context
import android.graphics.Canvas
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.sample.progressview.models.Shape
import com.sample.progressview.models.cycloid.Cycloid
import com.sample.progressview.models.cycloid.CycloidColors
import com.sample.progressview.models.cycloid.getFiveTypeCycloid
import com.sample.progressview.models.cycloid.getFourTypeCycloid
import com.sample.progressview.models.cycloid.getOneTypeCycloid
import com.sample.progressview.models.cycloid.getThreeTypeCycloid
import com.sample.progressview.models.cycloid.getTwoTypeCycloid
import com.sample.progressview.models.toColorModel


class ProgressView : View {

    var isShapeDynamic: Boolean = true

    private var shape: Shape

    init {
        if (id == NO_ID) throw IllegalArgumentException("You must set the id to work correctly")
        isSaveEnabled = true
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        var lineWidth = context.resources.getDimensionPixelSize(R.dimen.progress_view_line_width)
        var particleRadius = context.resources.getDimensionPixelSize(R.dimen.progress_view_circle_radius)
        var to = context.resources.getInteger(R.integer.progress_view_to)
        var from = context.resources.getInteger(R.integer.progress_view_from)
        var backgroundColor = ContextCompat.getColor(context, R.color.progress_view_background)
        var defaultColor = ContextCompat.getColor(context, R.color.progress_view_default)
        var progressColor = ContextCompat.getColor(context, R.color.progress_view_progress)
        var isDynamicRadius = context.resources.getBoolean(R.bool.progress_view_dynamic_radius)
        var isDynamicColor = true
        var type = 0

        attrs?.let { context.obtainStyledAttributes(attrs, R.styleable.ProgressView) }
            ?.also {
                lineWidth = it.getInt(R.styleable.ProgressView_line_width, lineWidth)
                particleRadius = it.getInt(R.styleable.ProgressView_particle_radius, particleRadius)
                to = it.getInt(R.styleable.ProgressView_to_progress, to)
                from = it.getInt(R.styleable.ProgressView_from_progress, from)
                backgroundColor = it.getInt(R.styleable.ProgressView_color_background, backgroundColor)
                progressColor = it.getInt(R.styleable.ProgressView_color_progress, progressColor)
                defaultColor = it.getInt(R.styleable.ProgressView_color_shape, defaultColor)
                isDynamicRadius = it.getBoolean(R.styleable.ProgressView_dynamic_radius, isDynamicRadius)
                isDynamicColor = it.getBoolean(R.styleable.ProgressView_dynamic_color, isDynamicColor)
                isShapeDynamic = it.getBoolean(R.styleable.ProgressView_dynamic_shape, isShapeDynamic)
                type = it.getInt(R.styleable.ProgressView_type_progress, 0)
                it.recycle()
            }

        shape = when(type) {
            in 0 .. 4 -> {
                val colors = CycloidColors(
                    backgroundColor = backgroundColor.toColorModel,
                    defaultColor = defaultColor.toColorModel,
                    progressColor = progressColor.toColorModel
                )

                val shapeModel = when(type) {
                    0 -> getOneTypeCycloid(colors = colors, lineWidth = lineWidth.toFloat(), particleRadius = particleRadius.toFloat())
                    1 -> getTwoTypeCycloid(colors = colors, lineWidth = lineWidth.toFloat(), particleRadius = particleRadius.toFloat())
                    2 -> getThreeTypeCycloid(colors = colors, lineWidth = lineWidth.toFloat(), particleRadius = particleRadius.toFloat())
                    3 -> getFourTypeCycloid(colors = colors, lineWidth = lineWidth.toFloat(), particleRadius = particleRadius.toFloat())
                    4 -> getFiveTypeCycloid(colors = colors, lineWidth = lineWidth.toFloat(), particleRadius = particleRadius.toFloat())
                    else -> getOneTypeCycloid(colors = colors, lineWidth = lineWidth.toFloat(), particleRadius = particleRadius.toFloat())
                }

                Cycloid(
                    model = shapeModel,
                    fromProgress = from,
                    toProgress = to,
                    isRadiusDynamic = isDynamicRadius,
                    isShapeDynamic = isShapeDynamic,
                    isColorDynamic = isDynamicColor
                )
            }
            else -> throw IllegalArgumentException("Unknown shape type")
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return ProgressViewState(super.onSaveInstanceState(), shape.onSave())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(when(state) {
            is ProgressViewState -> {
                shape.onRestore(state.state)
                state.parcelable
            }
            else -> state
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shape.onSizeChanged(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let(shape::onDraw)
        if (isShapeDynamic) {
            postInvalidateDelayed(10)
        }
    }

    fun setShape(shape: Shape) {
        shape.onSizeChanged(width, height)
        invalidate()
    }

    fun setProgress(progress: Int) {
        shape.setProgress(progress)
        invalidate()
    }

}