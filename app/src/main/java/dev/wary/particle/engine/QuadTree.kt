package dev.wary.particle.engine

class QuadTree<T>(var width: Double = 0.0, var height: Double = 0.0) {
    private val root: Node<T> = Node()
    private val valueToNode = mutableMapOf<T, Node<T>>()

    /**
     * Returns a list of any pre-inserted values that overlaps with rect.
     */
    fun collisions(rect: Rect): List<T> {
        // TODO
        return emptyList()
    }

    fun findLocalEntries(rect: Rect): List<Entry<T>> {
        val result = mutableListOf<Entry<T>>()
        //findInnerRect(rect, result)
        return result
    }

    fun findInnerRect() {
        // TODO
    }

    /**
     * Returns a list of any other pre-inserted values this overlaps with.
     * If value does not exist in the tree, returns an empty list.
     */
    fun collisions(rect: Rect, value: T): List<T> {
        val entries = findLocalEntries(value)

        val results = mutableListOf<T>()
        for (entry in entries) {
            if (collides(entry.rect, rect)) {
                results.add(entry.value)
            }
        }

        return results
    }

    private fun collides(rect: Rect, rect2: Rect): Boolean {
        // If one or more corners of 1 rect are inside the other, they overlap
        return ((rect.left in rect2.left .. rect2.right ||
                 rect.right in rect2.left .. rect2.right) &&
                (rect.top in rect2.top .. rect2.bottom ||
                 rect.bottom in rect2.top .. rect2.bottom)) ||

                ((rect2.left in rect.left .. rect.right ||
                  rect2.right in rect.left .. rect.right) &&
                 (rect2.top in rect.top .. rect.bottom ||
                  rect2.bottom in rect.top .. rect.bottom))
    }

    /**
     * Returns a list of any pre-inserted entries that value may overlap with.
     */
    fun findLocalEntries(value: T): List<Entry<T>> {
        val result = mutableListOf<Entry<T>>()
        valueToNode[value]?.let {
            findInner(it, result)
        }
        return result.filter { it.value != value }
    }

    private fun findInner(root: Node<T>, result: MutableList<Entry<T>>) {
        result.addAll(root.content)
        root.topLeft?.let { findInner(it, result) }
        root.topRight?.let { findInner(it, result) }
        root.bottomLeft?.let { findInner(it, result) }
        root.bottomRight?.let { findInner(it, result) }
    }

    /**
     * Adds a value with the specified bounding rectangle
     */
    fun add(rect: Rect, value: T) {
        addInner(root, entryOf(rect, value), 0.0, 0.0, width, height)
    }

    private fun addInner(root: Node<T>, entry: Entry<T>, x: Double, y: Double, width: Double, height: Double) {
        // Note: It's possible that objects could be partially
        // or entirely offscreen, so out of the quadtree bounds.
        // this is accounted for in checks below

        val halfWidth = width / 2
        val halfHeight = height / 2
        val xMid = x + halfWidth
        val yMid = y + halfHeight

        if (entry.rect.right < xMid && entry.rect.right >= x) {
            if (entry.rect.bottom < yMid && entry.rect.bottom >= y) {
                // Traverse topLeft sub region
                if (root.topLeft == null) {
                    root.topLeft = Node()
                }
                addInner(root.topLeft!!, entry, x, y, halfWidth, halfHeight)
            } else if (entry.rect.top >= yMid && entry.rect.top < y + height) {
                // Traverse bottomLeft sub region
                if (root.bottomLeft == null) {
                    root.bottomLeft = Node()
                }
                addInner(root.bottomLeft!!, entry, x, yMid, halfWidth, halfHeight)
            } else {
                // Cannot fit in sub regions, add entry here
                addEntry(root, entry)
            }
        } else if (entry.rect.left >= xMid && entry.rect.left < x + width) {
            if (entry.rect.bottom < yMid && entry.rect.bottom >= y) {
                // Traverse topRight sub region
                if (root.topRight == null) {
                    root.topRight = Node()
                }
                addInner(root.topRight!!, entry, xMid, y, halfWidth, halfHeight)
            } else if (entry.rect.top >= yMid && entry.rect.top < y + height) {
                // Traverse bottomRight sub region
                if (root.bottomRight == null) {
                    root.bottomRight = Node()
                }
                addInner(root.bottomRight!!, entry, xMid, yMid, halfWidth, halfHeight)
            } else {
                // Cannot fit in sub regions, add entry here
                addEntry(root, entry)
            }
        } else {
            // Cannot fit in sub regions, add entry here
            addEntry(root, entry)
        }
    }

    private fun addEntry(root: Node<T>, entry: Entry<T>) {
        root.content.add(entry)
        valueToNode[entry.value] = root
    }

    fun <T> entryOf(rect: Rect, value: T) = Entry(rect, value)
    class Entry<T>(val rect: Rect, val value: T)

    private class Node<T>(val content: MutableList<Entry<T>> = mutableListOf()) {
        var topLeft: Node<T>? = null
        var topRight: Node<T>? = null
        var bottomLeft: Node<T>? = null
        var bottomRight: Node<T>? = null
    }
}

