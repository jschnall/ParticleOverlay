package dev.wary.particle.engine

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

class DoubleColor(var alpha: Double = 0.0, var red: Double = 0.0, var green: Double = 0.0, var blue: Double = 0.0) {
    constructor(color: Int): this(
        alpha = ((color shr 24) and 0xff).toDouble(),
        red = ((color shr 16) and 0xff).toDouble(),
        green = ((color shr 8) and 0xff).toDouble(),
        blue = (color and 0xff).toDouble()
    )

    // Warning: Do not use with paint.setColor()
    fun toInt(): Int =
        (alpha.toInt() and 0xff) shl 24 or
        (red.toInt() and 0xff) shl 16 or
        (green.toInt() and 0xff) shl 8 or
        (blue.toInt() and 0xff)

    operator fun times(l: Long): DoubleColor {
        return DoubleColor(
            alpha = (alpha * l),
            red = (red * l),
            green = (green * l),
            blue = (blue * l)
        )
    }

    operator fun plus(other: DoubleColor): DoubleColor {
        return DoubleColor(
            alpha = (alpha + other.alpha).coerceIn(0.0, 255.0),
            red = (red + other.red).coerceIn(0.0, 255.0),
            green = (green + other.green).coerceIn(0.0, 255.0),
            blue = (blue + other.blue).coerceIn(0.0, 255.0)
        )
    }


    companion object{
        fun fromInt(color: Int) = DoubleColor(color)
    }
}

fun Int.toDoubleColor() = DoubleColor.fromInt(this)

open class Particle(
    position: Point,
    width: Double,
    height: Double,
    // TODO sizeChange, oscilators
    var lifeSpan: Long,
    val velocity: Point = Point(0.0, 0.0),
    val acceleration: Point = Point(0.0, 0.0),
    var drawableResId: Int? = null,
    var color: DoubleColor = DoubleColor(),
    val colorChange: DoubleColor = DoubleColor(),
    var tint: DoubleColor? = null,
    var tintChange: DoubleColor = DoubleColor(),
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

class ColorRangeParam(
    fromDoubleColor: DoubleColor,
    toDoubleColorExclusive: DoubleColor
) {
    constructor(from: Int, to: Int): this(
        fromDoubleColor = DoubleColor.fromInt(from),
        toDoubleColorExclusive = DoubleColor.fromInt(to)
    )
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
    val color: Param<DoubleColor> = ExactParam(DoubleColor()),
    val colorChange: Param<DoubleColor> = ExactParam(DoubleColor()),
    val tint: Param<DoubleColor> = ExactParam(DoubleColor()),
    val tintChange: Param<DoubleColor> = ExactParam(DoubleColor()),
)