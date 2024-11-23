package dev.wary.data.treap

interface Treap<T : Comparable<T>> {
    fun subTreap(fromInclusive: T? = null, toExclusive: T? = null): Treap<T>
    fun minOrNull(floor: T? = null): T?
    fun maxOrNull(ceil: T? = null): T?
    fun clear()
    // fun intersect(treap : Treap<T>): Treap<T>
    // fun union(treap: Treap<T>): Treap<T>
    fun delete(value: T): Boolean
    fun insert(value: T): Boolean
    fun find(value: T): Boolean

    val comparator: Comparator<T>
    val size: Int
}

class DelegateTreap<T : Comparable<T>>(
    private val treap: Treap<T>,
    private val fromInclusive: T?,
    private val toExclusive: T?
): Treap<T> {
    override val comparator: Comparator<T>
        get() = treap.comparator

    override val size: Int
        get() = TODO("Not yet implemented")

    override fun subTreap(fromInclusive: T?, toExclusive: T?): Treap<T> {
        if (comparator.compare(fromInclusive, toExclusive) > 0 ||
            (this.fromInclusive != null && comparator.compare(fromInclusive, this.fromInclusive) < 0) ||
            (this.toExclusive != null && comparator.compare(this.toExclusive, toExclusive) > 0))
        throw IllegalArgumentException("Invalid range")

        return DelegateTreap(
            treap,
            fromInclusive ?: this.fromInclusive,
            toExclusive ?: this.toExclusive
        )
    }

    override fun minOrNull(floor: T?): T? {
        if (comparator.compare(floor, fromInclusive) >= 0) {
            return treap.minOrNull(floor)
        }
        return treap.minOrNull(fromInclusive)
    }

    override fun maxOrNull(ceil: T?): T? {
        if (comparator.compare(ceil, fromInclusive) < 0) {
            return treap.maxOrNull(ceil)
        }
        return treap.maxOrNull(toExclusive)
    }

    override fun clear() {
        throw UnsupportedOperationException("Cannot delete outside range.")
    }

    override fun delete(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            throw IllegalArgumentException("Cannot delete outside range.")
        }
        return treap.insert(value)
    }

    override fun insert(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            throw IllegalArgumentException("Cannot insert outside range.")
        }
        return treap.insert(value)
    }

    override fun find(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            return false
        }
        return treap.find(value)
    }
}

/**
 * A tree obeying the max heap property
 * Duplicate values are not allowed
 */
open class BaseTreap<T : Comparable<T>>(
    override val comparator: Comparator<T> = compareBy { it }
) : Treap<T> {
    constructor(treap: Treap<T>): this(treap.comparator) {
        // TODO
    }

    /**
     * Creates a tree backed by this tree's data
     * subtreap size is lazy loaded for perf
     * Returns a subtree backed by this one
     */
    override fun subTreap(fromInclusive: T?, toExclusive: T?): Treap<T> {
        if (comparator.compare(fromInclusive, toExclusive) > 0)
            throw IllegalArgumentException("Invalid range")
        return DelegateTreap(this, fromInclusive, toExclusive)
    }

    override fun minOrNull(floor: T?): T? {
        var ptr = root

        while (ptr?.left != null &&
            (floor == null || comparator.compare(ptr.left!!.value, floor) >= 0)) {
            ptr = ptr.left
        }

        return ptr?.value
    }

    override fun maxOrNull(ceil: T?): T? {
        var ptr = root

        while (ptr?.right != null &&
            (ceil == null || comparator.compare(ptr.right!!.value, ceil) < 0)) {
            ptr = ptr.right
        }

        return ptr?.value
    }

    override fun clear() {
        count = 0
        root = null
    }

    private fun split(value: T): SplitResult<T> {
        upsert(value, Integer.MAX_VALUE, true)

        return SplitResult(root?.left, root, root?.right)
    }

    private fun join(splitResult: SplitResult<T>) {
        // TODO
    }

    public fun union(elements: Collection<T>): BaseTreap<T> {
        val treap = BaseTreap(this)

        for (element in elements) {
            treap
        }
    }

    /**
     * Finds the interesction of this treap and elements
     * returns the interesction as a new treap
     */
    public fun intersect(elements: Collection<T>): BaseTreap<T> {
        val treap = BaseTreap(comparator)

        for (element in elements) {
            if (find(element)) {
                treap.insert(element)
            }
        }

        return treap
    }

    /**
     * Replaces this treap with the interesction of it and elements
     * returns whether this treap has changed
     */
    fun toIntersect(elements: Collection<T>): Boolean {
        val treap = intersect(elements)

        if (treap.size != count) {
            count = treap.size
            root = treap.root
            return true
        }

        return false
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

    override fun delete(value: T): Boolean {
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
            if (node.left!!.priority > node.right!!.priority) {
                rotateLeft(node)
            } else {
                rotateRight(node)
            }
        } while (node.left != null && node.right != null)
        deleteLeafNode(parent, node)

        return true
    }

    override fun insert(value: T): Boolean {
        return upsert(value = value)
    }

    /**
     * Existing value that compares as equal is replaced,
     * otherwise inserts the new value
     * Returns true if a new value was inserted
     */
    private fun upsert(value: T, priority: Int = value.hashCode(), replace: Boolean = false): Boolean {
        val (parent, node) = search(root, value)

        if (node != null) {
            if (!replace) return false

            if (priority == node.priority) {
                // Replace existing node
                val newNode = node.copy(value = value).apply {
                    left = node.left
                    right = node.right
                }

                if (parent == null) {
                    root = newNode
                } else {
                    if (comparator.compare(value, parent.value) < 0) {
                        parent.left = newNode
                    } else {
                        parent.right = newNode
                    }
                }
            } else {
                delete(value)
                upsert(value, priority, true)
            }

            return false
        }

        // Insert new node
        val newNode = Node(value)

        if (parent == null) {
            root = newNode
        } else {
            if (comparator.compare(value, parent.value) < 0) {
                parent.left = newNode
                if (newNode.priority > parent.priority) {
                    rotateRight(parent)
                }
            } else {
                parent.right = newNode
                if (newNode.priority > parent.priority) {
                    rotateLeft(parent)
                }
            }

        }
        count++

        return true
    }

    override fun find(value: T): Boolean {
        return search(root, value).second != null
    }

    /**
     * returns (parent, node)
     */
    protected fun search(root: Node<T>?, value: T): Pair<Node<T>?, Node<T>?> {
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
     * Standard AVL Left Rotate:
     *
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
     * Standard AVL Right Rotate:
     *
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

    /**
     * Performs DFS traversal of tree
     * Returns the number of nodes in the tree
     */
    private fun countNodes(root: Node<T>?): Int {
        if (root == null) return 0

        var count = 0
        val stack = ArrayDeque<Node<T>>()
        stack.addFirst(root)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()
            count++
            node.left?.let { stack.addFirst(it) }
            node.right?.let { stack.addFirst(it) }
        }

        return count
    }

    override val size: Int
        get() {
            if (count < 0) {
                count = countNodes(root)
            }
            return count
        }

    private var count: Int = -1
    protected var root: Node<T>? = null

    companion object {
        fun <T : Comparable<T>> from(
            elements: Collection<T>,
            comparator: Comparator<T> = compareBy { it }
        ): Treap<T> {
            return BaseTreap(comparator).apply {
                for (element in elements) {
                    insert(element)
                }
            }
        }
    }

    inner class Iterator(
        val isDescending: Boolean,
        val fromInclusive: T? = null,
        val toExclusive: T? = null
    ): MutableIterator<T> {
        private val stack = ArrayDeque<Node<T>>()
        var current: Node<T>? = null

        init {
            if (isDescending) pushAllRight(root) else pushAllLeft(root)
        }

        override fun hasNext(): Boolean {
            return stack.isNotEmpty()
        }

        override fun next(): T {
            current = stack.removeFirst()

            if (isDescending) {
                current!!.left?.let {
                    pushAllRight(root)
                }
            } else {
                current!!.right?.let {
                    pushAllLeft(root)
                }
            }

            return current!!.value
        }

        // TODO test this. Make sure rotations during deletion don't screw up iteration order
        // TODO: Since we already have the node to delete, can we make this O(1) or
        // will that require a parent ptr in the Node class? If parent is saved on the stack,
        // that node may have been deleted and the parent changed.
        override fun remove() {
            current?.let {
                delete(it.value)
            }
        }

        private fun pushAllLeft(root: Node<T>?) {
            var ptr = root
            while (ptr != null &&
                (fromInclusive == null || comparator.compare(fromInclusive, ptr.value) <= 0)) {
                stack.addFirst(ptr)
                ptr = ptr.left
            }
        }

        private fun pushAllRight(root: Node<T>?) {
            var ptr = root
            while (ptr != null && comparator.compare(ptr.value, toExclusive) < 0) {
                stack.addFirst(ptr)
                ptr = ptr.right
            }
        }
    }

    class SplitResult<T>(lesser: Node<T>?, pivot: Node<T>?, greater: Node<T>?)

    data class Node<T>(val value: T, val priority: Int = value.hashCode()) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }
}

fun main() {
    val treap = BaseTreap<Int>()

    assert(!treap.find(100))
    for (i in 0..10_000) {
        assert(treap.insert(i))
    }
    assert(!treap.insert(100))
    for (i in 0..5000) {
        assert(treap.delete(i))
    }
    assert(!treap.find(100))
    for (i in 5000 downTo 0) {
        assert(treap.insert(i))
    }
    assert(treap.find(100))
}