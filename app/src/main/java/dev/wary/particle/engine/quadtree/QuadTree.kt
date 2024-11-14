package dev.wary.particle.engine.quadtree

class QuadTree<T>(val width: Double, val height: Double) {
    private val root: Node<T> = Node(0.0,0.0, width, height)
    private val valueToEntry = mutableMapOf<T, Entry<T>>()
    private val valueToNode = mutableMapOf<T, Node<T>>()

    /**
     * Returns a list of any values that overlaps with the polygon.
     * This descends the quadtree starting from the root
     */
//    fun collisionsWith(polygon: Polygon): List<T> {
//        val entries = findPossibleCollisions(rect)
//        val results = mutableListOf<T>()
//
//        for (entry in entries) {
//            if (collides(entry.rect, rect)) {
//                results.add(entry.value)
//            }
//        }
//
//        return results
//    }

    private fun findAllOverlaps(): List<Set<Entry<T>>> {
        val listByStart = findLocalEntriesSorted(root, compareBy { it.polygon.min(false) })
        val listByEnd = findLocalEntriesSorted(root, compareBy { it.polygon.max(false) })

        // Sweep

    }

    /**
     * returns a map of entries to a set of other entries they may overlap, without duplicates
     */
    fun sweep(
        listByStart: List<Entry<T>>,
        listByEnd: List<Entry<T>>,
        isVertical: Boolean = false
    ): Map<Entry<T>, Set<Entry<T>>> {
        val result = mutableMapOf<Entry<T>, MutableSet<Entry<T>>>()
        val currentSet = mutableSetOf<Entry<T>>()
        var startIndex = 0
        var endIndex = 0

        while (startIndex < listByStart.size && endIndex < listByEnd.size) {
            if (listByStart[startIndex].polygon.min(isVertical) < listByEnd[endIndex].polygon.max(isVertical)) {
                currentSet.add(listByStart[startIndex++])
            } else {
                currentSet.remove(listByEnd[endIndex])
                if (currentSet.isNotEmpty()) {
                    result.getOrPut(listByEnd[endIndex]) { mutableSetOf() }
                        .addAll(currentSet)
                }
                endIndex++
            }
        }

        while (endIndex < listByEnd.size) {
            currentSet.remove(listByEnd[endIndex])
            if (currentSet.isNotEmpty()) {
                result.getOrPut(listByEnd[endIndex]) { mutableSetOf() }
                    .addAll(currentSet)
            }
            endIndex++
        }

        return result
    }

    /**
     * Performs depth first search on the entire quadTree.
     * returns all entries separated into groups, which may overlap internally
     */
//    fun findLocalities(): List<List<Entry<T>>> {
//        val result = mutableListOf<List<Entry<T>>>()
//
//        val stack = ArrayDeque<Node<T>>()
//        stack.addFirst(root)
//
//        while (stack.isNotEmpty()) {
//            val node = stack.removeFirst()
//
//            if (node.entries.isEmpty()) {
//                for (child in node.children) {
//                    child?.let {
//                        stack.addFirst(it)
//                    }
//                }
//            } else {
//                result.add(findLocalEntries(node))
//            }
//        }
//
//        return result
//    }

    /**
     *
     * Returns all entries in the sub tree rooted at the node containing value.
     */
//    fun findLocalEntries(value: T): List<Entry<T>> {
//        valueToNode[value]?.let {
//            return findLocalEntries(it)
//        }
//        return emptyList()
//    }

    /**
     * Traverses the tree at root
     * Returns a list of all entries in the tree.
     */
//    private fun findLocalEntries(root: Node<T>): MutableList<Entry<T>> {
//        val result = mutableListOf<Entry<T>>()
//
//        val stack = ArrayDeque<Node<T>>()
//        stack.addFirst(root)
//
//        while (stack.isNotEmpty()) {
//            val node = stack.removeFirst()
//
//            result.addAll(node.content)
//
//            for (child in node.children) {
//                child?.let {
//                    stack.addFirst(it)
//                }
//            }
//        }
//
//        return result
//    }

    /**
     * Traverses the tree at root and merges all entries into a single sortedList
     * Returns the sorted list of entries
     */
    private fun findLocalEntriesSorted(
        root: Node<T>?,
        comparator: Comparator<Entry<T>>,
        isVertical: Boolean = false
    ): List<Entry<T>> {
        if (root == null) return emptyList()

        val tl = findLocalEntriesSorted(root.children[Quadrant.TopLeft.index], comparator, isVertical)
        val bl = findLocalEntriesSorted(root.children[Quadrant.BottomLeft.index], comparator, isVertical)
        val tr = findLocalEntriesSorted(root.children[Quadrant.BottomLeft.index], comparator, isVertical)
        val br = findLocalEntriesSorted(root.children[Quadrant.BottomLeft.index], comparator, isVertical)

        val childEntries = if (isVertical) {
            tl.mergeWith(tr, comparator).plus(bl.mergeWith(br, comparator))
        } else {
            tl.mergeWith(bl, comparator).plus(tr.mergeWith(br, comparator))
        }

        return root.entries.sortedWith(comparator).mergeWith(childEntries, comparator)
    }


    /**
     * Remove empty branches
     */
//   fun prune() {
//        // TODO
//        val stack = ArrayDeque<PathNode<T>>()
//        // Remove empty nodes
//        var shouldPrune = ptr.topLeft == null &&
//                ptr.bottomLeft == null &&
//                ptr.topRight == null &&
//                ptr.bottomRight == null &&
//                ptr.content.isEmpty()
//
//        while (shouldPrune && stack.isNotEmpty()) {
//            val top = stack.removeFirst()
//            ptr = top.node
//            ptr.children[top.quadrant] = null
//
//            shouldPrune = ptr.topLeft == null &&
//                    ptr.bottomLeft == null &&
//                    ptr.topRight == null &&
//                    ptr.bottomRight == null &&
//                    ptr.content.isEmpty()
//        }
//    }

    /**
     * Adds a value with the specified polygon to the deepest node that fully contains it.
     *
     * Polygons partially or entirely outside the quadTree bounds, will not be added.
     *
     * returns whether the value was successfully added
     */
    fun add(polygon: Polygon, value: T): Boolean {
        if (polygon.inBounds(root.x, root.y, width, height)) {
            val entry = entryOf(polygon, value)
            var ptr: Node<T>? = root

            while (ptr != null) {
                val halfWidth = ptr.width / 2
                val halfHeight = ptr.height / 2
                val xMid = ptr.x + halfWidth
                val yMid = ptr.y + halfHeight

                if (polygon.inBounds(ptr.x, ptr.y, halfWidth, halfHeight)) {
                    // Traverse topLeft sub region, create if needed
                    if (ptr.children[Quadrant.TopLeft.index] == null) {
                        ptr.children[Quadrant.TopLeft.index] = Node(ptr.x, ptr.y, halfWidth, halfHeight)
                    }
                    ptr = ptr.children[Quadrant.TopLeft.index]
                } else if (polygon.inBounds(ptr.x, yMid, halfWidth, halfHeight)) {
                    // Traverse bottomLeft sub region, create if needed
                    if (ptr.children[Quadrant.BottomLeft.index]  == null) {
                        ptr.children[Quadrant.BottomLeft.index] = Node(ptr.x, yMid, halfWidth, halfHeight)
                    }
                    ptr = ptr.children[Quadrant.BottomLeft.index]
                } else if (polygon.inBounds(xMid, ptr.y, halfWidth, halfHeight)) {
                    // Traverse topRight sub region, create if needed
                    if (ptr.children[Quadrant.TopRight.index] == null) {
                        ptr.children[Quadrant.TopRight.index] = Node(xMid, ptr.y, halfWidth, halfHeight)
                    }
                    ptr = ptr.children[Quadrant.TopRight.index]
                } else if (polygon.inBounds(xMid, yMid, halfWidth, halfHeight)) {
                    // Traverse bottomRight sub region, create if needed
                    if (ptr.children[Quadrant.BottomRight.index] == null) {
                        ptr.children[Quadrant.BottomRight.index] = Node(xMid, yMid, halfWidth, halfHeight)
                    }
                    ptr = ptr.children[Quadrant.BottomRight.index]
                } else {
                    return addEntry(ptr, entry)
                }
            }
        }
        return false
    }

    fun remove(value: T): Boolean {
        valueToEntry[value]?.let { entry ->
            return remove(root, entry)
        }

        return false
    }

    private fun remove(root: Node<T>, entry: Entry<T>): Boolean {
        val polygon = entry.polygon
        var ptr: Node<T> = root
        var found = false

        while (!found) {
            var inChild = false
            for (child in ptr.children) {
                if (polygon.inBounds(child)) {
                    ptr = child!!
                    inChild = true
                }
            }
            if (!inChild) {
               found = true
            }
        }

        return removeEntry(ptr, entry)
    }

    /**
     * Removes and reinserts the entry into the quadtree using the newly provided bounds
     */

    fun update(newBounds: Polygon, value: T): Boolean {
        valueToEntry[value]?.let { entry ->
            if (remove(root, entry)) {
                return add(newBounds, value)
            }
        }

        return false
    }

    private fun removeEntry(root: Node<T>, entry: Entry<T>): Boolean {
        if (root.entries.remove(entry)) {
            valueToEntry.remove(entry.value)
            valueToNode.remove(entry.value)
            return true
        }
        return false
    }

    private fun addEntry(root: Node<T>, entry: Entry<T>): Boolean {
        if (root.entries.add(entry)) {
            valueToEntry[entry.value] = entry
            valueToNode[entry.value] = root
            return true
        }
        return false
    }

    private fun Polygon.inBounds(node: Node<T>?): Boolean {
        return node != null && this.inBounds(node.x, node.y, node.width, node.height)
    }

    /**
     * Merge two sorted lists into a new list that is also sorted
     */
    private fun List<Entry<T>>.mergeWith(
        other: List<Entry<T>>,
        comparator: Comparator<Entry<T>>
    ): List<Entry<T>> {
        val result = mutableListOf<Entry<T>>()
        var index = 0
        var otherIndex = 0

        while (index < this.size && otherIndex < other.size) {
            if (comparator.compare(this[index], other[otherIndex]) < 0) {
                result.add(this[index++])
            } else {
                result.add(other[otherIndex++])
            }
        }

        while (index < this.size) {
            result.add(this[index++])
        }

        while (otherIndex < other.size) {
            result.add(other[otherIndex++])
        }

        return result
    }

    fun <T> entryOf(polygon: Polygon, value: T) = Entry(polygon, value)

    class Entry<T>(val polygon: Polygon, val value: T)

    private data class Node<T>(
        val x: Double,
        val y: Double,
        val width: Double,
        val height: Double,
        val entries: MutableList<Entry<T>> = mutableListOf(),
    ) {
        val children: Array<Node<T>?> = Array(4) { null }

        fun isLeaf(): Boolean {
            return children[Quadrant.TopLeft.index] == null &&
                    children[Quadrant.BottomLeft.index] == null &&
                    children[Quadrant.TopRight.index] == null &&
                    children[Quadrant.BottomRight.index] == null
        }

        fun contains(polygon: Polygon): Boolean {
            return polygon.inBounds(x, y, width, height)
        }
    }

//    private data class PathNode<T>(val node: Node<T>, val quadrant: Quadrant)
//
    private enum class Quadrant(val index: Int) {
        TopLeft(0),
        TopRight(1),
        BottomLeft(2),
        BottomRight(3)
    }
}

// data class Slice(val start: Double, val end: Double, val polygons: List<Polygon>)