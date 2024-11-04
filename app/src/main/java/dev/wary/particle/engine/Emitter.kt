package dev.wary.particle.engine

import kotlin.math.abs
import kotlin.random.Random


fun interface ParticleBuilder {
    fun build(source: Source): Particle
}

interface Emitter {
    val emitRate: Double
    fun emit(): Particle
}

fun interface Source {
    fun startPosition(): Point
}

class TemplateParticleBuilder(val template: Particle): ParticleBuilder {
    override fun build(source: Source) = Particle(
            lifeSpan = template.lifeSpan,
            width = template.width,
            height = template.height,
            position = source.startPosition().copy(),
            velocity = template.velocity.copy(),
            acceleration = template.acceleration.copy(),
            drawableResId = template.drawableResId,
            color = template.color,
            colorChange = template.colorChange,
            tint = template.tint,
            tintChange = template.tintChange,
            onEdgeCollision = template.onEdgeCollision,
            onParticleCollision = template.onParticleCollision
    )
}

class RangedParticleBuilder(val params: ParticleParams): ParticleBuilder {
    override fun build(source: Source): Particle {
        return Particle(
            lifeSpan = params.lifeSpan.value,
            width = params.width.value,
            height = params.height.value,
            position = source.startPosition().copy(),
            velocity = Point(params.vx.value, params.vy.value),
            acceleration = Point(params.ax.value, params.ay.value),
            drawableResId = params.drawableResIds?.value,
            color = params.color.value,
            colorChange = params.colorChange.value,
            tint = params.tint.value,
            tintChange = params.tintChange.value,
            onEdgeCollision = params.onEdgeCollision.value,
            onParticleCollision = params.onParticleCollision.value
        )
    }
}

class PointEmitter(
    val position: Point,
    val builder: ParticleBuilder,
    override val emitRate: Double,
): Rect(
    left = position.x,
    top = position.y,
    width = 1.0,
    height = 1.0
), Emitter, Source {
    override fun emit() = builder.build(this)
    override fun startPosition() = position
}

class LineEmitter(
    val start: Point,
    val end: Point,
    val builder: ParticleBuilder,
    override val emitRate: Double,
): Rect(
    left = start.x,
    top = start.y,
    width = abs(end.x - start.x),
    height = abs(end.y - start.y)
), Emitter, Source {
    override fun emit() = builder.build(this)

    override fun startPosition(): Point {
        if (end.x == start.x) {
            return Point(start.x, Random.nextDouble(minOf(start.y, end.y), maxOf(start.y, end.y) + 1))
        }

        // y = mx + b
        val m = (end.y - start.y) / (end.x - start.x)
        val b = start.y - m * start.x

        val x = Random.nextDouble(minOf(start.x, end.x), maxOf(start.x, end.x) + 1)
        val y = m * x + b

        return Point(x, y)
    }
}

class ParticleEmitter(
    val builder: ParticleBuilder,
    override val emitRate: Double,
    val source: Source? = null,
    lifeSpan: Long,
    width: Double,
    height: Double,
    position: Point,
    velocity: Point = Point(0.0, 0.0),
    acceleration: Point = Point(0.0, 0.0),
    drawableResId: Int? = null,
    color: DoubleColor = android.graphics.Color.TRANSPARENT.toDoubleColor(),
    colorChange: DoubleColor = DoubleColor()
): Particle(
    lifeSpan = lifeSpan,
    width = width,
    height = height,
    position = position,
    velocity = velocity,
    acceleration = acceleration,
    drawableResId = drawableResId,
    color = color,
    colorChange = colorChange
), Emitter, Source {
    override fun emit() = builder.build(this)
    override fun startPosition() = source?.startPosition() ?: Point(left, top)
}