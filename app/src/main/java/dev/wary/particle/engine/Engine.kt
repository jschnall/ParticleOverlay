package dev.wary.particle.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import java.util.logging.Logger
import kotlin.math.abs

enum class OverflowPolicy {
    DO_NOT_CREATE,
    REPLACE_OLDEST
}

/** Manages and updates particles
 *
 */
class ParticleEngine(
    initialState: List<Entity> = emptyList(),
    val renderer: ParticleRenderer = ParticleRenderer(),
    val gravity: Int = 0,
    val edgeCollisions: Boolean = true,
    val particleCollisions: Boolean = false,
    val maxCapacity: Int = 10_000,
    val overflowPolicy: OverflowPolicy = OverflowPolicy.DO_NOT_CREATE
) {
    val bounds = Rect(0, 0, 0, 0)
    val entities = mutableListOf<Entity>()
    var startIndex = 0
    var lastUpdateTime = 0L

    init {
        entities.addEntities(initialState)
    }

    fun onSizeChanged(width: Int, height: Int) {
        bounds.right = width
        bounds.bottom = height
    }

    fun draw(canvas: Canvas, context: Context) {
        for (entity in entities) {
            if (entity is Particle) {
                renderer.drawParticle(entity, canvas, context)
            }
        }
    }

    fun update(currentTime: Long) {
        val elapsedTime = if (lastUpdateTime == 0L) 1 else currentTime - lastUpdateTime
        lastUpdateTime = currentTime

        val newEntities = mutableListOf<Entity>()

        for (i in entities.indices) {
            val entity = entities[i]

            if (entity is Particle) {
                updateParticle(entity, elapsedTime)
                if (entity.lifeSpan > 0) newEntities.add(entity)
            } else {
                newEntities.add(entity)
            }

            if (entity is Emitter) {
                newEntities.add(entity.emit())
            }
        }
        entities.clear()
        entities.addEntities(newEntities)
    }

    private fun MutableList<Entity>.addEntities(newEntities: List<Entity>) {
        for (entity in newEntities) {
            if (this.size < maxCapacity) {
                this.add(entity)
            } else if (overflowPolicy == OverflowPolicy.REPLACE_OLDEST) {
                this[startIndex++] = entity
                if (startIndex >= this.size) startIndex = 0
            } else {
                break
            }
        }
    }

    private fun updateParticle(particle: Particle, elapsedTime: Long) {
        val vxFrame = (particle.velocity.x / elapsedTime).toInt()
        val vyFrame = (particle.velocity.y / elapsedTime).toInt()

        handleCollisions(particle, vxFrame, vyFrame)

        particle.lifeSpan -= elapsedTime
        particle.velocity.x += (particle.acceleration.x / elapsedTime).toInt()
        particle.velocity.y += (particle.acceleration.y / elapsedTime).toInt()

        particle.alpha?.let {
            particle.alpha = (it + (particle.alphaChange / elapsedTime)).toInt().coerceIn(0, 255)
        }
    }

    private fun handleCollisions(particle: Particle, vxFrame: Int, vyFrame: Int) {
        var collision = false
        if (edgeCollisions && collidesWithEdge(particle, vxFrame, vyFrame)) {
            collision = true
            particle.collisionBehavior.onEdgeCollision
        }

        // if (particleCollisions) {
        // collision = true
        //     TODO requires quadTree
        // }

        if (!collision) {
            particle.position.x += vxFrame
            particle.position.y += vyFrame
        }
    }

    private fun collidesWithEdge(particle: Particle, vxFrame: Int, vyFrame: Int): Boolean {
        val newX = particle.position.x + vxFrame
        val newY = particle.position.y + vyFrame

        val minX = minOf(particle.position.x, newX)
        val maxX = maxOf(particle.position.x, newX) + particle.width
        val minY = minOf(particle.position.y, newY)
        val maxY = maxOf(particle.position.y, newY) + particle.height

        var result = false

        if (bounds.left in minX .. maxX) {
            particle.velocity.x = -particle.velocity.x
            particle.acceleration.x = -particle.acceleration.x
            particle.position.x -= (vxFrame - (bounds.left - particle.position.x))
            result = true
        } else if (bounds.right in minX .. maxX) {
            particle.velocity.x = -particle.velocity.x
            particle.acceleration.x = -particle.acceleration.x
            particle.position.x -= (vxFrame - (bounds.right - (particle.position.x + particle.width)))
            result = true
        }

        if (bounds.top in minY .. maxY) {
            particle.velocity.y = -particle.velocity.y
            particle.acceleration.y = -particle.acceleration.y
            particle.position.y -= (vyFrame - (bounds.top - particle.position.y))
            result = true
        } else if (bounds.bottom in minY .. maxY) {
            particle.velocity.y = -particle.velocity.y
            particle.acceleration.y = -particle.acceleration.y
            particle.position.y -= (vyFrame - (bounds.bottom - (particle.position.y + particle.height)))
            result = true
        }

        return result
    }
}