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

        result.add((points[0] - points[points.size - 1]).normal())
        for (i in 1 until points.size) {
            result.add((points[i] - points[i - 1]).normal())
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

            // get the overlap
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

// To find orientation of ordered triplet (p, q, r).
// The function returns following values
// 0 --> p, q and r are colinear
// 1 --> Clockwise
// 2 --> Counterclockwise
fun orientation(p: Point, q: Point, r: Point): Int {
    val v = ((q.y - p.y) * (r.x - q.x) -
            (q.x - p.x) * (r.y - q.y)).toInt()

    if (v == 0) return 0 // colinear

    return if ((v > 0)) 1 else 2 // clock or counterclock wise
}

fun convexHull(points: List<Point>): List<Point> {
    val hull = mutableListOf<Point>()

    // There must be at least 3 points
    if (points.size < 3) return hull

    // Find the leftmost point
    var l = 0
    for (i in 1 until points.size) if (points[i].x < points[l].x) l = i

    // Start from leftmost point, keep moving
    // counterclockwise until reach the start point
    // again. This loop runs O(h) times where h is
    // number of points in result or output.
    var p = l
    var q: Int
    do {
        // Add current point to result
        hull.add(points[p])

        // Search for a point 'q' such that
        // orientation(p, x, q) is counterclockwise
        // for all points 'x'. The idea is to keep
        // track of last visited most counterclock-
        // wise point in q. If any point 'i' is more
        // counterclock-wise than q, then update q.
        q = (p + 1) % points.size

        for (i in points.indices) {
            // If i is more counterclockwise than
            // current q, then update q
            if (orientation(points[p], points[i], points[q]) == 2) q = i
        }

        // Now q is the most counterclockwise with
        // respect to p. Set p as q for next iteration,
        // so that q is added to result 'hull'
        p = q
    } while (p != l) // While we don't come to first

    return hull
}