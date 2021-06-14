package com.sample.progressview.models.cycloid

import android.graphics.*
import android.os.Parcelable
import androidx.annotation.ColorInt
import com.sample.progressview.models.Draw
import com.sample.progressview.models.particle.Particle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class Cycloid(
    private val type: Type,
    private val lineWidth: Int,
    private val particleRadius: Int,
    private val start: Int = 0,
    private val to: Int = 100,
    private val from: Int = start,
    private val isBackgroundColorRandom: Boolean = false,
    private val isCountDown: Boolean = false,
    private val isDynamicRadius: Boolean = true,
    @ColorInt private val backgroundColor: Int = Color.LTGRAY,
    @ColorInt private val progressColor: Int = Color.DKGRAY
) : Draw {

    companion object {
        private const val LINE_TIMER = 100
        private const val PARTICLES_DELTA = 0.4f
        private const val RADIUS_MAX = 0.30f
        private const val RADIUS_MIN = 0.20f
        private const val RADIUS_DELTA = 0.001f
        private const val ALPHA_START = 255
        private const val COLOR_STEP = 1
        private const val DELTA_SPEED = 0.01f
        private const val LINE_WIDTH_FACTOR = 2.0f
        private const val PARTICLE_RADIUS_FACTOR = 1.5f
    }

    enum class Type(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val count: Int) {
        One(1.286000f, 4.242000f, 1.286000f, 4.242000f, 110),
        Two(4.283176f, 3.518178f, 3.683177f, 3.668177f, 110),
        Three(2.150000f, 2.299000f, 2.150000f, 2.299000f, 110),
        Four(3.216887f, 4.066366f, 3.216887f, 4.066366f, 63),
        Five(1.745000f, 1.575000f, 1.745000f, 1.575000f, 63),
    }

    private val points = ArrayList<Particle>(type.count)
    private var totalSpeed: Float = 0f
    private var deltaSpeed: Float = DELTA_SPEED
    private var totalRadius: Float = RADIUS_MAX
    private var deltaRadius: Float = RADIUS_DELTA
    private var timeProgress: Long = 0
    private var indexProgress: Int = 0

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

    private val backgroundPaint: Paint = Paint().apply {
        color = backgroundColor
        strokeWidth = lineWidth.toFloat()
    }

    private val progressPaint: Paint = Paint().apply {
        color = if (isCountDown) Color.TRANSPARENT else progressColor
        strokeWidth = lineWidth * LINE_WIDTH_FACTOR
    }

    private val testPaint: Paint = Paint().apply {
        color = Color.RED
        strokeWidth = 20.0f
    }

    private fun setParticles() {
        var delta = 0.0f
        var colorValue = 255
        var alphaValue = ALPHA_START
        val colorStep = COLOR_STEP
        val alphaStep = COLOR_STEP

        (0 until type.count).forEach { index ->
            points.add(Particle(
                x = 0.0f, y = 0.0f,
                radius = particleRadius.toFloat(),
                delta = delta,
                paint = if (isBackgroundColorRandom) Paint().apply {
                    this.color = Color.argb(alphaValue, 170, colorValue, 128)
                } else {
                    backgroundPaint
                }
            ).also(::setParticleXY))

            delta += PARTICLES_DELTA
            colorValue += if (colorValue > 255 / 2) colorValue - colorStep else colorValue
            alphaValue = if (alphaValue > 255 / 2) alphaValue - alphaStep else alphaValue
        }
    }

    private fun setSpeed() {
        if (abs(totalSpeed - 2 * PI) < 0.001) totalSpeed = 0.0f
        totalSpeed += deltaSpeed
    }

    private fun setRadius() {
        if (isDynamicRadius) {
            deltaRadius *= if (totalRadius > RADIUS_MAX || totalRadius < RADIUS_MIN) -1.0f else 1.0f
            totalRadius += deltaRadius
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun setParticleXY(particle: Particle) {
        particle.x = center.x + radius.x * (cos(particle.delta + totalSpeed) +
                cos(type.x1 * (particle.delta + totalSpeed)) / type.y1)
        particle.y = center.y + radius.y * (sin(particle.delta + totalSpeed) +
                sin(type.x2 * (particle.delta + totalSpeed)) / type.y2)
    }

    private fun setProgress() {
        if (isTimeProgress && indexProgress < points.lastIndex) {
            timeProgress = System.currentTimeMillis()
            ++indexProgress
        }
    }

    private fun draw(canvas: Canvas) {
        // Draw close line under first
        if (points.size > 2) {
            canvas.drawLine(points.first().x, points.first().y, points.last().x, points.last().y, points.last().paint)
            if (indexProgress >= points.lastIndex) {
                canvas.drawLine(points.first().x, points.first().y, points.last().x, points.last().y, progressPaint)
            }
        }

        // Draw main particles and lines
        points.forEachIndexed { index, particle ->
            setParticleXY(particle)
            particle.onDraw(canvas)

            if (index > 0) {
                // Draw connect line
                val previous = points[index - 1]
                previous.paint.strokeWidth = 5.0f
                previous.paint.shader = LinearGradient(
                    previous.x, previous.y,
                    particle.x, particle.y,
                    previous.paint.color, particle.paint.color,
                    Shader.TileMode.CLAMP
                )
                canvas.drawLine(previous.x, previous.y, particle.x, particle.y, previous.paint)

                // Draw progress
                if (index in 1..indexProgress) {
//                    progressPaint.shader = LinearGradient(
//                        previous.x, previous.y,
//                        particle.x, particle.y,
//                        previous.paint.color, particle.paint.color,
//                        Shader.TileMode.CLAMP
//                    )
                    previous.paint = progressPaint
                    previous.radius = particleRadius * PARTICLE_RADIUS_FACTOR
                    previous.onDraw(canvas)
                    particle.paint = progressPaint
                    particle.radius = particleRadius * PARTICLE_RADIUS_FACTOR
                    particle.onDraw(canvas)
                    canvas.drawLine(previous.x, previous.y, particle.x, particle.y, progressPaint)
                }
            }
        }
    }

    override fun onSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        setParticles()
    }

    override fun onSave(): Parcelable? {
        return CycloidState(
            totalSpeed = totalSpeed,
            deltaSpeed = deltaSpeed,
            totalRadius = totalRadius,
            deltaRadius = deltaRadius,
            timeProgress = timeProgress,
            indexProgress = indexProgress
        )
    }

    override fun onRestore(state: Parcelable?) {
        if (state is CycloidState) {
            totalSpeed = state.totalSpeed
            deltaSpeed = state.deltaSpeed
            totalRadius = state.totalRadius
            deltaRadius = state.deltaRadius
            timeProgress = state.timeProgress
            indexProgress = state.indexProgress
        }
    }

    override fun onDraw(canvas: Canvas) {
        setSpeed()
        setRadius()
        draw(canvas)
        setProgress()
    }

}