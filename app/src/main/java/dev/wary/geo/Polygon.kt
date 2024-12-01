package dev.wary.geo

import kotlin.math.abs


data class Projection(val min: Double, val max: Double) {
    fun contains(projection: Projection): Boolean {
        return projection.min >= min && projection.max <= max
    }

    fun overlap(projection: Projection): Double {
        return minOf(max, projection.max) - maxOf(min, projection.min)
    }

    fun isOverlap(projection: Projection): Boolean {
        return min < projection.max && max > projection.min
    }
}

data class MinTranslationVector(val axis: Point, val overlap: Double)

data class Polygon(val points: List<Point>) {
    fun inBounds(x: Double, y: Double, width: Double, height: Double): Boolean {
        if (min(false) < x || max(false) > x + width ||
            min(true) < y || max(true) > y + height) return false
        return true
    }

    fun min(isVertical: Boolean) = if (isVertical) points.minBy { it.y }.y else points.minBy { it.x }.x
    fun max(isVertical: Boolean) = if (isVertical) points.maxBy { it.y }.y else points.maxBy { it.x }.x

    fun axes(): List<Point> {
        val result = mutableListOf<Point>()

        result.add((points[0] - points[points.size - 1]).unitNormal())
        for (i in 1 until points.size) {
            result.add((points[i] - points[i - 1]).unitNormal())
        }

        return result
    }

    fun projection(axis: Point): Projection {
        var min: Double = axis.dot(points[0])
        var max = min

        for (point in points) {
            val p = axis.dot(point)
            min = minOf(min, p)
            max = maxOf(max, p)
        }

        return Projection(min, max)
    }

    fun isOverlap(polygon: Polygon): Boolean {
        for (axis in axes()) {
            val p1 = projection(axis)
            val p2 = polygon.projection(axis)

            if (!p1.isOverlap(p2)) return false
        }
        for (axis in polygon.axes()) {
            val p1 = projection(axis)
            val p2 = polygon.projection(axis)

            if (!p1.isOverlap(p2)) return false
        }

        return true
    }

    fun overlap(polygon: Polygon): MinTranslationVector? {
        var minOverlap: Double = Double.MAX_VALUE
        var minAxis: Point? = null

        for (axis in axes()) {
            val p1 = projection(axis)
            val p2 = polygon.projection(axis)

            if (!p1.isOverlap(p2)) return null

            var overlap = p1.overlap(p2)
            if (p1.contains(p2) || p2.contains(p1)) {
                // overlap plus distance from min endpoints
                val min = abs(p1.min - p2.min)
                val max = abs(p1.max - p2.max)
                if (min < max) {
                    overlap += min;
                } else {
                    overlap += max;
                }
            }

            if (overlap < minOverlap) {
                minOverlap = overlap
                minAxis = axis
            }

        }
        for (axis in polygon.axes()) {
            val p1 = projection(axis)
            val p2 = polygon.projection(axis)

            if (!p1.isOverlap(p2)) return null

            var overlap = p1.overlap(p2)
            if (p1.contains(p2) || p2.contains(p1)) {
                // overlap plus distance from min endpoints
                val min = abs(p1.min - p2.min)
                val max = abs(p1.max - p2.max)
                if (min < max) {
                    overlap += min;
                } else {
                    overlap += max;
                }
            }

            if (overlap < minOverlap) {
                minOverlap = overlap
                minAxis = axis
            }
        }

        return MinTranslationVector(minAxis!!, minOverlap)
    }
}



