package dev.wary.particle.engine

import android.graphics.Color
import java.util.logging.Logger

data class Point(var x: Int, var y: Int)

enum class Offset {
    NONE,
    CENTER,
    CENTER_VERTICAL,
    CENTER_HORIZONTAL
}
data class CollisionBehavior(
    val onEdgeCollision: ((Particle) -> Unit) = {
        Logger.getLogger("foo").info("Collision")
    },
    val onParticleCollision: ((Particle) -> Unit) = {}
)

open class Entity(val position: Point, var width: Int, var height: Int)

open class Particle(
    position: Point,
    width: Int,
    height: Int,
    var lifeSpan: Long,
    val velocity: Point = Point(0, 0),
    val acceleration: Point = Point(0, 0),
    // val sizeDelta: Point,
    var alpha: Int? = null,
    val alphaChange: Int = 0,
    var drawableResId: Int? = null,
    var color: Int = Color.TRANSPARENT,
    // var colorDelta: Int,
    val collisionBehavior: CollisionBehavior = CollisionBehavior()
): Entity (position = position, width = width, height = height)

// Used to Initialize new particles
data class ParticleParams(
    val lifeSpan: LongRange, // Lifespan of particle in millis
    val width: IntRange,
    val height: IntRange,
    val vx: IntRange,
    val vy: IntRange,
    val ax: IntRange,
    val ay: IntRange,
    val drawableResIds: List<Int> = emptyList(),
    val colors: List<Int> = listOf(Color.TRANSPARENT),
    val alpha: IntRange? = null,
    val alphaChange: IntRange = IntRange(0, 0)
)