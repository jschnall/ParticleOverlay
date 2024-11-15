package dev.wary.geo

import kotlin.math.sign

data class Point(var x: Double, var y: Double) {
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    fun normal(): Point {
        return Point(-y.sign, x.sign)
    }

    fun dot(point: Point): Double {
        return x * point.x + y * point.y
    }
}