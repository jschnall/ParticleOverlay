package dev.wary.particle.engine

data class Point(var x: Double, var y: Double)

open class Rect(var left: Double, var top: Double, var width: Double, var height: Double) {
    val right: Double
        get() = left + width

    val bottom: Double
        get() = top + height
}

fun interface OnEdgeCollision {
    fun onEdgeColliision(particle: Particle)
}

fun interface OnParticleCollision {
    fun onParticleCollision(particle: Particle, other: Particle)
}

/** Stores each color component as a double for transitions
 *
 */
class DoubleColor(var alpha: Double = 0.0, var red: Double = 0.0, var green: Double = 0.0, var blue: Double = 0.0) {
    constructor(color: Int): this(
        alpha = ((color shr 24) and 0xff).toDouble(),
        red = ((color shr 16) and 0xff).toDouble(),
        green = ((color shr 8) and 0xff).toDouble(),
        blue = (color and 0xff).toDouble()
    )

    // FIXME?: Do not use with paint.setColor()
//    fun toInt(): Int =
//        (alpha.toInt() and 0xff) shl 24 or
//        (red.toInt() and 0xff) shl 16 or
//        (green.toInt() and 0xff) shl 8 or
//        (blue.toInt() and 0xff)

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

// TODO: Modifier types including fixed stopping points, oscilators
open class Particle(
    position: Point,
    width: Double,
    height: Double,
    // TODO: sizeChange
    var lifeSpan: Long,
    val velocity: Point = Point(0.0, 0.0),
    val acceleration: Point = Point(0.0, 0.0),
    var drawableResId: Int? = null,
    var color: DoubleColor = DoubleColor(),
    val colorChange: DoubleColor = DoubleColor(),
    var tint: DoubleColor? = null,
    val tintChange: DoubleColor = DoubleColor(),
    val onEdgeCollision: (Particle) -> Unit = {},
    val onParticleCollision: (Particle, Particle) -> Unit = { _, _ -> },
): Rect (left = position.x, top = position.y, width = width, height = height)

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
    val onEdgeCollision: Param<(Particle) -> Unit> = listParamOf({}),
    val onParticleCollision: Param<(Particle, Particle) -> Unit> = listParamOf({ _, _ -> })
)