package dev.wary.data.treap

class DelegateTreap<T : Comparable<T>> internal constructor(
    private val treap: TreapSet<T>,
    private val fromInclusive: T?,
    private val toExclusive: T?
): Treap<T>, TreapSet.UpdateListener<T> {
    override fun subTreap(fromInclusive: T?, toExclusive: T?): Treap<T> {
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

    override val comparator: Comparator<T>
        get() = treap.comparator

    override val size: Int
        get() {
            if (count < 0) {
                count = countNodes()
            }
            return count
        }

    private var count = -1
}