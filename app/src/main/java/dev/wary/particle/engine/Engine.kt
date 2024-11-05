package dev.wary.particle.engine

import android.content.Context
import android.graphics.Canvas
import java.util.logging.Logger
import kotlin.math.abs
import kotlin.math.sign

enum class OverflowPolicy {
    DO_NOT_CREATE,
    REPLACE_OLDEST,
    REPLACE_OLDEST_NON_EMITTER
}

/** Manages and updates particles
 *
 */
class ParticleEngine(
    initialState: List<Rect> = emptyList(),
    val renderer: ParticleRenderer = ParticleRenderer(),
    val gravity: Double = 0.0,
    val edgeCollisions: Boolean = true,
    val particleCollisions: Boolean = false,
    val maxCapacity: Int = 10_000,
    val overflowPolicy: OverflowPolicy = OverflowPolicy.DO_NOT_CREATE
) {
    val bounds = Rect(0.0, 0.0, 0.0, 0.0)
    val entities = mutableListOf<Rect>()
    var quadTree = QuadTree<Particle>()
    var startIndex = 0
    var lastUpdateTime = 0L

    init {
        entities.addEntities(initialState)
        if (particleCollisions) {
            for (entity in initialState) {
                if (entity is Particle) {
                    quadTree.add(entity, entity)
                }
            }
        }
    }

    fun onSizeChanged(width: Int, height: Int) {
        bounds.width = width.toDouble()
        bounds.height = height.toDouble()

        quadTree.width = bounds.width
        quadTree.height = bounds.height
    }

    fun onDraw(canvas: Canvas, context: Context) {
        for (entity in entities) {
            if (entity is Particle) {
                renderer.drawParticle(entity, canvas, context)
            }
        }
        if (DEBUG) renderer.drawDebugInfo(canvas, this)
    }

    fun onUpdate(currentTime: Long) {
        val elapsedTime = if (lastUpdateTime == 0L) 1 else currentTime - lastUpdateTime
        lastUpdateTime = currentTime

        val newEntities = mutableListOf<Rect>()
        val createdEntities = mutableListOf<Rect>()
        for (i in entities.indices) {
            val entity = entities[i]

            if (entity is Particle) {
                updateParticle(entity, elapsedTime)
                if (entity.lifeSpan > 0) newEntities.add(entity)
            } else {
                newEntities.add(entity)
            }

            if (entity is Emitter) {
                repeat((entity.emitRate * elapsedTime).toInt()) {
                    createdEntities.add(entity.emit())
                }
            }
        }
        entities.clear()
        entities.addEntities(newEntities)
        entities.addEntities(createdEntities)

        quadTree = QuadTree()
        for (entity in entities) {
            if (entity is Particle) {
                quadTree.add(entity, entity)
            }
        }
    }

    private fun MutableList<Rect>.addEntities(newEntities: List<Rect>) {
        for (entity in newEntities) {
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
                        return
                    }
                }
                this[start++] = entity
                startIndex = start
                if (startIndex >= this.size) startIndex = 0
            } else {
                return
            }
        }
    }

    private fun updateParticle(particle: Particle, elapsedTime: Long) {
        handleCollisions(particle, elapsedTime)

        particle.lifeSpan -= elapsedTime
        particle.velocity.x += particle.acceleration.x * elapsedTime
        particle.velocity.y += (gravity + particle.acceleration.y) * elapsedTime

        particle.color += particle.colorChange * elapsedTime

        particle.tint?.let {
            particle.tint = it + particle.tintChange * elapsedTime
        }

    }

    private fun handleCollisions(particle: Particle, elapsedTime: Long) {
        var collisionHandled = false

        if (particleCollisions) {
            collisionHandled = handleParticleCollisions(particle)
        }

        if (edgeCollisions) {
            collisionHandled = handleEdgeCollisions(particle, elapsedTime)
            if (collisionHandled) particle.onEdgeCollision(particle)
        }

        if (!collisionHandled) {
            val vxFrame = particle.velocity.x * elapsedTime
            val vyFrame = particle.velocity.y * elapsedTime

            particle.left += vxFrame
            particle.top += vyFrame
        }
    }

    private fun handleParticleCollisions(particle: Particle): Boolean {
        val colliders = quadTree.existingCollisions(particle)
        if (colliders.isEmpty()) return false

        var dirXSum = particle.velocity.x.sign
        var dirYSum = particle.velocity.y.sign

        for (collider in colliders) {
            dirXSum += collider.velocity.x.sign
            dirYSum += collider.velocity.y.sign

            // TODO reorganize this callback
            particle.onParticleCollision(particle, collider)
        }

        val dirXAvg = dirXSum / (colliders.size + 1)
        val dirYAvg = dirYSum / (colliders.size + 1)

        val xs = if (dirXAvg * particle.velocity.x.sign < 1) -particle.velocity.x.sign else particle.velocity.x.sign
        val ys = if (dirYAvg * particle.velocity.y.sign < 1) -particle.velocity.y.sign else particle.velocity.y.sign
        particle.velocity.x *= xs
        particle.acceleration.x = xs * abs(particle.acceleration.x)
        particle.velocity.y *= ys
        particle.acceleration.y = ys * abs(particle.acceleration.y)

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
        const val DEBUG = false
    }
}