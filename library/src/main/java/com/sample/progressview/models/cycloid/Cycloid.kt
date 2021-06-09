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
    private val type: Cycloid.Type,
    private val from: Int,
    private val to: Int,
    private val start: Int,
    private val isBackgroundColorRandom: Boolean,
    private val isCountDown: Boolean,
    private @ColorInt var backgroundColor: Int = Color.TRANSPARENT,
    private @ColorInt var progressColor: Int = Color.TRANSPARENT
) : Draw {

    companion object {
        private const val COUNT = 110
        private const val LINE_TIMER = 100
        private const val PARTICLES_DELTA = 0.4f
        private const val RADIUS_MAX = 0.35f
        private const val RADIUS_MIN = 0.25f
        private const val RADIUS_DELTA = 0.001f
        private const val ALPHA_START = 255
        private const val COLOR_STEP = 1
        private const val DELTA_SPEED = 0.01f
    }

    enum class Type(val x1: Float, val y1: Float, val x2: Float, val y2: Float) {
        One(4.938000f, 2.456000f, 4.593000f, 4.913000f),
        Two(1.286000f, 4.242000f, 1.286000f, 4.242000f), // +
        Three(4.283176f, 3.518178f, 3.683177f, 3.668177f),
        Four(2.150000f, 2.299000f, 2.150000f, 2.299000f),
        Five(4.066366f, 3.216887f, 3.216887f, 4.066366f),
        Six(3.104000f, 4.681000f, 3.104000f, 4.681000f), // +
        Seven(3.019000f, 2.913000f, 3.019000f, 2.913000f), // +
        Eight(3.216887f, 4.066366f, 3.216887f, 4.066366f), // +
        Nine(1.870000f, 2.955000f, 0.935000f, 3.385000f), // +
        Ten(4.216887f, 4.216887f, 5.066366f, 5.066366f),
    }

    private val points = ArrayList<Particle>(COUNT)
    private var totalSpeed: Float = 0.0f
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

    private val progressPaint: Paint = Paint().also { paint ->
        paint.color = progressColor
        paint.strokeWidth = 4.0f
    }

    private val headProgressPaint: Paint = Paint().also { paint ->
        paint.color = Color.RED
        paint.strokeWidth = 10.0f
    }

    private val testPaint: Paint = Paint().also { paint ->
        paint.color = Color.RED
        paint.strokeWidth = 20.0f
    }

    private fun setParticles() {
        var delta = 0.0f
        var colorValue = 255
        var alphaValue = ALPHA_START
        val colorStep = COLOR_STEP
        val alphaStep = COLOR_STEP

        (0 until COUNT).forEach { index ->
            points.add(Particle(
                delta = delta,
                paint = Paint().apply {
                    this.color = Color.argb(alphaValue, 170, colorValue, 128)
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
        deltaRadius *= if (totalRadius > RADIUS_MAX || totalRadius < RADIUS_MIN) -1.0f else 1.0f
        totalRadius += deltaRadius
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
                    particle.paint.set(progressPaint)
                    particle.onDraw(canvas)
                    canvas.drawLine(previous.x, previous.y, particle.x, particle.y, progressPaint)
                }
            }
        }

        // Draw close line
        if (points.size > 2 && indexProgress >= points.lastIndex) {
            points.first().paint.set(progressPaint)
            canvas.drawLine(points.first().x, points.first().y, points.last().x, points.last().y, progressPaint)
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