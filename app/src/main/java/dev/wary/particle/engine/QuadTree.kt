package dev.wary.particle.engine

class QuadTree<T>(val width: Double, val height: Double) {
    val root: Node<T> = Node()
    // val map = mutableMapOf<T, MutableSet<Node<T>>>()

//    fun add(entry: Entry<Rect>) {
//        addInner(root, Point(rect.top, rect.left), rect, 0.0, 0.0, width, height)
//        addInner(root, Point(rect.top, rect.right), rect, 0.0, 0.0, width, height)
//        addInner(root, Point(rect.bottom, rect.left), rect, 0.0, 0.0, width, height)
//        addInner(root, Point(rect.bottom, rect.right), rect, 0.0, 0.0, width, height)
//    }

    /**
     * Returns the node containing this point, or null if it is not in the QuadTree
     */
    fun find(point: Point): Node<T>? = findInner(root, point, 0.0, 0.0, width, height)

    private fun findInner(root: Node<T>, point: Point, x: Double, y: Double, width: Double, height: Double): Node<T>? {
        if (root.isLeaf) {
            for (entry in root.content) {
                if (entry.point == point) {
                    return root
                }
            }
            return null
        } else {
            val xMid = x + width / 2
            val yMid = y + height / 2
            if (point.x < xMid) {
                if (point.y < yMid) {
                    return findInner(root.topLeft!!, point, x, y, xMid, yMid)
                } else {
                    return findInner(root.bottomLeft!!, point, x, yMid, xMid, y + height)
                }
            } else {
                if (point.y < yMid) {
                    return findInner(root.topRight!!, point, xMid, y, x + width, yMid)
                } else {
                    return findInner(root.bottomRight!!, point, xMid, yMid, x + width, y + height)
                }
            }
        }
    }

    fun add(point: Point, value: T) {
        addInner(root, entryOf(point, value), 0.0, 0.0, width, height)
    }

    private fun splitNode(root: Node<T>, x: Double, y: Double, width: Double, height: Double) {
        root.isLeaf = false

        root.topLeft = Node()
        root.topRight = Node()
        root.bottomLeft = Node()
        root.bottomRight = Node()

        for (entry in root.content) {
            // map[entry.data]?.remove(root)
            addInner(root, entry, x, y, width, height)
        }
        root.content.clear()
    }

    private fun addInner(root: Node<T>, entry: Entry<T>, x: Double, y: Double, width: Double, height: Double) {
        if (root.isLeaf) {
            root.content.add(entry)
            if (root.content.size > 2 && width > 1 && height > 1) {
                splitNode(root, x, y, width, height)
            } else {
                // val set = map.getOrPut(entry.data) { mutableSetOf() }
                // set.add(root)
            }
        } else {
            val xMid = x + width / 2
            val yMid = y + height / 2
            if (entry.point.x < xMid) {
                if (entry.point.y < yMid) {
                    addInner(root.topLeft!!, entry, x, y, xMid, yMid)
                } else {
                    addInner(root.bottomLeft!!, entry, x, yMid, xMid, y + height)
                }
            } else {
                if (entry.point.y < yMid) {
                    addInner(root.topRight!!, entry, xMid, y, x + width, yMid)
                } else {
                    addInner(root.bottomRight!!, entry, xMid, yMid, x + width, y + height)
                }
            }
        }
    }

    fun <T> entryOf(point: Point, value: T) = Entry(point, value)

    class Entry<T>(val point: Point, val data: T)

    class Node<T>(val content: MutableList<Entry<T>> = mutableListOf(), var isLeaf: Boolean = true) {
        var topLeft: Node<T>? = null
        var topRight: Node<T>? = null
        var bottomLeft: Node<T>? = null
        var bottomRight: Node<T>? = null
    }
}

