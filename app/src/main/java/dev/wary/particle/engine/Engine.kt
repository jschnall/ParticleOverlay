package dev.wary.particle.engine

import android.content.Context
import android.graphics.Canvas
import dev.wary.geo.Point
import dev.wary.geo.Polygon
import dev.wary.geo.Rect
import dev.wary.geo.convexHull
import dev.wary.data.quadtree.QuadTree
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sign

enum class OverflowPolicy {
    DO_NOT_CREATE,
    REPLACE_OLDEST,
    REPLACE_OLDEST_NON_EMITTER
}

/**
 * Manages and updates particles
 */
class ParticleEngine(
    initialState: List<Rect> = emptyList(),
    val renderer: ParticleRenderer = ParticleRenderer(),
    // val gravity: Double = 0.0,
    val edgeCollisions: Boolean = true,
    val particleCollisions: Boolean = false,
    val maxCapacity: Int = 5_000,
    val overflowPolicy: OverflowPolicy = OverflowPolicy.DO_NOT_CREATE
) {
    val bounds = Rect(0.0, 0.0, 0.0, 0.0)
    val entities = mutableListOf<Rect>()
    var quadTree = QuadTree<Particle>()
    var startIndex = 0
    var lastUpdateTime = 0L
    val collisionBounds = mutableListOf<Polygon?>()

    init {
        for (entity in initialState) {
            entities.add(entity)
        }

        for (entity in initialState) {
            if (entity is Particle) {
                val polygon = entity.toPolygon()
                if (particleCollisions) {
                    quadTree.add(polygon, entity)
                }
                collisionBounds.add(polygon)
            } else {
                collisionBounds.add(null)
            }
        }
    }

    fun sizeChanged(width: Int, height: Int) {
        bounds.width = width.toDouble()
        bounds.height = height.toDouble()

        quadTree = QuadTree(bounds.width, bounds.height)
    }

    fun draw(canvas: Canvas, context: Context) {
        for (i in entities.indices) {
            val entity = entities[i]
            if (entity is Particle) {
                if (DRAW_COLLISION_BOUNDS) {
                    collisionBounds[i]?.let {
                        renderer.drawCollisionBounds(entity, it, canvas)
                    }
                } else {
                    renderer.drawParticle(entity, canvas, context)
                }
            }
        }
        if (DEBUG) renderer.drawDebugInfo(canvas, this)
    }

    fun update(currentTime: Long) {
        val elapsedTime = if (lastUpdateTime == 0L) 1 else currentTime - lastUpdateTime
        lastUpdateTime = currentTime

        val createdEntities = mutableMapOf<Rect, Polygon?>()
        val updatedEntities = mutableMapOf<Rect, Polygon?>()

        for (i in entities.indices) {
            val entity = entities[i]
            val oldPoints = entity.toPoints()

            if (entity is Particle) {
                if (entity.lifeSpan - elapsedTime > 0) {
                    updatedEntities[entity] = Polygon(convexHull(oldPoints.plus(entity.newBounds(elapsedTime)).toMutableList()))
                }
            } else {
                updatedEntities[entity] = null
            }

            if (entity is Emitter) {
                repeat(ceil(entity.emitRate * elapsedTime).toInt()) {
                    val particle = entity.emit()
                    createdEntities[particle] = particle.toPolygon()
                }
            }
        }

        entities.clear()
        collisionBounds.clear()
        if (particleCollisions) {
            quadTree = QuadTree(width = bounds.width, height = bounds.height)
        }
        for (entry in updatedEntities) {
            val entity = entry.key
            val polygon = entry.value

            if (entities.addEntity(entity)) {
                collisionBounds.add(polygon)
                if (particleCollisions && entity is Particle) {
                    quadTree.add(polygon!!, entity)
                }
            }
        }
        for (entry in createdEntities) {
            val entity = entry.key
            val polygon = entry.value

            if (entities.addEntity(entity)) {
                collisionBounds.add(polygon)
                if (particleCollisions && entity is Particle) {
                    quadTree.add(Polygon(entity.toPoints()), entity)
                }
            }
        }

        val allCollisions = quadTree.findAllOverlaps()

        for (entity in entities) {
            if (entity is Particle) {
                updateParticle(entity, elapsedTime, allCollisions.getOrDefault(entity, emptyList()))
            }
        }
    }

    private fun MutableList<Rect>.addEntity(entity: Rect): Boolean {
        if (this.size < maxCapacity) {
            this.add(entity)
        } else if (overflowPolicy == OverflowPolicy.REPLACE_OLDEST) {
            this[startIndex++] = entity
            if (startIndex >= this.size) startIndex = 0
        } else if (overflowPolicy == OverflowPolicy.REPLACE_OLDEST_NON_EMITTER) {
            var start = startIndex
            while (this[start] is Emitter) {
                start++
                if (start >= this.size) start = 0
                if (start == startIndex) {
                    // All emitters so cannot replace
                    return false
                }
            }
            this[start++] = entity
            startIndex = start
            if (startIndex >= this.size) startIndex = 0
        } else {
            // At capacity, do not create
            return false
        }
        return true
    }

    private fun Particle.newBounds(elapsedTime: Long): List<Point> {
        val result = mutableListOf<Point>()

        val dx = velocity.x * elapsedTime + 0.5 * acceleration.x * elapsedTime * elapsedTime
        val dy = velocity.y * elapsedTime + 0.5 * acceleration.y * elapsedTime * elapsedTime

        return Rect(
            left = left + dx,
            top = top + dy,
            width = width,
            height = height
        ).toPoints()
    }

    private fun updateParticle(particle: Particle, elapsedTime: Long, collisions: List<Particle>) {
        handleCollisions(particle, elapsedTime, collisions)

        particle.lifeSpan -= elapsedTime

        particle.velocity.x += particle.acceleration.x * elapsedTime
        particle.velocity.y += particle.acceleration.y * elapsedTime

        particle.color += particle.colorChange * elapsedTime

        particle.tint?.let {
            particle.tint = it + particle.tintChange * elapsedTime
        }
    }

    private fun handleCollisions(particle: Particle, elapsedTime: Long, collisions: List<Particle>) {
        var collisionHandled = false

        if (particleCollisions) {
            collisionHandled = handleParticleCollisions(particle, collisions)
        }

        if (edgeCollisions) {
            collisionHandled = handleEdgeCollisions(particle, elapsedTime)
            if (collisionHandled) particle.onEdgeCollision(particle)
        }

        if (!collisionHandled) {
            val dx = particle.velocity.x * elapsedTime + 0.5 * particle.acceleration.x * elapsedTime * elapsedTime
            val dy = particle.velocity.y * elapsedTime + 0.5 * particle.acceleration.y * elapsedTime * elapsedTime

            particle.left += dx
            particle.top += dy
        }
    }

    private fun handleParticleCollisions(particle: Particle, collisions: List<Particle>): Boolean {
        if (collisions.isEmpty()) return false

        var dirXSum = 1.0
        var dirYSum = 1.0

        for (collision in collisions) {
            dirXSum *= collision.velocity.x.sign
            dirYSum *= collision.velocity.y.sign

            particle.onParticleCollision(particle, collision)
        }

        val dirX = dirXSum / abs(dirXSum)
        val dirY = dirYSum / abs(dirYSum)

        particle.velocity.x *= dirX
        particle.acceleration.x *= dirX
        particle.velocity.y *= dirY
        particle.acceleration.y *= dirY

        return false
    }

    private fun handleEdgeCollisions(particle: Particle, elapsedTime: Long): Boolean {
        val vxFrame = particle.velocity.x * elapsedTime
        val vyFrame = particle.velocity.y * elapsedTime

        val newX = particle.left + vxFrame
        val newY = particle.top + vyFrame

        val minX = minOf(particle.left, newX)
        val maxX = maxOf(particle.left, newX) + particle.width
        val minY = minOf(particle.top, newY)
        val maxY = maxOf(particle.top, newY) + particle.height

        var result = false

        if (bounds.left in minX .. maxX) {
            particle.velocity.x = -particle.velocity.x
            particle.acceleration.x = -particle.acceleration.x
            particle.left -= (vxFrame - (bounds.left - particle.left))
            result = true
        } else if (bounds.right in minX .. maxX) {
            particle.velocity.x = -particle.velocity.x
            particle.acceleration.x = -particle.acceleration.x
            particle.left -= (vxFrame - (bounds.right - (particle.left + particle.width)))
            result = true
        }

        if (bounds.top in minY .. maxY) {
            particle.velocity.y = -particle.velocity.y
            particle.acceleration.y = -particle.acceleration.y
            particle.top -= (vyFrame - (bounds.top - particle.top))
            result = true
        } else if (bounds.bottom in minY .. maxY) {
            particle.velocity.y = -particle.velocity.y
            particle.acceleration.y = -particle.acceleration.y
            particle.top -= (vyFrame - (bounds.bottom - (particle.top + particle.height)))
            result = true
        }

        return result
    }

    companion object {
        const val DEBUG = true
        const val DRAW_COLLISION_BOUNDS = false
    }
}