package dev.wary.data.treap

class DelegateTreap<T> internal constructor(
    private val treap: TreapSet<T>,
    private val fromInclusive: T?,
    private val toExclusive: T?
): Treap<T>, TreapSet.UpdateListener<T> {
    override fun subSet(fromInclusive: T?, toExclusive: T?): Treap<T> {
        if (comparator.compare(fromInclusive, toExclusive) > 0 ||
            (this.fromInclusive != null && comparator.compare(
                fromInclusive,
                this.fromInclusive
            ) < 0) ||
            (this.toExclusive != null && comparator.compare(this.toExclusive, toExclusive) > 0)
        ) {
            throw IllegalArgumentException("Invalid range")
        }

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

    override fun first(): T {
        treap.minOrNull(floor = fromInclusive)?.let {
            return it
        }
        throw NoSuchElementException()
    }

    override fun last(): T {
        treap.maxOrNull(ceil = toExclusive)?.let {
            return it
        }
        throw NoSuchElementException()
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (comparator.compare(element, fromInclusive) < 0 || comparator.compare(element, toExclusive) >= 0)
                throw UnsupportedOperationException("Cannot delete outside range.")
        }
        return treap.removeAll(elements)
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

    override fun headSet(toExclusive: T): SortedSet<T> {
        return subSet(null, toExclusive)
    }

    override fun tailSet(fromInclusive: T): SortedSet<T> {
        return subSet(fromInclusive, null)
    }

    override fun iterator(): MutableIterator<T> {
        return treap.Iterator(false, fromInclusive, toExclusive)
    }

    override fun iteratorDescending(): MutableIterator<T> {
        return treap.Iterator(true, fromInclusive, toExclusive)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (comparator.compare(element, fromInclusive) < 0 || comparator.compare(element, toExclusive) >= 0)
                throw UnsupportedOperationException("Cannot add outside range.")
        }
        return treap.addAll(elements)
    }

    override fun union(elements: Collection<T>): Treap<T> {
        val result = TreapSet<T>()

        val iter = iterator()
        while (iter.hasNext()) {
            result.add(iter.next())
        }

        for (element in elements) {
            result.add(element)
        }

        return result
    }

    override fun intersect(elements: Collection<T>): Treap<T> {
        val result = TreapSet<T>()

        val iter = iterator()
        while (iter.hasNext()) {
            result.add(iter.next())
        }

        for (element in elements) {
            if (contains(element)) {
                result.add(element)
            }
        }

        return result
    }

    override fun remove(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            throw IllegalArgumentException("Cannot delete outside range.")
        }
        if (treap.remove(value)) {
            if (count >= 0) count--
            return true
        }
        return false
    }

    override fun add(value: T): Boolean {
        if ((fromInclusive != null && comparator.compare(value, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(value, toExclusive) >= 0)) {
            throw IllegalArgumentException("Cannot insert outside range.")
        }
        if (treap.add(value)) {
            if (count >= 0) count++
            return true
        }
        return false
    }

    override fun contains(element: T): Boolean {
        if ((fromInclusive != null && comparator.compare(element, fromInclusive) < 0) ||
            (toExclusive != null && comparator.compare(element, toExclusive) >= 0)) {
            return false
        }
        return treap.contains(element)
    }

    private fun countNodes(): Int {
        var result = 0

        val iter = iterator()
        while (iter.hasNext()) {
            iter.next()
            result++
        }

        return result
    }

    override fun onInsert(value: T) {
        if (count >= 0 &&
            (fromInclusive == null || comparator.compare(value, fromInclusive) >= 0) &&
            (toExclusive == null || comparator.compare(value, toExclusive) < 0)) {
            count++
        }
    }

    override fun onDelete(value: T) {
        if (count >= 0 &&
            (fromInclusive == null || comparator.compare(value, fromInclusive) >= 0) &&
            (toExclusive == null || comparator.compare(value, toExclusive) < 0)) {
                count--
            }
    }

    override fun onClear() {
        count = 0
    }

    override val comparator: Comparator<in T>
        get() = treap.comparator

    override val size: Int
        get() {
            if (count < 0) {
                count = countNodes()
            }
            return count
        }

    override fun containsAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (!contains(element)) return false
        }
        return true
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    private var count = -1
}