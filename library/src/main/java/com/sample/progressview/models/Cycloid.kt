package com.sample.progressview.models

import android.graphics.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Cycloid : Draw {

    companion object {
        private const val LINE_TIMER = 100
        private const val PARTICLES_DELTA = 0.4f
        private const val RADIUS_MAX = 0.35f
        private const val RADIUS_MIN = 0.25f
        private const val RADIUS_DELTA = 0.001f
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

    var count: Int = 110
    var type = Type.Ten
    var from: Int = 0
    var to: Int = 0
    var start: Int = 0
    var color: Int = 0

    private val points = ArrayList<Particle>(count)
    private var totalSpeed: Float = 0.0f
    private var deltaSpeed: Float = 0.01f
    private var totalRadius: Float = RADIUS_MAX
    private var deltaRadius: Float = RADIUS_DELTA

    private var lineTimeUpdate: Long = 0
    private var lineShiftIndexUpdate: Int = 0
    private var lineTotalIndexUpdate: Int = 0

    private var width: Int = 0
        get() = if (field > 0) field else throw IllegalStateException("Width must be more than zero")

    private var height: Int = 0
        get() = if (field > 0) field else throw IllegalStateException("Height must be more than zero")

    private val isLineTimeUpdate: Boolean
        get() = System.currentTimeMillis() - lineTimeUpdate > LINE_TIMER

    private val sizeCoefficient: Float
        get() = width.toFloat() / height.toFloat()

    private val center: Point
        get() = Point(width / 2, height / 2)

    private val radius: PointF
        get() = if (sizeCoefficient > 1.0) {
            PointF(width.toFloat() * totalRadius / sizeCoefficient, height.toFloat() * totalRadius)
        } else {
            PointF(width.toFloat() * totalRadius, height.toFloat() * totalRadius * sizeCoefficient)
        }

    private val linePaint: Paint = Paint().also { paint ->
        paint.color = Color.BLACK
        paint.strokeWidth = 2.0f
    }

    private val testPaint: Paint = Paint().also { paint ->
        paint.color = Color.RED
        paint.strokeWidth = 20.0f
    }

    private val particlePaint: Paint = Paint()

    private fun setParticles() {
        var delta = 0.0f
        val colorStep = 255 / count
        var colorValue = 0
        val alphaStep = 1
        var alphaValue = 255

        repeat(count) { index ->
            points.add(Particle(
                delta = delta,
                paint = Paint().apply {
                    this.color = Color.argb(alphaValue, 170, colorValue, 128)
                }
            ).also(::setParticle))

            delta += PARTICLES_DELTA
            colorValue += colorStep
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

    private fun setParticle(particle: Particle) {
        particle.x = center.x + radius.x * (cos(particle.delta + totalSpeed) + cos(type.x1 * (particle.delta + totalSpeed)) / type.y1)
        particle.y = center.y + radius.y * (sin(particle.delta + totalSpeed) + sin(type.x2 * (particle.delta + totalSpeed)) / type.y2)
    }

    override fun onSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        setParticles()
    }

    override fun onDraw(canvas: Canvas) {

        var count = lineTotalIndexUpdate

        setSpeed()
        setRadius()

        repeat(points.size / 2) { index ->
            val firstIndex = index * 2
            val secondIndex = index * 2 + 1
            val first = points[firstIndex]
            val second = points[secondIndex]

            setParticle(first)
            setParticle(second)

            first.onDraw(canvas)
            second.onDraw(canvas)

            if (count-- > 0) {
                canvas.drawLine(first.x, first.y, second.x, second.y, first.paint)

                if (index > 0) {
                    val previous = points[index * 2 - 1]
                    canvas.drawLine(previous.x, previous.y, first.x, first.y, previous.paint)
                }
            }
        }

        if (points.size % 2 != 0) {
            val last = points.last()
            setParticle(last)
            last.onDraw(canvas)

            if (points.lastIndex > 0) {
                val previous = points[points.lastIndex - 1]
                canvas.drawLine(last.x, last.y, previous.x, previous.y, previous.paint)
            }
        }

        if (points.size > 2 && lineTotalIndexUpdate >= points.lastIndex) {
            val first = points.first()
            val last = points.last()
            canvas.drawLine(first.x, first.y, last.x, last.y, testPaint)
        }

        if (isLineTimeUpdate && lineTotalIndexUpdate < points.lastIndex) {
            lineTimeUpdate = System.currentTimeMillis()
            ++lineTotalIndexUpdate
        }

        lineShiftIndexUpdate = if (lineShiftIndexUpdate >= points.lastIndex) 0 else lineShiftIndexUpdate + 1
    }

}