package dev.wary.geo

open class Rect(var left: Double, var top: Double, var width: Double, var height: Double) {
    val right: Double
        get() = left + width

    val bottom: Double
        get() = top + height

    // converts to list of counterClockwise points
    fun toPoints(): List<Point> {
        return listOf(Point(left, top), Point(left, bottom), Point(right, bottom), Point(right, top))
    }

    fun toPolygon(): Polygon {
        return Polygon(toPoints())
    }
}

fun polygonFromRectSweep(start: Rect, end: Rect): Polygon {
    val points = mutableListOf<Point>()

    points.addAll(start.toPoints())
    points.addAll(end.toPoints())

    return Polygon(convexHull(points))
}