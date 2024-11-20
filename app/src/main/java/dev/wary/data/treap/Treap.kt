package dev.wary.data.treap

/**
 * A tree obeying the min heap property
 * Duplicate values are not allowed
 */
open class Treap<T>(private val comparator: Comparator<T>) where T : Comparable<T> {
    constructor() : this(compareBy<T> { it })

    fun clear() {
        count = 0
        root = null
    }

    private fun deleteLeafNode(parent: Node<T>?, node: Node<T>) {
        if (parent == null) {
            root = null
            return
        }

        if (comparator.compare(node.value, parent!!.value) < 0) {
            parent.left = null
        } else {
            parent.right = null
        }
    }

    fun delete(value: T): Boolean {
        val (parent, node) = search(root, value)

        // Value not found
        if (node == null) return false

        count--

        // Leaf node
        if (node.left == null && node.right == null) {
            deleteLeafNode(parent, node)

            return true
        }

        // Only right child
        if (node.left == null) {
            if (parent == null) {
                root = node.right
            } else if (comparator.compare(value, parent.value) < 0) {
                parent.left = node.right
            } else {
                parent.right = node.right
            }

            return true
        }

        // Only left child
        if (node.right == null) {
            if (parent == null) {
                root = node.left
            } else if (comparator.compare(value, parent.value) < 0) {
                parent.left = node.left
            } else {
                parent.right = node.left
            }

            return true
        }

        // Two children
        do {
            if (node.left!!.priority < node.right!!.priority) {
                rotateLeft(node)
            } else {
                rotateRight(node)
            }
        } while (node.left != null && node.right != null)
        deleteLeafNode(parent, node)

        return true
    }

    /**
     * Existing value that compares as equal is replaced,
     * otherwise inserts the new value
     * Returns true if a new value was inserted
     */
    fun upsert(value: T, replace: Boolean = false): Boolean {
        val (parent, node) = search(root, value)

        if (node != null) {
            if (!replace) return false

            // Replace existing node
            val newNode = node.copy(value = value).apply {
                left = node.left
                right = node.right
            }

            if (parent == null) {
                root = newNode
            } else {
                val comparison = comparator.compare(value, parent.value)
                if (comparison < 0) {
                    parent.left = newNode
                } else {
                    parent.right = newNode
                }
            }

            return false
        }

        // Insert new node
        val newNode = Node(value)

        if (parent == null) {
            root = newNode
        } else {
            val comparison = comparator.compare(value, parent.value)

            if (comparison < 0) {
                parent.left = newNode
                if (newNode.priority < parent.priority) {
                    rotateRight(parent)
                }
            } else {
                parent.right = newNode
                if (newNode.priority < parent.priority) {
                    rotateLeft(parent)
                }
            }

        }
        count++

        return true
    }

    fun search(value: T): Boolean {
        return search(root, value).second != null
    }

    /**
     * returns (parent, node)
     */
    private fun search(root: Node<T>?, value: T): Pair<Node<T>?, Node<T>?> {
        var prev: Node<T>? = null
        var ptr = root

        while (ptr != null) {
            val comparison = comparator.compare(value, ptr.value)

            when {
                comparison < 0 -> ptr = ptr.left
                comparison == 0 -> return Pair(prev, ptr)
                else -> {
                    prev = ptr
                    ptr = ptr.right
                }
            }
        }

        return Pair(prev, null)
    }

    /**
     *      root                       R
     *      / \      Left Rotate      / \
     *     L   R        ———>      root   Y
     *        / \                 / \
     *       X   Y               L   X
     *
     * returns new root
     */
    private fun rotateLeft(root: Node<T>?): Node<T>? {
        val r = root?.right
        val x = root?.right?.left

        r?.left = root
        root?.right = x

        return r
    }

    /**
     *            root                      L
     *            / \     Right Rotate     / \
     *           L   R        ———>        X   root
     *          / \                           / \
     *         X   Y                         Y   R
     */
    private fun rotateRight(root: Node<T>?): Node<T>? {
        val l = root?.left
        val y = root?.left?.right

        l?.right = root
        root?.left = y

        return l
    }

    val size: Int
        get() = count

    private var count: Int = 0
    private var root: Node<T>? = null

    companion object {
        fun <T : Comparable<T>> fromSorted(list: List<T>): Treap<T> {
            // TODO
            return Treap()
        }
    }

    inner class Iterator: MutableIterator<T> {
        private val stack = ArrayDeque<Node<T>>()
        var prev: Node<T>? = null

        init {
            pushAllLeft(root)
        }

        override fun hasNext(): Boolean {
            return stack.isNotEmpty()
        }

        override fun next(): T {
            prev = stack.removeFirst()

            prev!!.right?.let {
                pushAllLeft(it)
            }

            return prev!!.value
        }

        // TODO: Since we already have the node to delete, can we make this O(1)?
        // May require a parent ptr in the Node class. If parent is saved on the stack,
        // that node may have been deleted and the parent changed.
        override fun remove() {
            prev?.let {
                delete(it.value)
            }
        }

        private fun pushAllLeft(root: Node<T>?) {
            var ptr = root
            while (ptr != null) {
                stack.addFirst(ptr)
                ptr = ptr.left
            }
        }
    }

    data class Node<T>(val value: T, val priority: Int = value.hashCode()) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }
}

fun main() {
    val treap = Treap<Int>()

    assert(!treap.search(100))
    for (i in 0..10_000) {
        assert(treap.upsert(i))
    }
    assert(!treap.upsert(100))
    for (i in 0..5000) {
        assert(treap.delete(i))
    }
    assert(!treap.search(100))
    for (i in 5000 downTo 0) {
        assert(treap.upsert(i))
    }
    assert(treap.search(100))
}