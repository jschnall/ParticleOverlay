package dev.wary.particle.engine.quadtree

import dev.wary.particle.engine.Point

data class Polygon(val points: List<Point>) {
    fun inBounds(x: Double, y: Double, width: Double, height: Double): Boolean {
        if (min(false) < x || max(false) > x + width ||
            min(true) < y || max(true) > y + height) return false
        return true
    }

    fun min(isVertical: Boolean) = if (isVertical) points.minBy { it.y }.y else points.minBy { it.x }.x
    fun max(isVertical: Boolean) = if (isVertical) points.maxBy { it.y }.y else points.maxBy { it.x }.x

    fun overlaps(polygon: Polygon): Boolean {
        // TODO
        return false
    }
}