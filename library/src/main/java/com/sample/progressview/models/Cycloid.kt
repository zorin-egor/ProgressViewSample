package com.sample.progressview.models

import android.graphics.*
import kotlin.math.cos
import kotlin.math.sin

class Cycloid : Draw {

    companion object {
        private const val MAX_SPEED = Int.MAX_VALUE
        private const val MIN_SPEED = 0.0f
        private const val RADIUS_COEF = 0.35f
        private const val LINE_TIMER = 100
        private const val PARTICLE_DELTA = 0.4f
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
    var width: Int = 0
    var height: Int = 0
    var points = ArrayList<Particle>(count)

    private var totalSpeed: Float = 0f
    private var deltaSpeed: Float = 0.01f

    private var lineTimeUpdate: Long = 0
    private var lineShiftIndexUpdate: Int = 0
    private var lineTotalIndexUpdate: Int = 0

    private val isLineTimeUpdate: Boolean
        get() = System.currentTimeMillis() - lineTimeUpdate > LINE_TIMER

    private val coefficient: Float
        get() = width.toFloat() / height.toFloat()

    private val center: Point
        get() = Point(width / 2, height / 2)

    private val radius: PointF
        get() = PointF(width.toFloat() * RADIUS_COEF, height.toFloat() * RADIUS_COEF * coefficient)

    private val linePaint: Paint = Paint().also { paint ->
        paint.color = Color.BLACK
        paint.strokeWidth = 2.0f
    }

    private val particlePaint: Paint = Paint().also { paint ->
        paint.color = Color.BLACK
    }

    init {
        initParticles()
    }

    override fun onDraw(canvas: Canvas) {

        var count = lineTotalIndexUpdate

        setSpeed()

        (0 until points.size / 2).forEach { index ->
            val firstIndex = index * 2
            val secondIndex = index * 2 + 1
            val first = points[firstIndex]
            val second = points[secondIndex]

            setParticle(first)
            setParticle(second)

            first.onDraw(canvas)
            second.onDraw(canvas)

            if (count-- > 0) {
                canvas.drawLine(first.x, first.y, second.x, second.y, linePaint)

                if (index > 0) {
                    val previous = points[index * 2 - 1]
                    canvas.drawLine(previous.x, previous.y, first.x, first.y, linePaint)
                }
            }
        }

        if (isLineTimeUpdate && lineTotalIndexUpdate <= points.lastIndex) {
            lineTimeUpdate = System.currentTimeMillis()
            ++lineTotalIndexUpdate
        }

        lineShiftIndexUpdate = if (lineShiftIndexUpdate >= points.lastIndex) 0 else lineShiftIndexUpdate + 1
    }

    private fun initParticles() {
        var delta = 0.0f
        var color = Color.argb(255, 255, 255, 255)
        repeat(count) {
            points.add(Particle(
                t = delta,
                paint = particlePaint
            ).also(::setParticle))
            delta += PARTICLE_DELTA
        }
    }

    private fun setSpeed() {
        deltaSpeed *= if (totalSpeed > MAX_SPEED || totalSpeed < MIN_SPEED) -1.0f else 1.0f
        totalSpeed += deltaSpeed
    }

    private fun setParticle(particle: Particle) {
        particle.x = center.x + radius.x * (cos(particle.t + totalSpeed) + cos(type.x1 * (particle.t + totalSpeed)) / type.y1)
        particle.y = center.y + radius.y * (sin(particle.t + totalSpeed) + sin(type.x2 * (particle.t + totalSpeed)) / type.y2)
    }

}