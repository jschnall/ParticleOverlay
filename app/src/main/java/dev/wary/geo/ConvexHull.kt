package dev.wary.geo

// Returns a list of points on the convex hull
// in counter-clockwise order
// O(nlogn)
fun convexHull(points: MutableList<Point>): List<Point> {
    val n = points.size
    var k = 0

    if (n <= 3) return points

    val ans: MutableList<Point> = ArrayList(2 * n)

    // Sort points lexicographically
    points.sort()

    // Build lower hull
    for (i in 0..<n) {
        // If the point at K-1 position is not a part
        // of hull as vector from ans[k-2] to ans[k-1]
        // and ans[k-2] to A[i] has a clockwise turn
        while (k >= 2
            && (crossProduct(
                ans[k - 2],
                ans[k - 1], points[i]
            )
                    <= 0)
        ) ans.removeAt(--k)
        ans.add(points[i])
        k++
    }


    // Build upper hull
    var i = n - 2
    val t = k
    while (i >= 0) {
        // If the point at K-1 position is not a part
        // of hull as vector from ans[k-2] to ans[k-1]
        // and ans[k-2] to A[i] has a clockwise turn
        while (k > t
            && (crossProduct(
                ans[k - 2],
                ans[k - 1], points[i]
            )
                    <= 0)
        ) ans.removeAt(--k)
        ans.add(points[i])
        k++
        --i
    }


    // Resize the array to desired size
    ans.removeAt(ans.size - 1)

    return ans
}

// Cross product of two vectors OA and OB
// returns positive for counter clockwise
// turn and negative for clockwise turn
fun crossProduct(O: Point, A: Point, B: Point): Long {
    return ((A.x - O.x) * (B.y - O.y)
            - (A.y - O.y) * (B.x - O.x)).toLong()
}