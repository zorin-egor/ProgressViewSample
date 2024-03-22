package com.sample.progressview.models.cycloid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.os.Parcelable
import com.sample.progressview.models.ColorModel
import com.sample.progressview.models.Shape
import com.sample.progressview.models.particle.Particle
import com.sample.progressview.models.toIntColor
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Cycloid(
    private var model: CycloidModel,
    private var fromProgress: Int = 0,
    private var toProgress: Int = 100,
    private var isShapeDynamic: Boolean = true,
    private var isColorDynamic: Boolean = true,
    private var isRadiusDynamic: Boolean = false,
) : Shape {

    companion object {
        private const val LINE_TIMER = 100
        private const val PARTICLES_DELTA = 0.4f
        private const val RADIUS_MAX = 0.30f
        private const val RADIUS_MIN = 0.20f
        private const val RADIUS_DELTA = 0.001f
        private const val DELTA_SPEED = 0.01f
    }

    private val points = ArrayList<Particle>(toProgress)
    private var totalSpeed: Float = 0f
    private var deltaSpeed: Float = DELTA_SPEED
    private var totalRadius: Float = RADIUS_MAX
    private var deltaRadius: Float = RADIUS_DELTA
    private var timeProgress: Long = 0
    private var indexProgress: Int = fromProgress

    private var width: Int = 0
        get() = if (field > 0) field else throw IllegalStateException("Width must be more than zero")

    private var height: Int = 0
        get() = if (field > 0) field else throw IllegalStateException("Height must be more than zero")

    private val isTimeProgress: Boolean
        get() = System.currentTimeMillis() - timeProgress > LINE_TIMER

    private val sizeCoefficient: Float
        get() = width.toFloat() / height

    private val center: Point
        get() = Point(width / 2, height / 2)

    private val radius: PointF
        get() = if (sizeCoefficient > 1.0) {
            PointF(width.toFloat() * totalRadius / sizeCoefficient, height.toFloat() * totalRadius)
        } else {
            PointF(width.toFloat() * totalRadius, height.toFloat() * totalRadius * sizeCoefficient)
        }

    private val particleColor: ColorModel get() = model.colors.particleDefaultColor

    private val lineColor: ColorModel get() = model.colors.lineDefaultColor

    private val particleProgressColor: ColorModel get() = model.colors.particleProgressColor

    private val lineProgressColor: ColorModel get() = model.colors.lineProgressColor


    private val backgroundPaint: Paint = Paint().apply {
        color = model.colors.backgroundColor.toIntColor
    }

    private val linePaint: Paint = Paint().apply {
        color = lineColor.toIntColor
        strokeWidth = model.lineWidth
    }

    private val particlePaint: Paint = Paint().apply {
        color = particleColor.toIntColor
    }

    private fun setParticles() {
        var delta = 0.0f

        (0 until toProgress).forEach { index ->
            val particleColor = particleColor.addToRGBDynamic(
                factor = index.toFloat() / toProgress,
                isAlphaChannelAddOrSub = false
            )

            particlePaint.color = particleColor.toIntColor
            points.add(Particle(
                x = 0.0f, y = 0.0f,
                radius = model.particleRadius,
                delta = delta,
                paint = particlePaint
            ).also(::setParticleXY))

            delta += PARTICLES_DELTA
        }
    }

    private fun setSpeed() {
        if (abs(totalSpeed - 2 * PI) < 0.001) totalSpeed = 0.0f
        totalSpeed += deltaSpeed
    }

    private fun setRadius() {
        if (isRadiusDynamic) {
            deltaRadius *= if (totalRadius > RADIUS_MAX || totalRadius < RADIUS_MIN) -1.0f else 1.0f
            totalRadius += deltaRadius
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun setParticleXY(particle: Particle) {
        particle.x = center.x + radius.x * (cos(particle.delta + totalSpeed) +
                cos(model.x1 * (particle.delta + totalSpeed)) / model.y1)
        particle.y = center.y + radius.y * (sin(particle.delta + totalSpeed) +
                sin(model.x2 * (particle.delta + totalSpeed)) / model.y2)
    }

    private fun setProgress() {
        if (isTimeProgress && indexProgress < points.lastIndex) {
            timeProgress = System.currentTimeMillis()
            if (isShapeDynamic) {
                ++indexProgress
            }
        }
    }

    private fun draw(canvas: Canvas) {
        canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw close line under first
        if (points.size > 2) {
            val lineColor = if (indexProgress >= points.lastIndex)
                lineProgressColor.addToRGBDynamic(factor = 1.0f, isAlphaChannelAddOrSub = false)
                    else lineColor.addToRGBDynamic(factor = 1.0f)

            linePaint.color = lineColor.toIntColor

            canvas.drawLine(
                points.first().x, points.first().y,
                points.last().x, points.last().y,
                linePaint
            )
        }

        // Draw main particles and lines
        points.forEachIndexed { index, particle ->
            setParticleXY(particle)

            val factor = index.toFloat() / toProgress
            val radius = model.particleRadius
            var particleColor = particleColor.addToRGBDynamic(factor = factor)
            var lineColor = lineColor.addToRGBDynamic(factor = factor)

            if (index in 0..indexProgress) {
                particleColor = particleProgressColor.addToRGBDynamic(
                    factor = factor * 0.5f,
                    isAlphaChannelAddOrSub = false
                )
                lineColor = lineProgressColor.addToRGBDynamic(
                    factor = factor * 0.5f,
                    isAlphaChannelAddOrSub = false
                )
            }

            if (index < points.lastIndex) {
                linePaint.color = lineColor.toIntColor
                val next = points[index + 1]
                canvas.drawLine(next.x, next.y, particle.x, particle.y, linePaint)
            }

            particlePaint.color = particleColor.toIntColor
            particle.paint = particlePaint
            particle.radius = radius
            particle.onDraw(canvas)
        }
    }

    override fun onSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        setParticles()
    }

    override fun onSave(): Parcelable {
        return CycloidState(
            shapeModel = model,
            totalSpeed = totalSpeed,
            deltaSpeed = deltaSpeed,
            totalRadius = totalRadius,
            deltaRadius = deltaRadius,
            timeProgress = timeProgress,
            indexProgress = indexProgress,
            fromProgress = fromProgress,
            toProgress = toProgress,
            isShapeDynamic = isShapeDynamic,
            isColorDynamic = isColorDynamic,
            isRadiusDynamic = isRadiusDynamic
        )
    }

    override fun onRestore(state: Parcelable?) {
        if (state is CycloidState) {
            model = state.shapeModel as CycloidModel
            totalSpeed = state.totalSpeed
            deltaSpeed = state.deltaSpeed
            totalRadius = state.totalRadius
            deltaRadius = state.deltaRadius
            timeProgress = state.timeProgress
            indexProgress = state.indexProgress
            fromProgress = state.fromProgress
            toProgress = state.toProgress
            isShapeDynamic = state.isShapeDynamic
            isColorDynamic = state.isColorDynamic
            isRadiusDynamic = state.isRadiusDynamic
        }
    }

    override fun setProgress(value: Int) {
        if (value == indexProgress || value !in 0..toProgress) {
            return
        }
        indexProgress = value
    }

    override fun onDraw(canvas: Canvas) {
        setSpeed()
        setRadius()
        draw(canvas)
        setProgress()
    }

    private inline fun ColorModel.addToRGB(factor: Float, isAlphaChannelAddOrSub: Boolean? = null): ColorModel {
        val newRed = min(max(red + (255.0f - red) * factor, 0.0f), 255.0f)
        val newGreen = min(max(green + (255.0f - green) * factor, 0.0f), 255.0f)
        val newBlue = min(max( blue + (255.0f - blue) * factor, 0.0f), 255.0f)

        val newAlpha = when(isAlphaChannelAddOrSub) {
            true -> min(max( alpha + (255.0f - alpha) * factor, 0.0f), 255.0f)
            false -> min(max( alpha * (255.0f - factor), 0.0f), 255.0f)
            else -> alpha
        }

        return ColorModel(
            red = newRed.toInt(),
            green = newGreen.toInt(),
            blue = newBlue.toInt(),
            alpha = newAlpha.toInt()
        )
    }

    private inline fun ColorModel.addToRGBDynamic(factor: Float, isAlphaChannelAddOrSub: Boolean? = null): ColorModel {
        return if (isColorDynamic)
            addToRGB(factor = factor, isAlphaChannelAddOrSub = isAlphaChannelAddOrSub) else
            this
    }

}