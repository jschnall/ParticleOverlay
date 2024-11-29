package dev.wary.geo

import kotlin.math.abs
import kotlin.math.sqrt

data class Point(var x: Double, var y: Double): Comparable<Point> {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    fun dot(point: Point): Double {
        return x * point.x + y * point.y
    }

    fun cross(point: Point): Double {
        return x * point.y - y * point.x
    }

    fun normal(): Point {
        return Point(-y, x)
    }

    fun unitNormal(): Point {
        val divisor =  sqrt(abs(x * x) + abs(y * y))
        return Point(-y / divisor, x / divisor)
    }

    override fun compareTo(other: Point): Int {
        if (x < other.x) return -1
        if (x > other.x) return 1
        if (y < other.y) return -1
        if (y > other.y) return 1
        return 0
    }
}

data class Edge(val p1: Point, val p2: Point)