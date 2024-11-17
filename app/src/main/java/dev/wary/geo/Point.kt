package dev.wary.geo

import kotlin.math.abs
import kotlin.math.sqrt

data class Point(var x: Double, var y: Double): Comparable<Point> {
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    fun normal(): Point {
        return Point(-y, x)
    }

    fun unitNormal(): Point {
        val divisor =  sqrt(abs(x * x) + abs(y * y))
        return Point(-y / divisor, x / divisor)
    }

    fun dot(point: Point): Double {
        return x * point.x + y * point.y
    }

    override fun compareTo(other: Point): Int {
        if (x < other.x) return -1
        if (x > other.x) return 1
        if (y < other.y) return -1
        if (y > other.y) return 1
        return 0
    }
}