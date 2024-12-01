package dev.wary.data.treap

import kotlin.random.Random

/**
 * A BST with priorities obeying the max heap property
 * Duplicate values are not allowed
 */
open class TreapSet<T>(
    override val comparator: Comparator<in T> = compareBy { it.hashCode() }
): Treap<T> {
    fun copy(): TreapSet<T> {
        val treap = TreapSet(comparator)
        val iterator = iterator()

        while (iterator.hasNext()) {
            treap.add(iterator.next())
        }

        return treap
    }

    private fun addUpdateListener(listener: UpdateListener<T>) {
        updateListeners.add(listener)
    }

    override fun headSet(toExclusive: T): Treap<T> {
        return subSet(null, toExclusive)
    }

    override fun tailSet(fromInclusive: T): Treap<T> {
        return subSet(fromInclusive, null)
    }

    /**
     * Creates a tree backed by this tree's data
     * subtreap size is lazy loaded for perf
     * Returns a subtree backed by this one
     */
    override fun subSet(fromInclusive: T?, toExclusive: T?): Treap<T> {
        if (fromInclusive != null && toExclusive != null && comparator.compare(fromInclusive, toExclusive) > 0)
            throw IllegalArgumentException("Invalid range")
        val subTreap = DelegateTreap(this, fromInclusive, toExclusive)

        addUpdateListener(subTreap)

        return subTreap
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

        for (listener in updateListeners) {
            listener.onClear()
        }
    }

    override fun first(): T {
        minOrNull()?.let {
            return it
        }
        throw NoSuchElementException()
    }

    override fun last(): T {
        maxOrNull()?.let {
            return it
        }
        throw NoSuchElementException()
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val oldSize = size
        val set = elements.toSet()
        val iterator = iterator()

        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element in set) {
                iterator.remove()
            }
        }

        return size != oldSize
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val oldSize = size
        val set = elements.toSet()
        val iterator = iterator()

        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element !in set) {
                iterator.remove()
            }
        }

        return size != oldSize
    }

    override fun iterator(): MutableIterator<T> {
        return Iterator()
    }

    override fun iteratorDescending(): MutableIterator<T> {
        return Iterator(isDescending = true)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var result = false

        for (element in elements) {
            result = result or add(element)
        }

        return result
    }

    override fun isEmpty(): Boolean {
       return size == 0
    }

    // TODO add ability to split treap into 2
//    private fun split(value: T): SplitResult<T> {
//        upsert(value, Integer.MAX_VALUE, true)
//
//        return SplitResult(root?.left, root, root?.right)
//    }

    override fun union(elements: Collection<T>): Treap<T> {
        val treap = copy()

        for (element in elements) {
            treap.add(element)
        }

        return treap
    }

    /**
     * Finds the interesction of this treap and elements
     * returns the interesction as a new treap
     */
    override fun intersect(elements: Collection<T>): Treap<T> {
        val treap = TreapSet(comparator)

        val set = elements.toSet()
        val iterator = iterator()

        while (iterator.hasNext()) {
            val v = iterator.next()
            if (v in set) {
                treap.add(v)
            }
        }

        return treap
    }

    private fun deleteLeafNode(parent: Node<T>?, node: Node<T>) {
        if (parent == null) {
            root = null
            return
        }

        if (comparator.compare(node.value, parent.value) < 0) {
            parent.left = null
        } else {
            parent.right = null
        }
    }

    override fun remove(element: T): Boolean {
        val (parent, node) = search(root, element)

        // Value not found
        if (node == null) return false

        // Two children
        var prev = parent
        while (node.left != null && node.right != null) {
            if (node.left!!.priority > node.right!!.priority) {
                if (prev == null) {
                    root = rotateRight(node)
                    prev = root
                } else {
                    if (comparator.compare(node.value, prev.value) < 0) {
                        prev.left = rotateRight(node)
                        prev = prev.left
                    } else {
                        prev.right = rotateRight(node)
                        prev = prev.right
                    }
                }
            } else {
                if (prev == null) {
                    root = rotateLeft(node)
                    prev = root
                } else {
                    if (comparator.compare(node.value, prev.value) < 0) {
                        prev.left = rotateLeft(node)
                        prev = prev.left
                    } else {
                        prev.right = rotateLeft(node)
                        prev = prev.right
                    }
                }
            }
        }

        if (node.left == null && node.right == null) {
            // Leaf node
            deleteLeafNode(prev, node)
        } else if (node.right == null) {
            // Only left child
            if (prev == null) {
                root = node.left
            } else if (comparator.compare(node.value, prev.value) < 0) {
                prev.left = node.left
            } else {
                prev.right = node.left
            }
        } else {
            // Only right child
            if (prev == null) {
                root = node.right
            } else if (comparator.compare(node.value, prev.value) < 0) {
                prev.left = node.right
            } else {
                prev.right = node.right
            }
        }

        count--

        for (listener in updateListeners) {
            listener.onDelete(element)
        }

        return true
    }

    override fun add(element: T): Boolean {
        if (!insertNewValue(element)) {
            if (remove(element)) {
                return insertNewValue(element)
            }
            return false
        }
        return true
    }

    /**
     * Inserts value if it is not already in the treap
     * Returns true if a new value was inserted
     */
    private fun insertNewValue(value: T, priority: Int = Random.nextInt()): Boolean {
        val stack = ArrayDeque<Node<T>?>()
        stack.add(root)

        var prev: Node<T>? = Node(value, priority)
        while (stack.first() != null) {
            val node = stack.first()!!
            val comparison = comparator.compare(value, node.value)

            when {
                comparison < 0 -> {
                    stack.addFirst(node.left)
                }
                comparison > 0 -> {
                    stack.addFirst(node.right)
                }
                else -> {
                    return false
                }
            }
        }
        stack.removeFirst()

        while(stack.isNotEmpty()) {
            val node = stack.removeFirst()!!

            if (comparator.compare(prev?.value, node.value) < 0) {
                node.left = prev
                if (node.left!!.priority > node.priority) {
                    prev = rotateRight(node)
                } else {
                    prev = node
                }
            } else {
                node.right = prev
                if (node.right!!.priority > node.priority) {
                    prev = rotateLeft(node)
                } else {
                    prev = node
                }
            }
        }
        root = prev
        count++

        for (listener in updateListeners) {
            listener.onInsert(value)
        }

        return true
    }

    override fun contains(element: T): Boolean {
        return search(root, element).second != null
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
                comparison < 0 -> {
                    prev = ptr
                    ptr = ptr.left
                }
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
     *
     * returns new root
     */
    private fun rotateRight(root: Node<T>?): Node<T>? {
        val l = root?.left
        val y = root?.left?.right

        l?.right = root
        root?.left = y

        return l
    }

    override val size: Int
        get() = count

    override fun containsAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (!contains(element)) return false
        }
        return true
    }

    private var updateListeners = mutableListOf<UpdateListener<T>>()
    private var count: Int = 0
    private var root: Node<T>? = null

    companion object {
        fun <T> from(
            elements: Collection<T>,
            comparator: Comparator<T> = compareBy { it.hashCode() }
        ): Treap<T> {
            return TreapSet(comparator).apply {
                for (element in elements) {
                    add(element)
                }
            }
        }
    }

    inner class Iterator(
        private val isDescending: Boolean = false,
        private val fromInclusive: T? = null,
        private val toExclusive: T? = null
    ): MutableIterator<T> {
        private val stack = ArrayDeque<Node<T>>()
        var current: Node<T>? = null
        var next: Node<T>? = null

        init {
            if (isDescending) pushAllRight(root) else pushAllLeft(root)
            next = nextValidNode()
        }

        override fun hasNext(): Boolean {
            return next != null
        }

        override fun next(): T {
            if (next == null) throw NoSuchElementException()

            current = next
            next = nextValidNode()

            return current!!.value
        }

        private fun nextValidNode(): Node<T>? {
            var node: Node<T>?

            do {
                node = nextNode()
            } while (node != null && ((fromInclusive != null && comparator.compare(node.value, fromInclusive) < 0) ||
                        (toExclusive != null && comparator.compare(node.value, toExclusive) >= 0)))

            return node
        }

        private fun nextNode(): Node<T>? {
            if (stack.isEmpty()) return null

            val result = stack.removeFirst()

            if (isDescending) {
                result.left?.let {
                    pushAllRight(it)
                }
            } else {
                result.right?.let {
                    pushAllLeft(it)
                }
            }

            return result
        }

        override fun remove() {
            current?.let {
                remove(it.value)
            }
        }

        private fun pushAllLeft(root: Node<T>?) {
            var ptr = root
            while (ptr != null) {
                stack.addFirst(ptr)
                ptr = ptr.left
            }
        }

        private fun pushAllRight(root: Node<T>?) {
            var ptr = root
            while (ptr != null) {
                stack.addFirst(ptr)
                ptr = ptr.right
            }
        }
    }

    // class SplitResult<T>(lesser: Node<T>?, pivot: Node<T>?, greater: Node<T>?)

    interface UpdateListener<T> {
        fun onInsert(value: T)
        fun onDelete(value: T)
        fun onClear()
    }

    data class Node<T>(val value: T, val priority: Int = Random.nextInt()) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }
}