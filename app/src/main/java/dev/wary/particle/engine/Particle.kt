package dev.wary.particle.engine

import android.graphics.Color
import java.util.logging.Logger
import kotlin.random.Random

data class Point(var x: Double, var y: Double)
data class Rect(var left: Double, var top: Double, var right: Double, var bottom: Double) {
    val width: Double
        get() = right - left

    val height: Double
        get() = bottom - top
}

data class CollisionBehavior(
    val onEdgeCollision: ((Particle) -> Unit) = {
        Logger.getLogger("foo").info("Collision")
    },
    val onParticleCollision: ((Particle) -> Unit) = {}
)

// TODO replace Entity with Rect?
open class Entity(val position: Point, var width: Double, var height: Double)

open class Particle(
    position: Point,
    width: Double,
    height: Double,
    var lifeSpan: Long,
    val velocity: Point = Point(0.0, 0.0),
    val acceleration: Point = Point(0.0, 0.0),
    // val sizeDelta: Point,
    var alpha: Double? = null,
    val alphaChange: Double = 0.0,
    var drawableResId: Int? = null,
    var color: Int = Color.TRANSPARENT,
    // var colorDelta: Int,
    val collisionBehavior: CollisionBehavior = CollisionBehavior()
): Entity (position = position, width = width, height = height)

interface Param<T> {
    val value: T
}

class ExactParam<T>(val v: T): Param<T> {
    override val value: T
        get() = v
}

class ListParam<T>(val list: List<T>): Param<T> {
    override val value: T
        get() = list[Random.nextInt(0, list.size)]
}

fun <T> listParamOf(vararg items: T) = ListParam(items.toList())

abstract class RangeParam<T>(val from: T, val toExclusive: T): Param<T>

class IntRangeParam(from: Int, toExclusive: Int): RangeParam<Int>(from, toExclusive) {
    override val value
        get() = Random.nextInt(from, toExclusive)
}

class LongRangeParam(from: Long, toExclusive: Long): RangeParam<Long>(from, toExclusive) {
    override val value
        get() = Random.nextLong(from, toExclusive)
}

class DoubleRangeParam(from: Double, toExclusive: Double): RangeParam<Double>(from, toExclusive) {
    override val value
        get() = Random.nextDouble(from, toExclusive)
}

// Used to Initialize new particles
data class ParticleParams(
    val lifeSpan: Param<Long>, // Lifespan of particle in millis
    val width: Param<Double>,
    val height: Param<Double>,
    val vx: Param<Double>,
    val vy: Param<Double>,
    val ax: Param<Double>,
    val ay: Param<Double>,
    val drawableResIds: ListParam<Int>? = null,
    val colors: Param<Int> = ListParam(listOf(Color.TRANSPARENT)),
    val alpha: Param<Double>? = null,
    val alphaChange: Param<Double> = ExactParam(0.0)
)