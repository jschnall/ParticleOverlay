package dev.wary.data.treap

import dev.wary.data.treap.TreapSet.Node
import kotlin.random.Random

interface Treap<T : Comparable<T>> {
    fun subTreap(fromInclusive: T? = null, toExclusive: T? = null): Treap<T>
    fun minOrNull(floor: T? = null): T?
    fun maxOrNull(ceil: T? = null): T?
    fun clear()
    fun iterator(): MutableIterator<T>
    fun iteratorDescending(): MutableIterator<T>
    fun intersect(elements : Collection<T>): Treap<T>
    fun union(elements: Collection<T>): Treap<T>
    fun delete(value: T): Boolean
    fun insert(value: T): Boolean
    fun find(value: T): Boolean

    val comparator: Comparator<T>
    val size: Int
}

class DelegateTreap<T : Comparable<T>>(
    private val treap: TreapSet<T>,
    private val fromInclusive: T?,
    private val toExclusive: T?
): Treap<T> {
    override val comparator: Comparator<T>
        get() = treap.comparator

    override val size: Int
        get() {
            if (count < 0) {
                count = countNodes()
            }
            return count
        }

    override fun subTreap(fromInclusive: T?, toExclusive: T?): Treap<T> {
        if (comparator.compare(fromInclusive, toExclusive) > 0 ||
            (this.fromInclusive != null && comparator.compare(
                fromInclusive,
                this.fromInclusive
            ) < 0) ||
            (this.toExclusive != null && comparator.compare(this.toExclusive, toExclusive) > 0)
        )
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

    override fun iterator(): MutableIterator<T> {
        return treap.Iterator(false, fromInclusive, toExclusive)
    }

    override fun iteratorDescending(): MutableIterator<T> {
        return treap.Iterator(true, fromInclusive, toExclusive)
    }

    override fun union(elements: Collection<T>): Treap<T> {
        val result = TreapSet<T>()

        val iter = iterator()
        while (iter.hasNext()) {
            result.insert(iter.next())
        }

        for (element in elements) {
            result.insert(element)
        }

        return result
    }

    override fun intersect(elements: Collection<T>): Treap<T> {
        val result = TreapSet<T>()

        val iter = iterator()
        while (iter.hasNext()) {
            result.insert(iter.next())
        }

        for (element in elements) {
            if (find(element)) {
                result.insert(element)
            }
        }

        return result
    }

    override fun delete(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            throw IllegalArgumentException("Cannot delete outside range.")
        }
        if (treap.delete(value)) {
            if (count >= 0) count--
            return true
        }
        return false
    }

    override fun insert(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            throw IllegalArgumentException("Cannot insert outside range.")
        }
        if (treap.insert(value)) {
            if (count >= 0) count++
            return true
        }
        return false
    }

    override fun find(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            return false
        }
        return treap.find(value)
    }

    private fun countNodes(): Int {
        var result = 0

        val iter = treap.iterator()
        while (iter.hasNext()) {
            iter.next()
            result++
        }

        return result
    }

    private var count = -1
}

/**
 * A tree obeying the max heap property
 * Duplicate values are not allowed
 */
open class TreapSet<T : Comparable<T>>(
    override val comparator: Comparator<T> = compareBy { it }
) : Treap<T> {
    constructor(treap: Treap<T>): this(treap.comparator) {
        val iter = treap.iterator()

        while (iter.hasNext()) {
            insert(iter.next())
        }
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

    override fun iterator(): MutableIterator<T> {
        return Iterator()
    }

    override fun iteratorDescending(): MutableIterator<T> {
       return Iterator(isDescending = true)
    }

    // TODO add ability to split treap into 2
//    private fun split(value: T): SplitResult<T> {
//        upsert(value, Integer.MAX_VALUE, true)
//
//        return SplitResult(root?.left, root, root?.right)
//    }

    override fun union(elements: Collection<T>): TreapSet<T> {
        val treap = TreapSet(this)

        for (element in elements) {
            treap.insert(element)
        }

        return treap
    }

    /**
     * Finds the interesction of this treap and elements
     * returns the interesction as a new treap
     */
    override fun intersect(elements: Collection<T>): TreapSet<T> {
        val treap = TreapSet(comparator)

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

        if (comparator.compare(node.value, parent.value) < 0) {
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

        // Two children
        var ptr: Node<T> = node
        var prev = parent
        while (ptr.left != null && ptr.right != null) {
            if (ptr.left!!.priority > ptr.right!!.priority) {
                if (prev == null) {
                    root = rotateRight(ptr)
                    prev = root
                } else {
                    if (comparator.compare(value, prev.value) < 0) prev.left = rotateRight(ptr) else prev.right = rotateRight(ptr)
                    prev = ptr
                }
                ptr = ptr.right!!
            } else {
                if (prev == null) {
                    root = rotateLeft(ptr)
                    prev = root
                } else {
                    if (comparator.compare(value, prev.value) < 0) prev.left = rotateLeft(ptr) else prev.right = rotateLeft(ptr)
                    prev = ptr
                }
                ptr = ptr.left!!
            }
        }

        // Only left child
        if (node.right == null) {
            if (prev == null) {
                root = node.left
            } else if (comparator.compare(value, prev.value) < 0) {
                prev.left = node.left
            } else {
                prev.right = node.left
            }

            return true
        }

        // Only right child
        if (node.left == null) {
            if (prev == null) {
                root = node.right
            } else if (comparator.compare(value, prev.value) < 0) {
                prev.left = node.right
            } else {
                prev.right = node.right
            }

            return true
        }

        // Leaf node
        deleteLeafNode(prev, node)
        return true
    }

    override fun insert(value: T): Boolean {
        if (!insertNewValue(value)) {
            if (delete(value)) {
                return insertNewValue(value)
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

    private var count: Int = 0
    protected var root: Node<T>? = null

    companion object {
        fun <T : Comparable<T>> from(
            elements: Collection<T>,
            comparator: Comparator<T> = compareBy { it }
        ): Treap<T> {
            return TreapSet(comparator).apply {
                for (element in elements) {
                    insert(element)
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
                    pushAllRight(it)
                }
            } else {
                current!!.right?.let {
                    pushAllLeft(it)
                }
            }

            return current!!.value
        }

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
            while (ptr != null &&
                (toExclusive == null || comparator.compare(ptr.value, toExclusive) < 0)) {
                stack.addFirst(ptr)
                ptr = ptr.right
            }
        }
    }

    class SplitResult<T>(lesser: Node<T>?, pivot: Node<T>?, greater: Node<T>?)

    data class Node<T>(val value: T, val priority: Int = Random.nextInt()) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }
}


fun main() {
//    testDelete()
    testIterator()
//    testIterator(isDescending)
}

fun testDelete() {
    val treap = TreapSet<Int>()

    for (i in 1 .. 10) {
        treap.insert(i)
    }
    treap.delete(5)
    treap.delete(5)
    treap.delete(1)
    treap.delete(0)
    treap.delete(5)
    println(treap.size)

    printTreap(treap)
}

fun <T: Comparable<T>> printTreap(treap: TreapSet<T>, isDescending: Boolean = false) {
    val iter = treap.Iterator(isDescending)
    print("[")
    if (iter.hasNext()) print(iter.next())
    while (iter.hasNext()) {
        print(", ${iter.next()}")
    }
    println("]")
}

fun testIterator(isDescending: Boolean = false) {
    val treap = TreapSet<Int>()

    for (i in 1 .. 10) {
        treap.insert(i)
    }
    printTreap(treap, isDescending)

    val iter = treap.Iterator(isDescending)
    iter.remove()
    println(treap.size)
    while (iter.hasNext()) {
        iter.next()
        iter.remove()
        println(treap.size)
    }
}