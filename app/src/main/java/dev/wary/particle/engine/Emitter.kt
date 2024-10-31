package dev.wary.particle.engine

import android.graphics.Color
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

fun interface ParticleBuilder {
    fun build(source: Source): Particle
}

fun interface Emitter {
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
            alpha = template.alpha,
            alphaChange = template.alphaChange
    )
}

class RangedParticleBuilder(val params: ParticleParams): ParticleBuilder {
    override fun build(source: Source) = Particle(
            lifeSpan = Random.nextLong(params.lifeSpan),
            width = Random.nextInt(params.width),
            height = Random.nextInt(params.height),
            position = source.startPosition().copy(),
            velocity = Point(Random.nextInt(params.vx), Random.nextInt(params.vy)),
            acceleration = Point(Random.nextInt(params.ax), Random.nextInt(params.ay)),
            drawableResId = if (params.drawableResIds.isEmpty()) null else params.drawableResIds[Random.nextInt(params.drawableResIds.size)],
            color = params.colors[Random.nextInt(params.colors.size)],
            alpha = params.alpha?.let { Random.nextInt(it) },
            alphaChange = Random.nextInt(params.alphaChange)
    )
}

class PointEmitter(
    position: Point,
    val builder: ParticleBuilder,
): Entity(
    position = position,
    width = 1,
    height = 1
), Emitter, Source {
    override fun emit() = builder.build(this)
    override fun startPosition() = position
}

class LineEmitter(
    val start: Point,
    val end: Point,
    val builder: ParticleBuilder,
): Entity(
    position = start,
    width = abs(end.x - start.x),
    height = abs(end.y - start.y)
), Emitter, Source {
    override fun emit() = builder.build(this)

    override fun startPosition(): Point {
        if (end.x == start.x) {
            return Point(start.x, Random.nextInt(minOf(start.y, end.y), maxOf(start.y, end.y) + 1))
        }

        // y = mx + b
        val m = (end.y - start.y) / (end.x - start.x)
        val b = start.y - m * start.x

        val x = Random.nextInt(minOf(start.x, end.x), maxOf(start.x, end.x) + 1)
        val y = m * x + b

        return Point(x, y)
    }
}

class ParticleEmitter(
    val builder: ParticleBuilder,
    val emitRate: Int = 0,
    val source: Source? = null,
    lifeSpan: Long,
    width: Int,
    height: Int,
    position: Point,
    velocity: Point = Point(0, 0),
    acceleration: Point = Point(0, 0),
    drawableResId: Int? = null,
    color: Int = Color.TRANSPARENT
): Particle(
    lifeSpan = lifeSpan,
    width = width,
    height = height,
    position = position,
    velocity = velocity,
    acceleration = acceleration,
    drawableResId = drawableResId,
    color = color
), Emitter, Source {
    override fun emit() = builder.build(this)
    override fun startPosition() = source?.startPosition() ?: position
}